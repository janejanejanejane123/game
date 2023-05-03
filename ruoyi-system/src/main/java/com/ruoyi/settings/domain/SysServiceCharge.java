package com.ruoyi.settings.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * 手续费设置对象 sys_service_charge
 * 
 * @author nn
 * @date 2022-03-26
 */
public class SysServiceCharge extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 表ID */
    private Long id;

    /** 商户ID */
    @Excel(name = "商户ID")
    private Long userId;

    /** 商户名称 */
    @Excel(name = "商户名称")
    private String userName;

    /** 手续费类型(1:市场购买 2:市场销售 3:玩家充值 4:玩家提现 5:商户充值 6:商户提现 7:商户转入 8:商户转出) */
    @Excel(name = "手续费类型(1:市场购买 2:市场销售 3:玩家充值 4:玩家提现 5:商户充值 6:商户提现 7:商户转入 8:商户转出)")
    private String feeType;

    /** 费率(千分之) */
    @Excel(name = "费率(千分之)")
    private BigDecimal rate;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
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
    public void setFeeType(String feeType) 
    {
        this.feeType = feeType;
    }

    public String getFeeType() 
    {
        return feeType;
    }
    public void setRate(BigDecimal rate)
    {
        this.rate = rate;
    }

    public BigDecimal getRate()
    {
        return rate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("userName", getUserName())
            .append("feeType", getFeeType())
            .append("rate", getRate())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
