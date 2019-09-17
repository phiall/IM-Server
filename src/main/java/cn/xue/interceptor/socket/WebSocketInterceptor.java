package cn.xue.interceptor.socket;

import cn.xue.common.Constants;
import cn.xue.model.user.User;
import cn.xue.service.user.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Component
public class WebSocketInterceptor implements HandshakeInterceptor {
    private final Logger logger = Logger.getLogger(getClass());
    @Autowired
    private UserService userService;
//    @Autowired
//    private AdminService adminService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest httpServletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            Long userId = Long.parseLong(httpServletRequest.getParameter("userId").toString());
            String password = httpServletRequest.getParameter("password");
            if(userId > 0) {
                User user = userService.getUserById(userId);
                if (null != user && user.getPassword().equals(password)) {
                    attributes.put(Constants.WEBSOCKET_USER, user);
                    logger.info("用户" + user.getNickName() + "连接并验证成功" + request.getRemoteAddress());
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {

    }
}
