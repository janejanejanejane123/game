package com.ruoyi.common.enums;

/**
 * @author nn
 * @Description: 撤回来源枚举类
 * @date 2022-07-24 10:33
 */
public enum RecallSourceEnum {
    LOBBY_MESSAGE((byte)1,"大厅消息"),
    FRIENDS_MESSAGE((byte)2, "私聊消息"),;
    private byte recallSource;
    private String desc;

    RecallSourceEnum(byte recallSource, String desc) {
        this.recallSource = recallSource;
        this.desc = desc;
    }

    public byte getRecallSource() {
        return recallSource;
    }

    public String getDesc() {
        return desc;
    }
}
