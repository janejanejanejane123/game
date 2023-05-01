package com.ruoyi.common.enums;

/**
 * @description: 发送消息类型
 * @create: 2022-04-04 11:18
 **/
public enum SendMessageType {
    ALL((byte) 0,"所有在线用户，包含未登陆的"),
    LOGIN_USER((byte)1 ,"所有登陆用户"),
    DESIGNATION_USER((byte)2 ,"指定用户");
    private byte type;
    private String desc;

    SendMessageType(byte type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static SendMessageType getTypeByEnum(Byte sendType){
        if(sendType != null){
            SendMessageType[] array = SendMessageType.values();
            for (SendMessageType sendMessageType : array) {
                if(sendMessageType.getType() == sendType){
                    return sendMessageType;
                }
            }
        }
        return DESIGNATION_USER;
    }

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
