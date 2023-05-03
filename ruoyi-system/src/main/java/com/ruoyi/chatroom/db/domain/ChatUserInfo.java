package com.ruoyi.chatroom.db.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/7/18,15:26
 * @return:
 **/
@Document(collection="t_chat_user_info")
@Data
public class ChatUserInfo {

    /**表ID*/
    @Id
    public Long id;
    /**站点编码 - 预留字段*/
    private String siteCode = "1";
    /**我的用户Id*/
    private Long userId;
    /**我的用户登录名称*/
    private String userName;
    /**昵称*/
    private String nikeName;
    /**用户标识符*/
    private String userIdentifier;
    /**小头像*/
    private String smallHead;
    /**大头像*/
    private String largeHead;
    /**推广码*/
    private String shortUrl;
    /**创建时间*/
    private Date createTime;
    /**更新时间*/
    private Date updateTime;
    /**备注*/
    private String remark;
    /**商户ID*/
    private Long merchantId;

    private Long runId;
}
