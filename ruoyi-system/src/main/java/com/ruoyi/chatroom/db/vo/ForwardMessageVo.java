package com.ruoyi.chatroom.db.vo;

import lombok.Data;

/**
 * @description: 转发消息
 * @author: st
 * @create: 2022-08-05 11:21
 **/
@Data
public class ForwardMessageVo {
//    private Long messageId;//消息的唯一指纹码（即消息ID）
    /**转发来源ID*/
    private String forwardSourceId;
    /**转发来源名称*/
    private String forwardSourceName;
    /**转发类型,1.转发给好友,2.转发给群*/
    private Byte originalType;
    /**来源消息ID（即消息ID）*/
    private Long originalMessageId;
    /**消息类型*/
    private Byte msgType;
    /**消息体*/
    private Object msgBody;

}
