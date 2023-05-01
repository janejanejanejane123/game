package com.ruoyi.common.enums;

/**
 * @description: 举报类型枚举类
 * @author: nn
 * @create: 2022-07-31 13:41
 **/
public enum ReportTypeEnum {
    EROTICISM((byte) 1, "色情"),
    POLITICS((byte) 2, "政治"),
    VIOLENT((byte) 3, "暴恐"),
    SOCIETY((byte) 4, "民生"),
    REACTIONARY((byte) 5, "反动"),
    CORRUPTION((byte) 6, "贪腐"),
    NAMECALLING((byte) 7, "骂人"),
    OTHER((byte) 8, "其他");

    ReportTypeEnum(byte status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    private byte status;
    private String desc;

    public byte getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
