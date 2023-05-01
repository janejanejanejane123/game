package com.ruoyi.common.enums;

/**
 * @description: 用户类型
 * @author: nn
 * @create: 2022-08-01 10:09
 **/
public enum UserTypeEnum implements BaseEnum {
    PLAYER((short) 0,"会员","sid"),
    MANAGE((short) 1,"管理人员","manage-sid");
    private UserTypeEnum(Short type, String desc, String cookieName){
        this.type = type;
        this.desc = desc;
        this.cookieName = cookieName;
    }
    private Short type;
    private String desc;
    private String cookieName;

    public Short getType() {
        return type;
    }

    @Override
    public String getValue() {
        return cookieName;
    }

    public void setType(Short type) {
        this.type = type;
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public BaseEnum getTypeByEnum(Short type) {
        return getUserTypeByType(type);
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static UserTypeEnum getUserTypeByType(Short type){
        if(type == null){
            return PLAYER;
        }
        UserTypeEnum[] userTypeEnums = UserTypeEnum.values();
        for (UserTypeEnum userTypeEnum : userTypeEnums) {
            if(userTypeEnum.type == type){
                return userTypeEnum;
            }
        }
        return PLAYER;
    }
}
