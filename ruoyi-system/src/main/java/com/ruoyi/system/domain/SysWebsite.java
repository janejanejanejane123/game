package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 网站管理对象 sys_website
 * 
 * @author nn
 * @date 2022-07-21
 */
public class SysWebsite extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 表id */
    private Long id;

    /** 网站编码 */
    @Excel(name = "网站编码")
    private String siteCode;

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

    /** 状态（0:启用 1:禁用） */
    @Excel(name = "状态", readConverterExp = "0=:启用,1=:禁用")
    private String status;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    private String domain;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setSiteCode(String siteCode) 
    {
        this.siteCode = siteCode;
    }

    public String getSiteCode() 
    {
        return siteCode;
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
    public void setSiteDesc(String siteDesc) 
    {
        this.siteDesc = siteDesc;
    }

    public String getSiteDesc() 
    {
        return siteDesc;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public String getSiteIcon() {
        return siteIcon;
    }

    public void setSiteIcon(String siteIcon) {
        this.siteIcon = siteIcon;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("siteCode", getSiteCode())
            .append("siteName", getSiteName())
            .append("siteLogo", getSiteLogo())
            .append("siteIcon", getSiteIcon())
            .append("siteDesc", getSiteDesc())
            .append("status", getStatus())
            .toString();
    }

}
