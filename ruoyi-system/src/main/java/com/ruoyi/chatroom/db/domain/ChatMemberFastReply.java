package com.ruoyi.chatroom.db.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @description: mongo数据模型(会员快捷回复表)
 * @author: nn
 * @create: 2022-07-19 09:22
 **/
@Document(collection="t_chat_member_fast_reply")
@Data
public class ChatMemberFastReply {

    /**表ID*/
    @Id
    public Long id;

    /**站点编码 - 预留字段*/
    private String siteCode = "1";

    /**群员用户ID*/
    private Long userId;

    /**群员用户标识符*/
    private String userIdentifier;

    /**回复内容*/
    private String replyContent;

    /**创建时间*/
    private Date createTime;

}
