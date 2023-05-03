package com.ruoyi.settings.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 返水设置对象 sys_backwater
 * 
 * @author ruoyi
 * @date 2022-06-24
 */
public class SysBackwater extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 表ID */
    private Long id;

    /** 返水类型(1:银行卡 2:微信 3:支付宝 4:QQ钱包) */
    @Excel(name = "返水类型(1:银行卡 2:微信 3:支付宝 4:QQ钱包)")
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
