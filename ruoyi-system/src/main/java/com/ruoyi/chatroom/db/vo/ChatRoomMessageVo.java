package com.ruoyi.chatroom.db.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.enums.ChatMessageType;
import com.ruoyi.common.enums.MsgStatusEnum;
import com.ruoyi.common.enums.SendMessageType;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * @description: 消息载体
 * @author: nn
 * @create: 2022-08-06 11:12
 **/
@Data
public class ChatRoomMessageVo {
    private String seq;
    /**消息ID*/
    private Long messageId;
    /**上一次的消息ID*/
    private Long lastMessageId;
    /**消息头*/
    private Map header;
    /**
     * 消息类型
     * @see ChatMessageType
     */
    private byte msgType;
    /**消息体*/
    private Object msgBody;
    /**发送时间*/
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "GMT+8")
    private Date sendTime;
    /**发送消息的用户ID*/
    private Long userId;
    /**我自己的ID标识*/
    private String userIdentifier;
    /** 用户名称*/
    private String userName;
    /** 用户昵称*/
    private String nikeName;
    /**用户头像*/
    private String userPortrait;
    /**
     * 发送消息类型
     * @see SendMessageType
     */
    private byte sendType;
    /**用户等级*/
    private Integer userLevel;
    /**发送用户IP*/
    private String sendUserIp;
    /**是否被举报*/
    private boolean isReport;
    /**举报次数*/
    private Integer reportNumber;
    /**
     * @see MsgStatusEnum
     */
    private byte msgStatus;
    /**是否自己，0：不是,1:是*/
    private byte isSelf;
    /**是否客服发送消息*/
    private Boolean isServeCustomer;

}
