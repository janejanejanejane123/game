package com.ruoyi.common.enums;

import com.ruoyi.common.constant.TopicConstants;

public enum ChatTopic {
    CHAT_ROOM_RECORDS(TopicConstants.CHAT_ROOM_RECORDS),
    CHAT_ROOM_MESSAGE(TopicConstants.CHAT_ROOM_MESSAGE),
    CUSTOMER_ONLINE(TopicConstants.CUS_ONLINE),
    CUSTOMER_OFFLINE(TopicConstants.CUS_OFFLINE),
    PLAYER_OFFLINE_MESSAGE(TopicConstants.PLAYER_OFFLINE_MESSAGE),
    CUSTOMER_OFFLINE_MESSAGE(TopicConstants.CUS_OFFLINE_MESSAGE),
    PRIVATE_CHAT(TopicConstants.PRIVATE_CHAT),


    ;

    ChatTopic(String value){
        this.value=value;
    }
    private final String value;

    public String value(){
        return value;
    }

}
