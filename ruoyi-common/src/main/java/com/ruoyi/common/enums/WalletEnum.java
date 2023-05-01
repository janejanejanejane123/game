package com.ruoyi.common.enums;

public enum WalletEnum {

    //isEnter 1加钱 2扣钱
    FREEZE(1, "FREEZE", "冻结余额", 2),
    RETURN_FREEZE(2, "RETURN_FREEZE", "退还冻结金额", 1),
    DEDUCT_COST(3, "DEDUCT_COST", "扣除冻结余额", 2),
    INCREASE(4, "INCREASE", "增加余额", 1),
    SUBTRACT(5, "SUBTRACT", "扣除余额", 2),
    DEPOSIT(6, "DEPOSIT", "保证金", 1),
    RETURN_DEPOSIT(7, "RETURN_DEPOSIT", "退还保证金", 1),
    DEDUCT_DEPOSIT(8, "DEDUCT_DEPOSIT", "扣除保证金", 2),
    FREEZE_DEPOSIT(9, "FREEZE_DEPOSIT", "冻结保证金", 2),
    BAL_TO_DEPOSIT(10, "BAL_TO_DEPOSIT", "保证金", 1),
    DEPOSIT_TO_BAL(11, "DEPOSIT_TO_BAL", "保证金转到余额", 2);
    private Integer type;//类型

    private String code;//编码

    private String name;//名称

    private Integer isEnter;//转入1：转入 2转出是否

    WalletEnum(Integer type, String code, String name, Integer isEnter) {
        this.type = type;
        this.code = code;
        this.name = name;
        this.isEnter = isEnter;
    }

    public Integer getIsEnter() {
        return isEnter;
    }

    public void setIsEnter(Integer isEnter) {
        this.isEnter = isEnter;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
