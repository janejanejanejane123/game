package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 支付配置对象 sys_pay_config
 * 
 * @author nn
 * @date 2022-09-06
 */
public class SysPayConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 表id */
    private Long id;

    /** 商户ID */
    @Excel(name = "商户ID")
    private Long userId;

    /** 商户名称 */
    @Excel(name = "商户名称")
    private String userName;

    /** 网站名称 */
    @Excel(name = "网站名称")
    private String siteName;

    /** 网站logo */
    @Excel(name = "网站logo")
    private String siteLogo;

    /** 网站icon */
    @Excel(name = "网站icon")
    private String siteIcon;

    /** 网站描述 */
    @Excel(name = "网站描述")
    private String siteDesc;

    /** 支付名称 */
    @Excel(name = "支付名称")
    private String payName;

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
    public void setSiteName(String siteName) 
    {
        this.siteName = siteName;
    }

    public String getSiteName() 
    {
        return siteName;
    }
    public void setSiteLogo(String siteLogo) 
    {
        this.siteLogo = siteLogo;
    }

    public String getSiteLogo() 
    {
        return siteLogo;
    }
    public void setSiteIcon(String siteIcon) 
    {
        this.siteIcon = siteIcon;
    }

    public String getSiteIcon() 
    {
        return siteIcon;
    }
    public void setSiteDesc(String siteDesc) 
    {
        this.siteDesc = siteDesc;
    }

    public String getSiteDesc() 
    {
        return siteDesc;
    }
    public void setPayName(String payName) 
    {
        this.payName = payName;
    }

    public String getPayName() 
    {
        return payName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("userName", getUserName())
            .append("siteName", getSiteName())
            .append("siteLogo", getSiteLogo())
            .append("siteIcon", getSiteIcon())
            .append("siteDesc", getSiteDesc())
            .append("payName", getPayName())
            .toString();
    }
}
