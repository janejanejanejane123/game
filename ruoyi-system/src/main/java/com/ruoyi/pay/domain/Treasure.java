package com.ruoyi.pay.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 交易流水记录对象 t_treasure_1
 *
 * @author ry
 * @date 2022-04-26
 */
public class Treasure extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** uid */
    @Excel(name = "uid")
    private Long uid;

    /** merId */
  //  @Excel(name = "商户ID")
    private Long merId;

    /** 金额 */
    @Excel(name = "金额")
    private BigDecimal amount;

    /** 变更前金额 */
    @Excel(name = "变更前金额")
    private BigDecimal oldMoney;

    /** 变更后金额 */
    @Excel(name = "变更后金额")
    private BigDecimal newMoney;

    /** 单号 */
    @Excel(name = "单号")
    private String number;

    /** 类型 */
    @Excel(name = "类型")
    private Integer type;

    /** 摘要 */
    @Excel(name = "摘要")
    private String rmk;

    /** 创建日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date addTime;

    /** 会员名 */
    @Excel(name = "会员名")
    private String name;

    /** 操作人 */
    @Excel(name = "操作人")
    private String sysname;

    /** ip */
    @Excel(name = "ip")
    private String ip;

    /** 手续费 */
    @Excel(name = "手续费")
    private BigDecimal fee;

    /** 1会员 2商户 */
    @Excel(name = "1会员 2商户")
    private String accountType;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setUid(Long uid)
    {
        this.uid = uid;
    }

    public Long getUid()
    {
        return uid;
    }
    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }
    public void setOldMoney(BigDecimal oldMoney)
    {
        this.oldMoney = oldMoney;
    }

    public BigDecimal getOldMoney()
    {
        return oldMoney;
    }
    public void setNewMoney(BigDecimal newMoney)
    {
        this.newMoney = newMoney;
    }

    public BigDecimal getNewMoney()
    {
        return newMoney;
    }
    public void setNumber(String number)
    {
        this.number = number;
    }

    public String getNumber()
    {
        return number;
    }
    public void setType(Integer type)
    {
        this.type = type;
    }

    public Integer getType()
    {
        return type;
    }
    public void setRmk(String rmk)
    {
        this.rmk = rmk;
    }

    public String getRmk()
    {
        return rmk;
    }
    public void setAddTime(Date addTime)
    {
        this.addTime = addTime;
    }

    public Date getAddTime()
    {
        return addTime;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    public void setSysname(String sysname)
    {
        this.sysname = sysname;
    }

    public String getSysname()
    {
        return sysname;
    }
    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public String getIp()
    {
        return ip;
    }
    public void setFee(BigDecimal fee)
    {
        this.fee = fee;
    }

    public BigDecimal getFee()
    {
        return fee;
    }
    public void setAccountType(String accountType)
    {
        this.accountType = accountType;
    }

    public String getAccountType()
    {
        return accountType;
    }

    public Long getMerId() {
        return merId;
    }

    public void setMerId(Long merId) {
        this.merId = merId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("uid", getUid())
                .append("amount", getAmount())
                .append("oldMoney", getOldMoney())
                .append("newMoney", getNewMoney())
                .append("number", getNumber())
                .append("type", getType())
                .append("rmk", getRmk())
                .append("addTime", getAddTime())
                .append("name", getName())
                .append("sysname", getSysname())
                .append("ip", getIp())
                .append("fee", getFee())
                .append("accountType", getAccountType())
                .append("merId", getMerId())
                .toString();
    }
}
