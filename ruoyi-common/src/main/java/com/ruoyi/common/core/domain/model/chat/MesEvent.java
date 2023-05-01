package com.ruoyi.common.core.domain.model.chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.model.chat.enums.MessageTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * 用于广播消息
 * @param
 * @Author: sddd
 * @Date: 2022/7/10,13:29
 * @return:
 **/


@Data
public class MesEvent implements Serializable {
    private static final long serialVersionUID = -5271455283858465928L;
    public MesEvent(){
        this.id= UUID.randomUUID().toString();
    }
    private String id;

    String topic;
    /**
     * 接收者id
     */
    String sendIdentifier;
    /**
     * 服务器节点
     */
    Long nodeServer;
    /**
     * 消息类型
     */
    MessageTypeEnum type;
    /**
     * 消息内容
     */
    Object content;
    /**
     * 接收者ID
     */
    String recipient;
    /**
     * 发送日期;
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date sendTime;


}
