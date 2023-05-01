package com.ruoyi.common.core.domain.model.chat.enums;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/12/5,14:40
 * @return:
 **/
public enum MessageTypeEnum {
    SELF("00"),PRIVATE("11"),CHATROOM("22"),ALL("33"),ACK("44");

    MessageTypeEnum(String type){
        this.type=type;
    }
    private String type;


    public String type(){
        return this.type;
    }
}
