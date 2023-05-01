package com.ruoyi.common.constant;

/**
 * topic常量类.
 *
 * @author nn
 * @version v 1.0
 * @ClassName: TopicConstant
 * @date 2022/04/04
 */
public class TopicConstants {

    /**注册会员信息的消息topic*/
    public static final String REGISTER_USER_INFO_TOPIC = "register_member_info_to_chat";

    /**注册会员信息的消息topic 的分类*/
    public static final String REGISTER_USER_INFO_TOPIC_SORT = "register_member_info";

    /**修改会员信息的消息topic*/
    public static final String UPDATE_USER_INFO_TOPIC = "update_member_info_to_chat";

    /**修改会员信息的消息topic的分类*/
    public static final String UPDATE_USER_INFO_TOPIC_SORT = "update_member_info";

    /**大厅撤回消息*/
    public static final String HALL_RECALL_MESSAGE = "hall_recall_message";

    /**聊天室开关*/
    public static final String CHAT_SWITCH = "chat_switch";

    /**聊天室大厅开关*/
    public static final String CHAT_HALL_SWITCH = "chat_hall_switch";

    /**系统维护*/
    public static final String SYSTEM_MAINTAIN = "systemMaintain";

    /**大厅普通消息*/
    public static final String CHAT_ROOM_MESSAGE = "hall_message";
    public static final String CHAT_ROOM_RECORDS = "hall_records";
    public static final String PRIVATE_CHAT = "private_chat";

    public static final String CUS_ONLINE = "customer_online";
    public static final String CUS_OFFLINE = "customer_offline";
    public static final String CUS_OFFLINE_MESSAGE = "customer_offline_message";
    public static final String CUS_OFFLINE_INFO = "customer_offline_info";
    public static final String PLAYER_OFFLINE_MESSAGE = "player_offline_message";
}
