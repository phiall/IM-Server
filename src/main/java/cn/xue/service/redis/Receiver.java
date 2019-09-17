package cn.xue.service.redis;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.websocket.Session;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
@Component
public class Receiver implements MessageListener {
    private static final Logger logger = Logger.getLogger(Receiver.class);
    private WebSocketSession session;
    @Override
    public void onMessage(Message message, byte[] bytes) {
        //logger.info("收到订阅的消息" + message);
        try {
            session.sendMessage(new TextMessage(JSONObject.parse(message.toString()).toString()));
        } catch (IOException e) {
            logger.error("发送消息到客户端失败" + message);
        }
    }

    public WebSocketSession getSession() {
        return session;
    }

    public void setSession(WebSocketSession session) {
        this.session = session;
    }
}