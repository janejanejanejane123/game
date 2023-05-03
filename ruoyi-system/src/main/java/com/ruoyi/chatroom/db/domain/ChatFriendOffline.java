package com.ruoyi.chatroom.db.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @description: 好友离线消息
 * @author: nn
 * @create: 2022-07-17 11:14
 **/
@Data
@Document(collection="t_chat_friend_offline")

public class ChatFriendOffline {
    /**表ID*/
    private Long id;
    /**站点编码 - 预留字段*/
    private String siteCode = "1";
    /**消息发送者ID*/
    private Long senderUid;
    /**消息接收者ID */
    private Long receiverUid;
    /**玩家类型*/
    private Byte receiverUserType;
    /**消息ID*/
    private Long messageId;
    /**发送时间*/
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date sendTime;
}
