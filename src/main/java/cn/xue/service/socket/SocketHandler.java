package cn.xue.service.socket;

import cn.xue.common.Constants;
import cn.xue.model.base.PushModel;
import cn.xue.model.diy.MessageType;
import cn.xue.model.diy.SystemNotifyType;
import cn.xue.model.socket.Message;
import cn.xue.model.user.User;
import cn.xue.service.base.PushService;
import cn.xue.service.redis.Receiver;
import cn.xue.service.redis.RedisService;
import cn.xue.service.user.UserService;
import com.alibaba.fastjson.JSONObject;
import com.gexin.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class SocketHandler implements WebSocketHandler {
    @Autowired
    private RedisService redisService;
    @Autowired
    private UserService userService;
    @Autowired
    private PushService pushService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private RedisMessageListenerContainer redisMessageListenerContainer;

    private static final Logger logger = Logger.getLogger(SocketHandler.class);
    //在线用户列表
    private static final Map<Long, Receiver> users;
    static {
        users = new ConcurrentHashMap<>();
    }
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Object user = session.getAttributes().get(Constants.WEBSOCKET_USER);
        if (user != null) {
            if(user instanceof User) {
                Receiver receiver = new Receiver();
                receiver.setSession(session);
                //设置订阅topic
                redisMessageListenerContainer.
                        addMessageListener(receiver,
                                new PatternTopic(Constants.WEBSOCKET_MESSAGE + "*:" + ((User)user).getId()));
                if(users.containsKey(((User)user).getId())) {
                    users.get(((User)user).getId()).getSession().close();
                }
                users.put(((User)user).getId(), receiver);
                redisService.set(Constants.WEBSOCKET_USER + ((User)user).getId(), true);
                sendNoReadMessage(((User)user).getId(), session);
            }
            //session.sendMessage(new TextMessage("成功建立socket连接"));
            logger.info(user + "成功连接！");
        }
        logger.info("当前在线人数："+users.size());
    }
    //用户上线时将所有未发送消息发送给用户
    public void sendNoReadMessage(Long id, WebSocketSession session) {
        try {
            List<Message> messages = new ArrayList<>();
            String pattern = Constants.WEBSOCKET_MESSAGE + "*:" + String.valueOf(id);
            Set<String> keys = redisService.getAllKeyByPattern(pattern);
            for (String key : keys) {
                Message msg = null;
                while((msg = (Message) redisService.leftPop(key)) != null) {
                    messages.add(msg);
                }
            }
            messages.addAll(messageService.getNoReadMessagesFromDB(id));
            for (Message m : messages) {
                if(!m.isAlreadySent()) {
                    session.sendMessage(new TextMessage(JSONObject.toJSONString(m)));
                }
            }
            for(Message m : messages) {
                m.setAlreadySent(true);
            }
            messageService.insertMessages(messages);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
    //接收socket信息
    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        try{
            if(webSocketMessage instanceof TextMessage) {
                try {
                    if(JSONObject.isValid(((TextMessage) webSocketMessage).getPayload())) {
                        Message msg = JSONObject.parseObject(((TextMessage) webSocketMessage).getPayload(), Message.class);
                        logger.debug("收到用户消息：" + msg);
                        msg.setCreatedAt(new Date());
                        sendMessageToUser(msg);
                    } else {
                        logger.debug("收到其他消息：" + ((TextMessage) webSocketMessage).getPayload());
                    }
                } catch (Exception e) {
                    logger.debug("收到其他消息：" + ((TextMessage) webSocketMessage).getPayload());
                }
            } else {
                logger.debug("收到其他消息：" + webSocketMessage.getPayload());
            }
        }catch(Exception e){
            logger.error("消息处理失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
    @Async
    public void sendPushNotifyToUser(User user, Message message) {
        if(message.getType().equals(MessageType.text)) {
            User from = userService.getUserById(message.getSrcUserId());
            User to = userService.getUserById(message.getDesUserId());
            String content = message.getContent();
            if(StringUtils.isEmpty(content)) {
                switch (message.getType()) {
                    case video:
                        content = "[视频]";
                        break;
                    case audio:
                        content = "[语音]";
                        break;
                    case picture:
                        content = "[图片]";
                        break;
                }
            }
            PushModel pushModel = new PushModel(from.getNickName() + "给您发了消息",
                    content, JSON.toJSONString(message));
            pushService.sendNotifyToSingleUser(user, pushModel);
        } else {
            Map<String, Object> params = JSON.parseObject(message.getContent());
            PushModel pushModel = null;
            switch (SystemNotifyType.valueOf(params.get("type").toString())) {
                //Todo :
            }
            pushService.sendNotifyToSingleUser(user, pushModel);
        }
    }
    /**
     * 发送信息给指定用户n
     src = 0 表示系统消息
     */
    public void sendMessageToUser(Message message) {
        message.setCreatedAt(new Date());
        Long srcUserId = message.getSrcUserId();
        Long userId = message.getDesUserId();
        if(srcUserId.equals(0L) && userId.equals(0L)) {
            sendMessageToAllUsers(new TextMessage(JSON.toJSONString(message)));
        } else {
            if(Boolean.TRUE.equals(redisService.get(Constants.WEBSOCKET_USER + userId))){
                //消息接收者在线 直接转发
                redisService.convertAndSend(message.buildTopic(), JSONObject.toJSONString(message));
                logger.info("消息转发：" + message);
                message.setAlreadySent(true);
            } else {
                //消息接收者不在线
                User user = userService.getUserById(userId);
                sendPushNotifyToUser(user, message);
                logger.info("消息推送：" + message);
                message.setAlreadySent(false);
            }
        }
        String key = Constants.WEBSOCKET_MESSAGE + message.getSrcUserId() + ":" + message.getDesUserId();
        redisService.rightPush(key, message);
    }

    /**
     * 广播信息
     * @param message
     * @return
     */
    public boolean sendMessageToAllUsers(TextMessage message) {
        return true;
    }


    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        System.out.println("连接出错");
        removeDisconnectedUser(getUserId(session));
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("连接已关闭：" + status);
        removeDisconnectedUser(getUserId(session));
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 获取用户标识
     * @param session
     * @return
     */
    private Long getUserId(WebSocketSession session) {
        try {
            Object user = session.getAttributes().get(Constants.WEBSOCKET_USER);
            if(user instanceof User) {
                return ((User)user).getId();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
    private void removeDisconnectedUser(Long uid) {
        if(users.containsKey(uid)) {
            redisMessageListenerContainer.removeMessageListener(users.get(uid));
        }
        users.remove(uid);
        redisService.set(Constants.WEBSOCKET_USER + uid, false);
    }
}

