package com.ruoyi.chatroom.db.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.ruoyi.common.enums.ChatMessageType;
import com.ruoyi.common.enums.MsgStatusEnum;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @description: 大厅消息记录
 * @author: nn
 * @create: 2022-07-25 09:22
 **/

@Data
@Document(collection="t_chat_room_record")
public class ChatRoomRecord {
    /**消息ID*/
    @Id
    private Long messageId;

    private String seqDate;
    private Long seq;
    /**站点编码 - 预留字段*/
    private String siteCode = "1";
    /**上一次的消息ID*/
    private Long lastMessageId;
    /**
     * 消息类型
     * @see ChatMessageType
     */
    private byte msgType;
    /**消息体*/
    private Object msgBody;
    /**发送时间*/
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date sendTime;
    /**发送消息的用户ID*/
    private Long userId;
    /**我自己的ID标识*/
    private String userIdentifier;
    /** 用户名称*/
    private String userName;
    /**用户昵称*/
    private String nickName;
    /**用户头像*/
    private String userPortrait;
    /** 0:系统消息，1:用户消息, D2：精准管理员,4:客服消息  */
    private byte sendType;
    /**发送用户IP*/
    private String sendUserIp;
    /**是否被举报*/
    private boolean isReport;
    /**举报次数*/
    private Integer reportNumber;
    /**
     * 消息状态
     * @see MsgStatusEnum
     */
    private byte msgStatus;

}
