package com.ruoyi.chatroom.db.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.ruoyi.common.enums.ChatMessageType;
import com.ruoyi.common.enums.MsgStatusEnum;
import com.ruoyi.common.enums.PlayerTypeEnum;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @description: 朋友聊天记录
 * @author: nn
 * @create: 2022-07-19 11:58
 **/
@Data
@Document(collection="t_chat_friend_record")
public class ChatFriendRecord {


    private Long sessionId;
    /**消息ID*/
    @Id
    private Long messageId;
    /**站点编码 - 预留字段*/
    private String siteCode = "1";
    /**消息体*/
    private Object msgBody;
    /**
     * 消息类型
     * @see ChatMessageType
     */
    private byte msgType;
    //发送消息人的信息.
    /**发送用户ID*/
    private Long sendUserId;
    /**发送用户名称*/
    private String sendUserName;
    /**发送用户的ID标识*/
    private String sendUserIdentifier;
    /**发送用户昵称*/
    private String sendUserNickName;
    /**发送用户头像*/
    private String sendUserPortrait;
    /**
     * 发送用户的用户类型
     * @see PlayerTypeEnum
     */
    private Byte userPlayerType;
    //接收消息人的信息.
    /**朋友Id*/
    private Long friendUserId;
    /**朋友名称*/
    private String friendUserName;
    /**朋友ID标识*/
    private String friendIdentifier;
    /**朋友昵称*/
    private String friendNickName;
    /**朋友的头像*/
    private String friendPortrait;
    /**
     * 朋友的用户类型
     * @see PlayerTypeEnum
     */
    private Byte friendPlayerType;
    /**接收标志0:未接收,1:已经接收*/
    private byte receiveFlag;
    /**发送时间*/
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date sendTime;
    /**发送IP*/
    private String sendUserIp;
    /**
     * 消息状态
     * @see MsgStatusEnum
     */
    private byte msgStatus;
    /**是否被举报*/
    private boolean isReport;
    /**举报次数*/
    private Integer reportNumber;
    /**
     * 被发送人所连的服务器;
     */
    private Long friendRunId;
}
