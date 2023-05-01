package com.ruoyi.common.enums;

public class UserOrderConst {

    //订单状态 0待审核 1待抢单 2待支付 3已完成 4取消 5未通过

    public static final Short ORDER_STATUS_CHECK = Short.valueOf("0");

    public static final Short ORDER_STATUS_CREATE = Short.valueOf("1");

    public static final Short ORDER_STATUS_WAITING = Short.valueOf("2");

    public static final Short ORDER_STATUS_FINISH = Short.valueOf("3");

    public static final Short ORDER_STATUS_CANCEL = Short.valueOf("4");

    public static final Short ORDER_STATUS_UNPASS = Short.valueOf("5");


    //订单交易状态 1买家创建 2卖家确认 3买家已付款 4卖家放蛋成功 5暂停放蛋 6买家取消 7超时取消 8等待取消

    public static final Short PAY_STATUS_CREATE = Short.valueOf("1");

    public static final Short PAY_STATUS_CONFIRM = Short.valueOf("2");

    public static final Short PAY_STATUS_PAID = Short.valueOf("3");

    public static final Short PAY_STATUS_FINISH = Short.valueOf("4");

    public static final Short PAY_STATUS_PAUSE = Short.valueOf("5");

    public static final Short PAY_STATUS_CANCEL = Short.valueOf("6");

    public static final Short PAY_STATUS_TIMEOUT = Short.valueOf("7");

    public static final Short PAY_STATUS_WAITOUT = Short.valueOf("8");

    //订单状态变化消息topic
    public static final String TOPIC = "order_status_change";

    public static final String SALE_TOPIC = "order_sale_change";

    public static final String GRAB_TOPIC = "grab_status_change";

    public static final String TOPIC_WALLET = "wallet_change";

    //站内信未读标识(前台用户)
    public static final Integer UNREADSTATE = 0;

    //是否代付0自由挂单1代付订单
    public static final Short FREE_SALE = Short.valueOf("0");

    public static final Short MERCHANT_SALE = Short.valueOf("1");

}
