package cn.xue.schedule;

import cn.xue.common.Constants;
import cn.xue.model.socket.Message;
import cn.xue.service.redis.RedisService;
import cn.xue.service.socket.MessageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class MessageSchedule {
    private static final Logger logger = Logger.getLogger(MessageSchedule.class);
    @Autowired
    private RedisService redisService;

    @Autowired
    private MessageService messageService;

    //每日凌晨4点将Redis中的消息转储到MySQL
    @Scheduled(cron = "0 0 4 * * ?")
    private void saveMessage() {
        try {
            String prefix = Constants.WEBSOCKET_MESSAGE + "*";
            Set<String> keys = redisService.getAllKeyByPattern(prefix);
            for (String key : keys) {
                Message message = null;
                List<Message> messages = new ArrayList<>();
                while ((message = (Message) redisService.leftPop(key)) != null) {
                    messages.add(message);
                }
                if (messages.size() > 0) {
                    messageService.insertMessages(messages);
                }
            }
        } catch (Exception e) {
            logger.error("保存消息到数据库异常 " + e.getMessage());
        }
    }
}
