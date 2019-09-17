package cn.xue.model.diy;
//系统消息具体类型
public enum SystemNotifyType {
    commented,//评论通知 通知被评论者
    loved,//点赞通知 通知被点赞方
    delivered,//发货通知 通知买方
    booked,//订船成功通知 通知卖方 买方暂不通知
    bought,//鱼货购买成功通知 通知卖方 买方暂不通知
    //鱼货订单取消通知 如果卖方同意只通知买方；如果卖方拒绝 平台审核通过同时通知买方和卖方，否则只通知卖方
    product_order_canceled,//通知卖方
    //活动取消通知
    ticket_canceled,//通知卖方
    boat_boss_auth,//船长认证结果
}
