package com.ruoyi.common.enums;

/**
 * 聊天室配置枚举
 * @author nn
 * @date 2022/7/16
 */
public enum ChatRoomConfigEnum {

    // json.
    CHAT_ROOM_MEMBER_COUNT(
            "聊天室人数设置",
            "CHAT_ROOM_MEMBER_COUNT",
            "{\"startHour\":\"14\",\"startLowHour\":\"14\",\"endHour\":\"23\",\"endLowHour\":\"23\",\"startLeast\":5000,\"startMost\":10000,\"endLeast\":18000,\"endMost\":65000}",
            "json"
            ),
    CHAT_SENDING_CONDITION(
            "发言条件",
            "CHAT_SENDING_CONDITION",
            "{\"minAmount\":1000.0,\"bannedTime\":10}",
            "json"),

    // input.
    CHAT_ROOM_NOTICE(
            "聊天室公告",
            "CHAT_ROOM_NOTICE",
            "尊敬的用户，欢迎来到“聊天室”开启您的幸运之旅，请遵守聊天室相关规定，禁止任何形式的广告以及发送相关联系方式，管理员有权撤回不当言论或消息，并禁言停用用户账号，如果遇到充值或提现问题，请联系客服，谢谢！",
            "input"
            ),
    SERVICE_NOTICE(
            "客服公告",
            "SERVICE_NOTICE",
            "您好，很高兴为您服务，请问有什么可以帮助您的呢！！！",
            "input"
            ),

    //button.
    CHAT_SWITCH(
            "聊天室状态",
            "CHAT_SWITCH",
            "true",
            "button"
            ),
    CHAT_HALL_SWITCH(
            "聊天室大厅状态",
            "CHAT_HALL_SWITCH",
            "true",
            "button"
            ),
    GLOBAL_BAN_ON_SPEAKING(
            "聊天室全局禁言",
            "GLOBAL_BAN_ON_SPEAKING",
            "false",
            "button"
            ),
    LOBBY_MESSAGE_RECALL(
            "是否允许大厅消息撤回",
            "LOBBY_MESSAGE_RECALL",
            "false",
            "button"
            ),
    PRIVATE_MESSAGE_RECALL(
            "是否允许私聊消息撤回",
            "PRIVATE_MESSAGE_RECALL",
            "false",
            "button"
            ),
    WHETHER_SEND_FILE(
            "是否允许客服发送文件",
            "WHETHER_SEND_FILE",
            "false",
            "button"
            );

    private String ConfigKeyEnum;
    private String key;
    private String value;
    private String name;
    private String type;
    private String comment;

    public static ChatRoomConfigEnum getConfigKey(String key){
        ChatRoomConfigEnum[] values = ChatRoomConfigEnum.values();
        for (ChatRoomConfigEnum value : values) {
            if (value.getKey().equals(key)){
                return value;
            }
        }
        return null;
    }

    public String getConfigKeyEnum() {
        return ConfigKeyEnum;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public String getComment() {
        return comment;
    }

    ChatRoomConfigEnum(String comment,String key, String value, String type) {
        this.comment = comment;
        this.value = value;
        this.key = key;
        this.type = type;

    }

}
