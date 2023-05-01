package com.ruoyi.common.enums;

/**
 * @description: 消息状态枚举类
 * @author: nn
 * @create: 2022-07-19 13:41
 **/
public enum MsgStatusEnum {
    DELETE((byte) 0, "已经删除"),
    NORMAL((byte) 1, "正常消息"),
    RETRACT((byte) 2, "撤回消息");

    MsgStatusEnum(byte status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    private byte status;
    private String desc;

    public byte getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
