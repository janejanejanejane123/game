package com.ruoyi.common.enums;

/**
 * @author nn
 * @ClassName : FriendPlayerTypeEnum  //类名
 * @Description: 朋友类型
 * @date 2022-07-19 10:55  //时间
 */
public enum PlayerTypeEnum {
    PLAYER_USER((byte) 1,"会员用户"),
    CUSTOMER_SERVICE((byte)2 ,"客服");
    private byte playerType;
    private String desc;

    PlayerTypeEnum(byte playerType, String desc) {
        this.playerType = playerType;
        this.desc = desc;
    }

    public byte getPlayerType() {
        return playerType;
    }

    public String getDesc() {
        return desc;
    }
}
