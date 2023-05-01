package com.ruoyi.common.enums;

public enum PayResponEnums {

    success(200,"请求成功"),
    SIGN_ERR(1,"签名错误"),
    MER_NULL(2,"无效商户"),
    IP(3,"IP未在白名单内"),
    ADDRESS_ERR(4,"钱包地址不存在"),
    ORDER_EXT(5,"订单号已存在"),
    MAINTAINTAIN(6,"维护中"),
    ACCOUNT_NO_EXT(7,"账号不存在,请先注册"),
    ACCOUNT_ABNORMAL(8,"账号异常"),
    WITHDRAWAL_LIMIT(9,"您的充值功能已受限制,需要出售才能继续使用"),
    PARAMS_ERR(10,"参数错误"),
    BALANCE_NO(11,"余额不足"),;

    //类型
    private Integer code;
    //名称
    private String msg;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    PayResponEnums(int i, String s) {
        this.code = i;
        this.msg = s;
    }
}
