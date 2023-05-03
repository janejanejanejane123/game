package com.ruoyi.chatroom.db.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @description: mongo数据模型(好友表)
 * @author: nn
 * @create: 2022-07-19 09:22
 **/
@Document(collection="t_chat_friends")
@Data
public class ChatFriends {

    /**表ID*/
    @Id
    public Long id;

    /**站点编码 - 预留字段*/
    private String siteCode = "1";

    /**我的用户ID*/
    private Long myUserId;

    /**我的用户标识符*/
    private String myUserIdentifier;

    /**好友的用户ID*/
    private Long friendUserId;

    /**好友的用户标识符*/
    private String friendUserIdentifier;

    /**备注*/
    private String remark;

    /**成为好友的时间*/
    private Date createTime;

    /**更新时间*/
    private Date updateTime;

    /**接收消息(静音mute or 响声sound)*/
    private String muteOrSound;

}
