package com.ruoyi.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 交易流水类型枚举
 */
public enum TreasureTypeEnum {
    RECHARGE(1, "买入", WalletEnum.INCREASE,"1"),
    WITHDRAW_1(2, "申请卖出", WalletEnum.FREEZE,"2"),
    WITHDRAW_MERCHANT(30, "商户代付", WalletEnum.FREEZE,"2"),
    WITHDRAW_2(13, "确认卖出", WalletEnum.DEDUCT_COST,"2"),
    RECHARGE_RETURN(3, "佣金", WalletEnum.INCREASE,"1"),
    WITHDRAW_REFUND(4, "提现退款", WalletEnum.RETURN_FREEZE,"1"),
    WITHDRAW_MERCHANT_REFUND(31, "商户代付退款", WalletEnum.RETURN_FREEZE,"1"),
    TRANSFER_RECEIPT(5, "玩家充值", WalletEnum.INCREASE,"1"),
    TRANSFER_PAYMENT(6, "玩家提现", WalletEnum.SUBTRACT,"2"),
    BALANCE_OUT(7, "充值", WalletEnum.SUBTRACT,"2"),
    BALANCE_INTO(8, "提现", WalletEnum.INCREASE,"1"),
    ADMIN_RECHARGE(9, "平台加分", WalletEnum.INCREASE,"1"),
    ADMIN_DEDUCE(10, "平台扣分", WalletEnum.SUBTRACT,"2"),
    MERCHANT_RECHARGE(11, "商户充值", WalletEnum.INCREASE,"1"),
    MERCHANT_WITHDRAW(12, "商户提现", WalletEnum.SUBTRACT,"2"),
    MY_TAKEORDERS(14, "跑分接单", WalletEnum.FREEZE_DEPOSIT,"2"),
    MY_CANCELORDERS(15, "跑分取消订单", WalletEnum.RETURN_DEPOSIT,"1"),
    MY_SUCCESSORDERS(16, "跑分订单完成", WalletEnum.DEDUCT_COST,"2"),
    RETURN_WATER(17, "返水", WalletEnum.INCREASE,"1"),
    MERCHANT_TREASUE_OUT(18, "商户转出", WalletEnum.FREEZE,"2"),
    MERCHANT_TREASUE_OUT_SUCCESS(19, "商户转出", WalletEnum.DEDUCT_COST,"2"),
    MERCHANT_TREASUE_OUT_RETURN(20, "商户转出退款", WalletEnum.RETURN_FREEZE,"1"),
    MERCHANT_TREASUE_IN(21, "商户转入", WalletEnum.INCREASE,"1"),
    DEPOSIT(22, "缴纳保证金", WalletEnum.FREEZE_DEPOSIT,"2"),
    RETURN_DEPOSIT(23, "退还保证金", WalletEnum.RETURN_DEPOSIT,"2"),;

    //类型
    private Integer code;
    //名称
    private String msg;
    //钱包操作类型
    private WalletEnum walletEnum;

    //
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static String findNameByCode(Integer code) {
        for (TreasureTypeEnum t : TreasureTypeEnum.values()) {
            if (t.getCode() == code) return t.getMsg();
        }
        return "未找到类型";
    }


    TreasureTypeEnum(int code, String msg, WalletEnum walletEnum, String type) {
        this.code = code;
        this.msg = msg;
        this.walletEnum = walletEnum;
        this.type = type;
    }

    public static Map<String, TreasureTypeEnum> treasureTypeList() {
        Map<String, TreasureTypeEnum> map = new HashMap<>();
        for (TreasureTypeEnum gameOrderTypeEnum : TreasureTypeEnum.values()) {
            map.put(gameOrderTypeEnum.getCode().toString(), gameOrderTypeEnum);
        }
        return map;
    }

    public static TreasureTypeEnum getTreasureType(String status) {
        return treasureTypeList().get(status);
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public WalletEnum getWalletEnum() {
        return walletEnum;
    }

    public void setWalletEnum(WalletEnum walletEnum) {
        this.walletEnum = walletEnum;
    }
}
