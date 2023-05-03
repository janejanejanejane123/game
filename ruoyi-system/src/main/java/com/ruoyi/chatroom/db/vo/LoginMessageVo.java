package com.ruoyi.chatroom.db.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: 登陆Vo
 * @author: ddd
 * @create: 2019-04-15 15:54
 **/
@Data
public class LoginMessageVo implements Serializable {
    private static final long serialVersionUID = -1279690524474476310L;
    /**
     * 节点
     */
    private String server;
    /**
     * 用户标识
     */
    private String identifier;
    /**
     * 1 上线 0 下线
     */
    private byte type;
}
