package com.ruoyi.pay.domain.vo;

import lombok.Data;

@Data
public class PayNotifyVO {

    /**
     * 状态
     */
    private Integer status;

    /**
     * 订单ID
     */
    private String orderid;

    /***
     * 支付时间
     */
    private Long payTime;

    /***
     * 支付时间
     */
    private String amount;
    /***
     * 商户单号
     */
    private String merNo;

    /***
     * 系统单号
     */
    private String sysOrder;

    private String sign;

    private String notifyUrl;

    private String note;
}
