package com.ruoyi.pay.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 钱包对象 t_wallet
 *
 * @author
 * @date 2022-03-08
 */
public class Wallet extends BaseEntity implements Cloneable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * uid
     */
    @Excel(name = "uid")
    private Long uid;

    /**
     * 余额
     */
    @Excel(name = "余额")
    private BigDecimal balance;

    /**
     * 冻结余额
     */
    @Excel(name = "冻结余额")
    private BigDecimal frozenBalance;

    /**
     * 0会员1商户
     */
    @Excel(name = "0会1商户")
    private Long type;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date uptime;

    /**
     * 会员名
     */
    @Excel(name = "会员名")
    private String name;

    /**
     * 钱包地址
     */
    @Excel(name = "钱包地址")
    private String address;
    /**
     * 压金
     */
    @Excel(name = "保证金")
    private BigDecimal depositBalance;

    @Excel(name = "购买的金额")
    private BigDecimal buyAmount;

    /**
     * 获取有效的余额
     *
     * @return
     */
    public BigDecimal getEffectiveBal() {
        if (this.balance != null && this.buyAmount != null) {
            return this.balance.subtract(this.buyAmount);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getBuyAmount() {
        return buyAmount;
    }

    public void setBuyAmount(BigDecimal buyAmount) {
        this.buyAmount = buyAmount;
    }

    public BigDecimal getDepositBalance() {
        return depositBalance;
    }

    public void setDepositBalance(BigDecimal depositBalance) {
        this.depositBalance = depositBalance;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Long getUid() {
        return uid;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setFrozenBalance(BigDecimal frozenBalance) {
        this.frozenBalance = frozenBalance;
    }

    public BigDecimal getFrozenBalance() {
        return frozenBalance;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public Long getType() {
        return type;
    }

    public void setUptime(Date uptime) {
        this.uptime = uptime;
    }

    public Date getUptime() {
        return uptime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("uid", getUid())
                .append("balance", getBalance())
                .append("frozenBalance", getFrozenBalance())
                .append("type", getType())
                .append("uptime", getUptime())
                .append("name", getName())
                .append("address", getAddress())
                .toString();
    }

    public Object clone() {
        Wallet o = null;
        try {
            o = (Wallet) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }
}
