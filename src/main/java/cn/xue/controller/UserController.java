package cn.xue.controller;

import cn.xue.controller.base.BaseController;
import cn.xue.model.base.ResponseResult;
import cn.xue.model.user.User;
import cn.xue.service.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("User/")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;
    @PostMapping("/")
    public ResponseEntity<ResponseResult> registerUser(@RequestBody User user) {
        ResponseResult result = new ResponseResult();
        if(StringUtils.isEmpty(user.getPassword()) || StringUtils.isEmpty(user.getPushClientId())
            || StringUtils.isEmpty(user.getPhone())) {
            result.setCode(201);
            result.setMessage("参数非法");
            return new ResponseEntity<>(result, HttpStatus.NOT_ACCEPTABLE);
        }
        User ret = userService.saveUser(user);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
