package com.ruoyi.member.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户资料申请与审核对象 t_user_info_apply
 * 
 * @author ruoyi
 * @date 2022-03-28
 */
public class TUserInfoApply extends BaseEntity
{
    private static final long serialVersionUID = 1L;



    private Short status;

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    /**  */
    private Long id;

    /** 用户id */
    @Excel(name = "用户id")
    private Long uid;

    /** 申请账号 */
    @Excel(name = "申请账号")
    private String username;

    /** 审核内容 */
    @Excel(name = "审核内容")
    private String content;

    /** 类型 1 微信 2 支付宝 。。。。 */
    @Excel(name = "类型 1 微信 2 支付宝 。。。。")
    private Short type;

    /** 申请日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "申请日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date appTime;

    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "审核时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date upTime;

    /** 审核人账号 */
    @Excel(name = "审核人账号")
    private String upSysUsername;

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
    public void setUsername(String username) 
    {
        this.username = username;
    }

    public String getUsername() 
    {
        return username;
    }
    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }
    public void setType(Short type) 
    {
        this.type = type;
    }

    public Short getType() 
    {
        return type;
    }
    public void setAppTime(Date appTime) 
    {
        this.appTime = appTime;
    }

    public Date getAppTime() 
    {
        return appTime;
    }
    public void setUpTime(Date upTime) 
    {
        this.upTime = upTime;
    }

    public Date getUpTime() 
    {
        return upTime;
    }
    public void setUpSysUsername(String upSysUsername) 
    {
        this.upSysUsername = upSysUsername;
    }

    public String getUpSysUsername() 
    {
        return upSysUsername;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("uid", getUid())
            .append("username", getUsername())
            .append("content", getContent())
            .append("type", getType())
            .append("appTime", getAppTime())
            .append("upTime", getUpTime())
            .append("upSysUsername", getUpSysUsername())
            .toString();
    }
}
