package com.ruoyi.common.enums;

/**
 * @description: 消息类型
 * @author: nn
 * @create: 2022-07-23 15:46
 **/
public enum ChatMessageType {
    STRING((byte)1,"字符串类型"),
    IMAGE((byte)2,"图片类型"),
    CHAT_EMOTICON((byte) 3, "聊天表情"),
    FORWARD_MESSAGE((byte) 4, "转发消息"),
    RETRACT_MESSAGE((byte) 5, "撤回消息"),
    CUSTOMER_SERVICE_IMAGE((byte)6,"客服质询图片"),
    CUSTOMER_SERVICE_SOUND((byte)7,"客服质询音频"),
    CUSTOMER_SERVICE_VIDEO((byte)8,"视频"),
    SEND_MY_ORDER((byte) 9,"发送我的挂单"),
    ;
    private byte type;
    private String desc;

    ChatMessageType(byte type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static ChatMessageType getMessageTypeByType(Byte type){
        if(type != null){
            ChatMessageType[] enumArray = ChatMessageType.values();
            for(ChatMessageType chatMessageType : enumArray){
                if(chatMessageType.type == type.byteValue()){
                    return chatMessageType;
                }
            }
        }
        return STRING;
    }

    public static String getMessageDescByType(Byte type){
        if(type != null){
            ChatMessageType[] enumArray = ChatMessageType.values();
            for(ChatMessageType chatMessageType : enumArray){
                if(chatMessageType.type == type.byteValue()){
                    return chatMessageType.desc;
                }
            }
        }
        return "未定义类型";
    }

    public byte getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

}
