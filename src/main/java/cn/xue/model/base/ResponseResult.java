package cn.xue.model.base;

import java.util.HashMap;

public class ResponseResult extends HashMap<Object, Object> {
    public ResponseResult() {
        put("code", 0);
        put("message", "OK");
    }
    public void setCode(Integer code) {
        put("code", code);
    }
    public void setMessage(String msg) {
        put("message", msg);
    }
}
