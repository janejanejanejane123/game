package com.ruoyi.chatroom.db.vo;

import lombok.Data;

import java.util.Date;

/**
 * @description: mongo数据模型(好友表)
 * @author: nn
 * @create: 2019-10-10 09:22
 **/

@Data
public class ChatFriendsVo {

    /**表ID*/
    public Long id;

    /**我的用户标识符*/
    private String myUserIdentifier;

    /**好友的用户标识符*/
    private String friendUserIdentifier;

    /**好友头像*/
    private String friendHead;

    /**好友昵称*/
    private String friendNikeName;

    /**备注*/
    private String remark;

    /**成为好友的时间*/
    private Date createTime;

    /**更新时间*/
    private Date updateTime;

    /**接收消息(静音mute or 响声sound)*/
    private String muteOrSound;

    /**好友在线状态(0：离线 1：在线)*/
    private byte onLine;

}
