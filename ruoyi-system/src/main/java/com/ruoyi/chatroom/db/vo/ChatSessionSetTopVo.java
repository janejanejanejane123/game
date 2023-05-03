package com.ruoyi.chatroom.db.vo;

import lombok.Data;

import java.util.Date;

/**
 * @description: mongo数据模型(聊天置顶表)
 * @author: nn
 * @create: 2022-07-19 09:22
 **/
@Data
public class ChatSessionSetTopVo {

    /**表ID*/
    public Long id;

    /**对话ID(好友：用户标识)*/
    private String conversationId;

    /**用户标识符*/
    private String userIdentifier;

    /**置顶时间*/
    private Date setTopTime;

}
