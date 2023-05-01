package com.ruoyi.common.constant;

/**
 * @description: 转发消息类型
 * @create: 2019-12-09 15:15
 **/
public enum OriginalMsgType {
    ORIGINAL_USER((byte) 1 ,"来源是用户转发"),
    ORIGINAL_GROUP((byte) 2,"来源是群转发");
    OriginalMsgType(byte type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static OriginalMsgType getOriginalMsgType(Byte type){
        if(type != null) {
            OriginalMsgType[] originalMsgTypes = OriginalMsgType.values();
            for (OriginalMsgType originalMsgType : originalMsgTypes) {
                if (originalMsgType.getType() == type){
                    return originalMsgType;
                }
            }
        }
        return null;
    }

    private byte type;
    private String desc;

    public byte getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
