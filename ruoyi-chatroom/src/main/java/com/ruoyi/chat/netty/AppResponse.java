package com.ruoyi.chat.netty;


import com.ruoyi.common.core.domain.model.chat.enums.MessageTypeEnum;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/12/6,13:02
 * @return:
 **/
@Data
public class AppResponse {

    private AppResponse(MessageTypeEnum messageType){
        this.messageType=messageType;
    }


    public static AppResponse newInstance(MessageTypeEnum messageType){
        return new AppResponse(messageType);
    }




    private Map<String,Object> data=new HashMap<>();

    private int code=200;

    private String topic;

    private Long runServer;

    private String  receiver;

    private String sender;

    private final MessageTypeEnum messageType;

    public AppResponse put(String key,Object value){
        data.put(key,value);
        return this;
    }

    public AppResponse setData(Map<String,Object> data){
        this.data=data;
        return this;
    }
}
