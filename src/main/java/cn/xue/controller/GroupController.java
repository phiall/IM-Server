package cn.xue.controller;

import cn.xue.controller.base.BaseController;
import cn.xue.service.redis.RedisService;
import cn.xue.service.socket.MessageService;
import cn.xue.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("Group/")
public class GroupController extends BaseController {
    @Autowired
    private RedisService redisService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;

}
