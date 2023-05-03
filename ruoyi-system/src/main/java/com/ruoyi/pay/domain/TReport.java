package com.ruoyi.pay.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 报对象 t_report
 * 
 * @author ruoyi
 * @date 2022-05-31
 */
public class TReport extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** KEY */
    @Excel(name = "KEY")
    private String key;

    /** 金额 */
    @Excel(name = "金额")
    private BigDecimal amount;

    /** 手续费 */
    @Excel(name = "手续费")
    private BigDecimal fee;

    /** 数量 */
    @Excel(name = "数量")
    private Integer times;

    /** 日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date period;

    /** 商户号 */
    @Excel(name = "商户号")
    private String merNo;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setKey(String key) 
    {
        this.key = key;
    }

    public String getKey() 
    {
        return key;
    }
    public void setAmount(BigDecimal amount) 
    {
        this.amount = amount;
    }

    public BigDecimal getAmount() 
    {
        return amount;
    }
    public void setFee(BigDecimal fee)
    {
        this.fee = fee;
    }

    public BigDecimal getFee()
    {
        return fee;
    }
    public void setTimes(Integer times) 
    {
        this.times = times;
    }

    public Integer getTimes() 
    {
        return times;
    }
    public void setPeriod(Date period) 
    {
        this.period = period;
    }

    public Date getPeriod() 
    {
        return period;
    }

    public String getMerNo() {
        return merNo;
    }

    public void setMerNo(String merNo) {
        this.merNo = merNo;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("key", getKey())
            .append("amount", getAmount())
            .append("fee", getFee())
            .append("times", getTimes())
            .append("period", getPeriod())
            .toString();
    }
}
