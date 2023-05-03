package com.ruoyi.chatroom.db.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @description: mongo数据模型(聊天置顶表)
 * @author: nn
 * @create: 2022-07-17 09:22
 **/
@Document(collection="t_chat_session_set_top")
@Data
public class ChatSessionSetTop {

    /**表ID*/
    @Id
    public Long id;

    /**站点编码 - 预留字段*/
    private String siteCode = "1";

    /**
     * 对话ID(好友：用户标识)
     */
    private String conversationId;

    /**我的用户Id*/
    private Long userId;

    /**用户标识符*/
    private String userIdentifier;

    /**置顶时间*/
    private Date setTopTime;

}
