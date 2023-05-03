package com.ruoyi.settings.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 代理返水设置对象 sys_agency_backwater
 * 
 * @author nn
 * @date 2022-06-26
 */
public class SysAgencyBackwater extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 表ID */
    private Long id;

    /** 代理ID */
    @Excel(name = "代理ID")
    private Long userId;

    /** 代理名称 */
    @Excel(name = "代理名称")
    private String userName;

    /** 返水类型(1:银行卡 2:微信 3:支付宝 4:QQ钱包 ) */
    @Excel(name = "返水类型(1:银行卡 2:微信 3:支付宝 4:QQ钱包 )")
    private String feeType;

    /** 费率(千分之) */
    @Excel(name = "费率(千分之)")
    private BigDecimal rate;

    /** 上线id  */
    @Excel(name = "上线id ")
    private Long superiorId;

    /** 上线名称 */
    @Excel(name = "上线名称")
    private String superiorName;

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
    public void setSuperiorId(Long superiorId) 
    {
        this.superiorId = superiorId;
    }

    public Long getSuperiorId() 
    {
        return superiorId;
    }
    public void setSuperiorName(String superiorName) 
    {
        this.superiorName = superiorName;
    }

    public String getSuperiorName() 
    {
        return superiorName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("userName", getUserName())
            .append("feeType", getFeeType())
            .append("rate", getRate())
            .append("superiorId", getSuperiorId())
            .append("superiorName", getSuperiorName())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
