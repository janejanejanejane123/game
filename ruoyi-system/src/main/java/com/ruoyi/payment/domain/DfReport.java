package com.ruoyi.payment.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 代付报表对象 t_df_report
 * 
 * @author ry
 * @date 2022-08-30
 */
public class DfReport extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 商户id */
    @Excel(name = "商户id")
    private Long uid;

    /** 商户名 */
    @Excel(name = "商户名")
    private String name;

    /** 日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date period;

    /** 订单号 */
    @Excel(name = "订单号")
    private String refId;

    /** 金额 */
    @Excel(name = "金额")
    private BigDecimal money;

    /** 费率差 */
    @Excel(name = "费率差")
    private BigDecimal rate;

    /** 返水金额 */
    @Excel(name = "返水金额")
    private BigDecimal moneyBack;

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
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setPeriod(Date period) 
    {
        this.period = period;
    }

    public Date getPeriod() 
    {
        return period;
    }
    public void setRefId(String refId) 
    {
        this.refId = refId;
    }

    public String getRefId() 
    {
        return refId;
    }
    public void setMoney(BigDecimal money) 
    {
        this.money = money;
    }

    public BigDecimal getMoney() 
    {
        return money;
    }
    public void setRate(BigDecimal rate) 
    {
        this.rate = rate;
    }

    public BigDecimal getRate() 
    {
        return rate;
    }
    public void setMoneyBack(BigDecimal moneyBack) 
    {
        this.moneyBack = moneyBack;
    }

    public BigDecimal getMoneyBack() 
    {
        return moneyBack;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("uid", getUid())
            .append("name", getName())
            .append("period", getPeriod())
            .append("refId", getRefId())
            .append("money", getMoney())
            .append("rate", getRate())
            .append("moneyBack", getMoneyBack())
            .toString();
    }
}
