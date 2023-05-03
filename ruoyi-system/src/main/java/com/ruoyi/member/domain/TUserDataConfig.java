package com.ruoyi.member.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户自我配置对象 t_user_data_config
 * 
 * @author ruoyi
 * @date 2022-06-24
 */
public class TUserDataConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /**  */
    private Long uid;

    /**  */
    @Excel(name = "")
    private Short payPasswordForSell;

    /**  */
    @Excel(name = "")
    private Short emailForSell;

    public void setUid(Long uid) 
    {
        this.uid = uid;
    }

    public Long getUid() 
    {
        return uid;
    }
    public void setPayPasswordForSell(Short payPasswordForSell) 
    {
        this.payPasswordForSell = payPasswordForSell;
    }

    public Short getPayPasswordForSell() 
    {
        return payPasswordForSell;
    }
    public void setEmailForSell(Short emailForSell) 
    {
        this.emailForSell = emailForSell;
    }

    public Short getEmailForSell() 
    {
        return emailForSell;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("uid", getUid())
            .append("payPasswordForSell", getPayPasswordForSell())
            .append("emailForSell", getEmailForSell())
            .toString();
    }
}
