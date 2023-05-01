package com.ruoyi.common.enums;

/**
 * @author nn
 * @Description: 撤回类型枚举类
 * @date 2022-07-24 10:33
 */
public enum RecallTypeEnum {
    MANAGER((byte)1,"后台管理员撤回"),
    MEMBER((byte)2, "前端会员撤回"),;
    private byte recallType;
    private String desc;

    RecallTypeEnum(byte recallType, String desc) {
        this.recallType = recallType;
        this.desc = desc;
    }

    public byte getRecallType() {
        return recallType;
    }

    public String getDesc() {
        return desc;
    }
}
