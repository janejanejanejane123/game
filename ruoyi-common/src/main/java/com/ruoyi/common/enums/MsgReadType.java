package com.ruoyi.common.enums;

/**
 * @description: 消息读取类型
 * @create: 2022-04-04 09:39
 **/
public enum MsgReadType {
    DEFAULT((byte) 0,"默认类型"),
    READ_IMMEDIATELY((byte) 1,"立即阅读类型");

    MsgReadType(byte type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static MsgReadType getMsgReadType(Byte readType){
        if(readType != null){
            MsgReadType[] values = MsgReadType.values();
            for (MsgReadType msgReadType : values) {
                if(msgReadType.type == readType){
                    return msgReadType;
                }
            }
        }
        return DEFAULT;
    }

    private byte type;
    private String desc;

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }}
