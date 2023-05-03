package com.ruoyi.order.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * report对象 t_grab_report
 * 
 * @author ry
 * @date 2022-08-01
 */
public class TGrabReport extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date date;

    /** 用户类型(0会员1商户) */
    @Excel(name = "用户类型(0会员1商户)")
    private Integer type;

    /** 会员id */
    @Excel(name = "会员id")
    private Long userId;

    /** 会员名 */
    @Excel(name = "会员名")
    private String userName;

    /** 交易金额 */
    @Excel(name = "交易金额")
    private BigDecimal amount;

    /** 交易类型0银行卡1微信2支付宝3QQ */
    @Excel(name = "交易类型0银行卡1微信2支付宝3QQ")
    private Integer transType;

    /** 交易手续费 */
    @Excel(name = "交易手续费")
    private BigDecimal feeRate;

    /** 佣金 */
    @Excel(name = "佣金")
    private BigDecimal fee;

    /** 返水 */
    @Excel(name = "返水")
    private BigDecimal water;

    /** 盈利 */
    @Excel(name = "盈利")
    private BigDecimal benifit;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setDate(Date date) 
    {
        this.date = date;
    }

    public Date getDate() 
    {
        return date;
    }
    public void setType(Integer type) 
    {
        this.type = type;
    }

    public Integer getType() 
    {
        return type;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setUserName(String userName) 
    {
        this.userName = userName;
    }

    public String getUserName() 
    {
        return userName;
    }
    public void setAmount(BigDecimal amount) 
    {
        this.amount = amount;
    }

    public BigDecimal getAmount() 
    {
        return amount;
    }
    public void setTransType(Integer transType) 
    {
        this.transType = transType;
    }

    public Integer getTransType() 
    {
        return transType;
    }
    public void setFeeRate(BigDecimal feeRate) 
    {
        this.feeRate = feeRate;
    }

    public BigDecimal getFeeRate() 
    {
        return feeRate;
    }
    public void setFee(BigDecimal fee) 
    {
        this.fee = fee;
    }

    public BigDecimal getFee() 
    {
        return fee;
    }
    public void setWater(BigDecimal water) 
    {
        this.water = water;
    }

    public BigDecimal getWater() 
    {
        return water;
    }
    public void setBenifit(BigDecimal benifit) 
    {
        this.benifit = benifit;
    }

    public BigDecimal getBenifit() 
    {
        return benifit;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("date", getDate())
            .append("type", getType())
            .append("userId", getUserId())
            .append("userName", getUserName())
            .append("amount", getAmount())
            .append("transType", getTransType())
            .append("feeRate", getFeeRate())
            .append("fee", getFee())
            .append("water", getWater())
            .append("benifit", getBenifit())
            .append("remark", getRemark())
            .toString();
    }
}
