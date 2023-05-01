package com.ruoyi.common.bussiness.constants.enums;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/6/7,10:50
 * @return:
 **/
public enum  TopicEnum {
    KICKOUTMESSAGE("kick_out"),APPLYCOMPLETE("info_apply"),VERIFIEDCOMPLETE("verify_complete"),PROXY_COMP("proxy_com");

    private String topic;

    TopicEnum(String topic){
        this.topic=topic;
    }

    public String getTopic(){
        return topic;
    }
}
