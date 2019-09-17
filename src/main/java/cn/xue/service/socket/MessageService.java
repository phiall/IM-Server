package cn.xue.service.socket;

import cn.xue.model.socket.Message;
import cn.xue.repo.socket.MessageRepository;
import cn.xue.service.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    RedisService redisService;

    public Message insertMessage(Message message) {
        return messageRepository.save(message);
    }
    public List<Message> insertMessages(List<Message> messages) {
        Iterable<Message> ret = messageRepository.saveAll(messages);
        List<Message> res = new ArrayList<>();
        ret.forEach(res::add);
        return res;
    }

    public List<Message> getNoReadMessagesFromDB(Long userId) {
        return messageRepository.findByDesUserIdAndAlreadySent(userId, false);
    }
}