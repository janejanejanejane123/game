package com.ruoyi.pay.domain.vo;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.enums.TreasureTypeEnum;
import com.ruoyi.common.enums.WalletEnum;
import com.ruoyi.pay.domain.Wallet;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;


public class WalletVo {
    @NotNull(message = "uid不能为空")
    private Long uid;
    //金额
    @NotNull(message = "walletvo.amount.great0")
    @DecimalMin(value = "0.01", message = "walletvo.amount.great0")
    private BigDecimal amount;
    // 交易类型
    @NotNull(message = "walletvo.treasureType.notnull")
    TreasureTypeEnum treasureTypeEnum;
    // 流水单号
    @NotNull(message = "walletvo.refid.notnull")
    String refId;
    // 备注
    String remark;
    // 会员账号
    @NotNull(message = "walletvo.userName.notnull")
    String userName;

    /**
     * 手续费
     */
    @NotNull(message = "walletvo.commission.notnull")
    private BigDecimal fee = BigDecimal.ZERO;

    private Long merId;

    //操作人
    private String sysName;

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    /**
     * IP
     */
    private String ip;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TreasureTypeEnum getTreasureTypeEnum() {
        return treasureTypeEnum;
    }

    public void setTreasureTypeEnum(TreasureTypeEnum treasureTypeEnum) {
        this.treasureTypeEnum = treasureTypeEnum;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getMerId() {
        return merId;
    }

    public void setMerId(Long merId) {
        this.merId = merId;
    }
}
