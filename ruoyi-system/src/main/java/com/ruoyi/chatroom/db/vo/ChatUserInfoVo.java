package com.ruoyi.chatroom.db.vo;

import lombok.Data;

import java.util.Date;

/**
 * @description: mongo数据模型(用户信息表)
 * @author: nn
 * @create: 2022-07-19 09:22
 **/
@Data
public class ChatUserInfoVo {

    /**表ID*/
    public Long id;

    /**好友Id*/
    private String friendId;

    /**用户标识符*/
    private String userIdentifier;

    /**小头像*/
    private String smallHead;

    /**大头像*/
    private String largeHead;

    /**昵称*/
    private String nikeName;

    /**推广码*/
    private String shortUrl;

    /**创建更新时间*/
    private Date createTime;

    /**更新时间*/
    private Date updateTime;

    /**备注*/
    private String remark;

    /**我的状态(0 :无状态 1：已申请 2：已邀请 3:已拒绝 4：好友 )*/
    private short myStatus;

    /**在线状态(0：离线 1：在线)*/
    private byte onLine;

}
