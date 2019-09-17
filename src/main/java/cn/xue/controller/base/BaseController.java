package cn.xue.controller.base;

import cn.xue.model.base.ResponseResult;
import io.swagger.annotations.Api;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class BaseController {
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ResponseResult> error(Exception e, HttpServletRequest request) {
        Logger logger = Logger.getLogger(getClass());
        ResponseResult result = new ResponseResult();
        result.setMessage(e.getMessage());
        result.setCode(-1);
        logger.error(request.getRequestURL().toString() + "处理失败！！\n" + e.getMessage());
        StackTraceElement[] error = e.getStackTrace();
        for (StackTraceElement stackTraceElement : error) {
            logger.error(stackTraceElement.toString());
        }
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
