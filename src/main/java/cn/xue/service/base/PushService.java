package cn.xue.service.base;

import cn.xue.model.base.PushModel;
import cn.xue.model.user.User;
import com.alibaba.fastjson.JSON;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.ListMessage;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.notify.Notify;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.dto.GtReq;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.gexin.rp.sdk.template.style.Style0;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PushService {
    @Value("${push.app.id}")
    private String appId;
    @Value("${push.app.key}")
    private String appKey;
    @Value("${push.app.secret}")
    private String appSecret;
    @Value("${push.app.master.secret}")
    private String appMasterSecret;
    @Value("${push.api.url}")
    private String apiUrl;

    public String sendNotifyToSingleUser(User user, PushModel pushModel) {
        IGtPush push = new IGtPush(apiUrl, appKey, appMasterSecret);
        SingleMessage message = new SingleMessage();
        message.setOffline(true);
        // 离线有效时间，单位为毫秒，可选
        message.setOfflineExpireTime(24 * 3600 * 1000);
        message.setData(transmissionTemplate(pushModel));
        // 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
        message.setPushNetWorkType(0);
        Target target = new Target();
        target.setAppId(appId);
        target.setClientId(user.getPushClientId());
        IPushResult ret = null;
        try {
            ret = push.pushMessageToSingle(message, target);
        } catch (RequestException e) {
            e.printStackTrace();
            ret = push.pushMessageToSingle(message, target, e.getRequestId());
        }
        if (ret != null) {
            return ret.getResponse().toString();
        } else {
            return "服务器响应异常";
        }
    }

    public String senNotificationToAllUsers(List<User> userList, PushModel pushModel) {
        IGtPush push = new IGtPush(apiUrl, appKey, appMasterSecret);
        ListMessage message = new ListMessage();
        message.setData(notificationTemplate(pushModel));
        // 设置消息离线，并设置离线时间
        message.setOffline(true);
        // 离线有效时间，单位为毫秒，可选
        message.setOfflineExpireTime(24 * 1000 * 3600);
        // 配置推送目标
        List targets = new ArrayList();
        for (User user : userList) {
            Target target = new Target();
            target.setAppId(appId);
            target.setClientId(user.getPushClientId());
            targets.add(target);
        }
        // taskId用于在推送时去查找对应的message
        String taskId = push.getContentId(message);
        IPushResult ret = push.pushMessageToList(taskId, targets);
        if (ret != null) {
            return ret.getResponse().toString();
        } else {
            return "服务器响应异常";
        }
    }
    public NotificationTemplate notificationTemplate(PushModel model) {
        NotificationTemplate template = new NotificationTemplate();
        // 设置APPID与APPKEY
        template.setAppId(appId);
        template.setAppkey(appKey);
        // 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
        template.setTransmissionType(1);
        Map<String, String> contents = new HashMap<>();
        contents.put("title", model.getTitle());
        contents.put("content", model.getContent());
        contents.put("payload", model.getPayload());
        template.setTransmissionContent(JSON.toJSONString(contents));
        Style0 style = new Style0();
        // 设置通知栏标题与内容
        style.setTitle(model.getTitle());
        style.setText(model.getContent());
        // 配置通知栏图标
        style.setLogo("static/img/icon.png");
        // 设置通知是否响铃，震动，或者可清除
        style.setRing(true);
        style.setVibrate(true);
        style.setClearable(true);
        template.setStyle(style);
        return template;
    }
    public TransmissionTemplate transmissionTemplate(PushModel pushModel){
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        template.setTransmissionContent(pushModel.getPayload());
        template.setAPNInfo(getAPNPayload(pushModel));
        template.setTransmissionType(2);
        Notify notify = new Notify();
        notify.setTitle(pushModel.getTitle());
        notify.setContent(pushModel.getContent());
        String intent = String.format("intent:#Intent;action=android.intent.action.oppopush;launchFlags=0x14000000;" +
                "component=cn.heydong.fishing/io.dcloud.PandoraEntry;S.UP-OL-SU=true;" +
                "S.title=%s;S.content=%s;S.payload=%s;end", pushModel.getTitle(), pushModel.getContent(), pushModel.getPayload());
        notify.setIntent(intent);
        notify.setType(GtReq.NotifyInfo.Type._intent);
        template.set3rdNotifyInfo(notify);//设置第三方通知
        return template;
    }
    private APNPayload getAPNPayload(PushModel model) {
        APNPayload payload = new APNPayload();
        //在已有数字基础上加1显示，设置为-1时，在已有数字上减1显示，设置为数字时，显示指定数字
        payload.setAutoBadge("+1");
        payload.setContentAvailable(1);
        //ios 12.0 以上可以使用 Dictionary 类型的 sound
        payload.setSound("default");

        //简单模式APNPayload.SimpleMsg
        payload.setAlertMsg(getDictionaryAlertMsg(model));
        payload.addCustomMsg("payload", model.getPayload());
//        payload.setVoicePlayType(2);
//        payload.setVoicePlayMessage("定义内容");
        return payload;
    }

    private APNPayload.DictionaryAlertMsg getDictionaryAlertMsg(PushModel model) {
        APNPayload.DictionaryAlertMsg alertMsg = new APNPayload.DictionaryAlertMsg();
        alertMsg.setBody(model.getContent());
        // iOS8.2以上版本支持
        alertMsg.setTitle(model.getTitle());
        return alertMsg;
    }
}
