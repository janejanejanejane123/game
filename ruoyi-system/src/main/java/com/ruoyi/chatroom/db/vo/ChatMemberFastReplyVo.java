package com.ruoyi.chatroom.db.vo;

import lombok.Data;

import java.util.Date;

/**
 * @description: mongo数据模型(会员快捷回复表)
 * @author: nn
 * @create: 2020-03-31 09:22
 **/
@Data
public class ChatMemberFastReplyVo {

    /**表ID*/
    public Long id;

    /**群员用户标识符*/
    private String userIdentifier;

    /**回复内容*/
    private String replyContent;

    /**创建时间*/
    private Date createTime;

}
