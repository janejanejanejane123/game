package com.ruoyi.common.constant;

/**
 * 聊天常量类
 *
 * @author nn
 * @version v 1.0
 * @date 2022/07/19
 */
public class ChatConstant {

    public static final String IDENTIFIER = "IDepay";

    /**接收消息(静音mute or 响声sound)*/
    /** 静音*/
    public static final String MUTE = "mute";
    /** 响声*/
    public static final String SOUND = "sound";

    /**禁言类型
     *  1、none: 都不禁言.
     * 	2、normal: 普通成员禁言，即普通成员不能发消息.
     * 	3、all: 全体禁言，即所有成员均不能发消息.
     * */
    /** 都不禁言*/
    public static final String NONE_MUTE = "none";
    /** 普通成员禁言*/
    public static final String NORMAL_MUTE = "normal";
    /** 全体禁言*/
    public static final String ALL_MUTE = "all";

}
