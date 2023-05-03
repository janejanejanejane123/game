package com.ruoyi.chatroom.db.vo;

import com.ruoyi.common.enums.ChatMessageType;
import com.ruoyi.common.enums.RecallSourceEnum;
import com.ruoyi.common.enums.RecallTypeEnum;
import lombok.Data;

import java.util.Date;

/**
 * @description: mongo数据模型(消息撤回记录表VO)
 * @author: nn
 * @create: 2020-05-09 09:22
 **/
@Data
public class MessageRecallRecordVo {

    /**表ID*/
    public Long id;

    /**消息ID*/
    private Long messageId;

    /**
     * 消息类型
     * @see ChatMessageType
     */
    private byte msgType;

    /** 消息类型描述*/
    private String msgTypeDesc;

    /**消息体*/
    private Object msgBody;

    /**
     * 撤回来源
     * @see RecallSourceEnum
     */
    private byte recallSource;

    /**
     * 撤回类型
     * @see RecallTypeEnum
     */
    private byte recallType;

    /**撤回人用户ID*/
    private Long recallUserId;

    /**撤回人用户名称*/
    private String recallUserName;

    /**发消息人用户名称*/
    private String sendUserName;

    /**发消息人昵称*/
    private String sendNickName;

    /**撤回时间*/
    private Date recallTime;

    /**撤回备注*/
    private String recallRemark;

}
