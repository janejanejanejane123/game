package com.ruoyi.chatroom.db.domain;

import com.ruoyi.common.enums.ChatMessageType;
import com.ruoyi.common.enums.RecallSourceEnum;
import com.ruoyi.common.enums.RecallTypeEnum;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @description: mongo数据模型(消息撤回记录表)
 * @author: nn
 * @create: 2022-07-19 09:22
 **/
@Document(collection="t_message_recall_record")
@Data
public class MessageRecallRecord {

    /**表ID*/
    @Id
    public Long id;
    /**站点编码 - 预留字段*/
    private String siteCode = "1";
    /**消息ID*/
    private Long messageId;

    /**
     * 消息类型
     * @see ChatMessageType
     */
    private byte msgType;

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

    /**发消息人用户ID*/
    private Long sendUserId;

    /**发消息人用户名称*/
    private String sendUserName;

    /**发消息人昵称*/
    private String sendNickName;

    /**撤回时间*/
    private Date recallTime;

    /**撤回备注*/
    private String recallRemark;

}
