package com.ruoyi.settings.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 提现配置对象 t_withdrwal_config
 *
 * @author ry
 * @date 2022-09-13
 */
public class WithdrwalConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Integer id;

    /** 提现次数 */
    @Excel(name = "提现次数")
    private Integer withdrawalCount;

    /** 提现金额 */
    @Excel(name = "提现金额")
    private BigDecimal withdrawalAmount;

    /** 天数 */
    @Excel(name = "天数")
    private Integer day;

    /** 0开启1关闭 */
    @Excel(name = "0开启1关闭")
    private Integer status;

    /** 最少出售金额百分比 */
    @Excel(name = "最少出售金额百分比")
    private Integer sellAmountPercent;

    /** 会员账号，多个以,分割 */
    @Excel(name = "会员账号，多个以,分割")
    private String userNames;

    /** 商户号 */
    @Excel(name = "商户号")
    private String merNo;

    /** 商户ID */
    @Excel(name = "商户ID")
    private Long merId;

    /** 真实姓名，多个以,分割 */
    @Excel(name = "真实姓名，多个以,分割")
    private String realName;

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getId()
    {
        return id;
    }
    public void setWithdrawalCount(Integer withdrawalCount)
    {
        this.withdrawalCount = withdrawalCount;
    }

    public Integer getWithdrawalCount()
    {
        return withdrawalCount;
    }
    public void setWithdrawalAmount(BigDecimal withdrawalAmount)
    {
        this.withdrawalAmount = withdrawalAmount;
    }

    public BigDecimal getWithdrawalAmount()
    {
        return withdrawalAmount;
    }
    public void setDay(Integer day)
    {
        this.day = day;
    }

    public Integer getDay()
    {
        return day;
    }
    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Integer getStatus()
    {
        return status;
    }
    public void setSellAmountPercent(Integer sellAmountPercent)
    {
        this.sellAmountPercent = sellAmountPercent;
    }

    public Integer getSellAmountPercent()
    {
        return sellAmountPercent;
    }
    public void setUserNames(String userNames)
    {
        this.userNames = userNames;
    }

    public String getUserNames()
    {
        return userNames;
    }
    public void setMerNo(String merNo)
    {
        this.merNo = merNo;
    }

    public String getMerNo()
    {
        return merNo;
    }
    public void setMerId(Long merId)
    {
        this.merId = merId;
    }

    public Long getMerId()
    {
        return merId;
    }
    public void setRealName(String realName)
    {
        this.realName = realName;
    }

    public String getRealName()
    {
        return realName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("withdrawalCount", getWithdrawalCount())
                .append("withdrawalAmount", getWithdrawalAmount())
                .append("day", getDay())
                .append("createBy", getCreateBy())
                .append("updateBy", getUpdateBy())
                .append("createTime", getCreateTime())
                .append("status", getStatus())
                .append("sellAmountPercent", getSellAmountPercent())
                .append("userNames", getUserNames())
                .append("merNo", getMerNo())
                .append("merId", getMerId())
                .append("realName", getRealName())
                .toString();
    }
}
