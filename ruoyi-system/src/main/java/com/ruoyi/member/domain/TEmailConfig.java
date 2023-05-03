package com.ruoyi.member.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 邮箱配置对象 t_email_config
 * 
 * @author ruoyi
 * @date 2022-03-29
 */
public class TEmailConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 发送内容 */
    @Excel(name = "发送内容")
    private String content;

    /** 邮箱账号 */
    @Excel(name = "邮箱账号")
    private String emailAccount;

    /** 邮箱密码 */
    @Excel(name = "邮箱密码")
    private String emailPassword;

    /** 失效时间 */
    @Excel(name = "失效时间")
    private Long outTime;

    /** 网站 */
    @Excel(name = "网站")
    private String host;

    /** 端口 */
    @Excel(name = "端口")
    private String port;

    /** 主题 */
    @Excel(name = "主题")
    private String subject;

    /** 0 禁用 1 启用 */
    @Excel(name = "0 禁用 1 启用")
    private Short status;

    /** 类型 */
    @Excel(name = "类型")
    private Short type;

    /** 权重 */
    @Excel(name = "权重")
    private Short weight;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }
    public void setEmailAccount(String emailAccount) 
    {
        this.emailAccount = emailAccount;
    }

    public String getEmailAccount() 
    {
        return emailAccount;
    }
    public void setEmailPassword(String emailPassword) 
    {
        this.emailPassword = emailPassword;
    }

    public String getEmailPassword() 
    {
        return emailPassword;
    }
    public void setOutTime(Long outTime) 
    {
        this.outTime = outTime;
    }

    public Long getOutTime() 
    {
        return outTime;
    }
    public void setHost(String host) 
    {
        this.host = host;
    }

    public String getHost() 
    {
        return host;
    }
    public void setPort(String port) 
    {
        this.port = port;
    }

    public String getPort() 
    {
        return port;
    }
    public void setSubject(String subject) 
    {
        this.subject = subject;
    }

    public String getSubject() 
    {
        return subject;
    }
    public void setStatus(Short status) 
    {
        this.status = status;
    }

    public Short getStatus() 
    {
        return status;
    }
    public void setType(Short type) 
    {
        this.type = type;
    }

    public Short getType() 
    {
        return type;
    }
    public void setWeight(Short weight) 
    {
        this.weight = weight;
    }

    public Short getWeight() 
    {
        return weight;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("content", getContent())
            .append("emailAccount", getEmailAccount())
            .append("emailPassword", getEmailPassword())
            .append("outTime", getOutTime())
            .append("host", getHost())
            .append("port", getPort())
            .append("subject", getSubject())
            .append("status", getStatus())
            .append("type", getType())
            .append("weight", getWeight())
            .toString();
    }
}
