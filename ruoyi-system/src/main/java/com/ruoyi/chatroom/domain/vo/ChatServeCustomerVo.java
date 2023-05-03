package com.ruoyi.chatroom.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 *  客服列表.
 */
@Data
public class ChatServeCustomerVo {

    /**表ID*/
    private Long id;

    /**客服ID*/
    private Long userId;

    /**客服名称*/
    private String userName;

    /**用户标识符*/
    private String userIdentifier;

    /**我的friendID*/
    private String friendId;

    /**客服昵称*/
    private String nickName;

    /**小头像*/
    private String smallHead;

    /**大头像*/
    private String largeHead;

    /**创建时间*/
    private Date createTime;

    /**备注*/
    private String remarks;

    /**是否启用(0:禁用，1:启用)*/
    private Short isEnable;

    /**运行id*/
    private Long runId;

    /**
     * 判断是否在线
     */
    private Boolean onlineState = false;

}