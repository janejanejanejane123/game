package com.ruoyi.chatroom.db.vo;

import lombok.Data;

/**
 * @description: 离线消息VO
 * @author: nn
 * @create: 2022-07-19 13:42
 **/
@Data
public class ChatFriendOfflineVo {

    /**消息接收者ID*/
    private Long receiverUid;
    /**消息ID*/
    private Long messageId;
    /**最后消息ID*/
    private Long lastMessageId;
}
