package com.ruoyi.common.enums;

/**
 * 手续费类型枚举
 * @author nn
 * @date 2022/8/20
 */
public enum FeeTypeEnum {

    UNKNOWN("0", "未定义类型"),
    MARKET_BUY("1","市场购买"),
    MARKET_SELL("2","市场销售"),
    PLAYER_RECHARGE("3", "玩家充值"),
    PLAYER_WITHDRAW("4", "玩家提现"),
    MERCHANT_RECHARGE("5", "商户充值"),
    MERCHANT_WITHDRAW("6", "商户提现"),
    MERCHANT_ROLL_IN("7", "商户转入"),
    MERCHANT_ROLL_OUT("8", "商户转出"),
    BANK_CARD("9", "银行卡"),
    WECHAT("10", "微信"),
    ALIPAY("11", "支付宝"),
    QQ_WALLET("12", "QQ钱包"),
    ;

    /**
     * 手续费类型.
     */
    private String feeType;
    /**
     * 手续费描述.
     */
    private String desc;

    public String getFeeType() {
        return feeType;
    }

    public String getDesc() {
        return desc;
    }

    FeeTypeEnum(String feeType, String desc) {
        this.feeType = feeType;
        this.desc = desc;
    }

    /**
     * 根据手续费类型查找.
     * @param feeType
     * @return
     */
    public static FeeTypeEnum getFeeTypeByType(String feeType){
        if(feeType != null){
            FeeTypeEnum[] enumArray = FeeTypeEnum.values();
            for(FeeTypeEnum feeTypeEnum : enumArray){
                if(feeTypeEnum.feeType.equals(feeType)){
                    return feeTypeEnum;
                }
            }
        }
        return UNKNOWN;
    }

    /**
     * 根据手续费类型查找描述.
     * @param feeType
     * @return
     */
    public static String getDescByType(String feeType){
        if(feeType != null){
            FeeTypeEnum[] enumArray = FeeTypeEnum.values();
            for(FeeTypeEnum feeTypeEnum : enumArray){
                if(feeTypeEnum.feeType.equals(feeType)){
                    return feeTypeEnum.desc;
                }
            }
        }
        return "未定义的手续费类型";
    }

}
