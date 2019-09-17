package cn.xue.controller;

import cn.xue.controller.base.BaseController;
import cn.xue.model.base.ResponseResult;
import cn.xue.model.diy.SystemNotifyType;
import cn.xue.model.socket.Message;
import cn.xue.service.redis.RedisService;
import cn.xue.service.socket.SocketHandler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("message/")
public class MessageController extends BaseController{
    private final Logger logger = Logger.getLogger(MessageController.class);
    @Autowired
    private SocketHandler socketHandler;
    @Autowired
    private RedisService redisService;

    @PostMapping("notice")
    public ResponseEntity<ResponseResult> sendNotify(@RequestBody Message message) {
        ResponseResult result = new ResponseResult();
        try {
            socketHandler.sendMessageToUser(message);
        } catch (Exception e) {
            result.setMessage(e.getMessage());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @GetMapping("history/{userId}")
    public ResponseEntity<ResponseResult> getHistory(@PathVariable("userId") Long userId,
            @PageableDefault(page = 0, size = 20) Pageable pageable) {
        return null;
    }
}
