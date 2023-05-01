package com.ruoyi.common.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: 会员修改信息Vo
 * @author: nn
 * @create: 2022-07-19 16:09
 **/
@Data
public class MemberMessageVo implements Serializable {

    private static final long serialVersionUID = -9116165367307937369L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * friendId
     */
    private String friendId;

    /**
     * 头像
     */
    private String userHead;

    /**
     * 小头像
     */
    private String smallHead;

    /**
     * 昵称
     */
    private String userNikeName;
    /**
     * 推广码
     */
    private String shortUrl;

    /**
     * 层级
     */
    private Byte userRank;

    /**
     * 修改类型(1：注冊.2: 头像 3:昵称 )
     */
    private byte type;


    private Long merchantId;

}
