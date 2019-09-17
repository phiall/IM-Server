package cn.xue.service.redis;

import cn.xue.model.socket.Message;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void set(String key, Object value) {
        ValueOperations<String, Object> valueOperation = redisTemplate.opsForValue();
        valueOperation.set(key, value);
    }

    public void setWithExpire(String key, Object value, long time, TimeUnit timeUnit) {
        BoundValueOperations<String, Object> boundValueOperations = redisTemplate.boundValueOps(key);
        boundValueOperations.set(value);
        boundValueOperations.expire(time,timeUnit);
    }

    public long getExpireTime(String key) {
        long time = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return time;
    }

    public <K> K get(String key) {
        ValueOperations<String, Object> valueOperation = redisTemplate.opsForValue();

        return (K) valueOperation.get(key);
    }

    public boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    public void addToListLeft(String listKey, Object... values) {
        //绑定操作
        BoundListOperations<String, Object> boundValueOperations = redisTemplate.boundListOps(listKey);
        //插入数据
        boundValueOperations.leftPushAll(values);
        //设置过期时间
        boundValueOperations.expire(30, TimeUnit.DAYS);
    }
    public Object leftPop(String key) {
        BoundListOperations<String, Object> boundValueOperations = redisTemplate.boundListOps(key);
        return boundValueOperations.leftPop();
    }

    public Set<String> getAllKeyByPattern(String prefix) {
        return redisTemplate.keys(prefix);
    }

    public List<Message> leftPopWithSubKey(String subKey) {
        Set<String> keySet = redisTemplate.keys(subKey);
        //批量获取数据
        List<Object> objectListRedis = redisTemplate.opsForValue().multiGet(keySet);
        List<Message> objectList = JSON.parseArray(objectListRedis.toString(), Message.class);
        return objectList;
    }
    public void rightPush(String key, Object value) {
        BoundListOperations<String, Object> boundValueOperations = redisTemplate.boundListOps(key);
        boundValueOperations.rightPush(value);
    }
    public void clearQueue(String key) {
        BoundListOperations<String, Object> boundValueOperations = redisTemplate.boundListOps(key);
        boundValueOperations.expire(1, TimeUnit.SECONDS);
    }
    public void addToListRight(String listKey, Object... values) {
        //绑定操作
        BoundListOperations<String, Object> boundValueOperations = redisTemplate.boundListOps(listKey);
        //插入数据
        boundValueOperations.rightPushAll(values);
        //设置过期时间
        boundValueOperations.expire(30, TimeUnit.DAYS);
    }

    public List<Object> rangeList(String listKey, long start, long end) {
        //绑定操作
        BoundListOperations<String, Object> boundValueOperations = redisTemplate.boundListOps(listKey);
        //查询数据
        return boundValueOperations.range(start, end);
    }

    public void addToSet(String setKey, Object... values) {
        SetOperations<String, Object> opsForSet = redisTemplate.opsForSet();
        opsForSet.add(setKey, values);
    }

    public Boolean isSetMember(String setKey, Object value) {
        SetOperations<String, Object> opsForSet = redisTemplate.opsForSet();

        return opsForSet.isMember(setKey, value);
    }

    public void removeFromSet(String setKey, Object... values) {
        SetOperations<String, Object> opsForSet = redisTemplate.opsForSet();
        opsForSet.remove(setKey, values);
    }

    public boolean checkKeyValue(String key, String value) {
        String target = get(key);
        //delete(key);
        if(null == target) return false;
        return target.equals(value);
    }

    public void convertAndSend(String channel, Object message) {
        redisTemplate.convertAndSend(channel, message);
    }
}