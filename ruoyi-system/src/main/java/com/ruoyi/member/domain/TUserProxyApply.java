package com.ruoyi.member.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 代理申请对象 t_user_proxy_apply
 * 
 * @author ruoyi
 * @date 2022-06-26
 */
public class TUserProxyApply extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 账号 */
    @Excel(name = "账号")
    private String username;

    /** 用户id */
    @Excel(name = "用户id")
    private Long uid;

    /** 状态 */
    @Excel(name = "状态")
    private Short status;

    /** 申请时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "申请时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createtTime;

    /** 审核人 */
    @Excel(name = "审核人")
    private String updateAdmin;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setUsername(String username) 
    {
        this.username = username;
    }

    public String getUsername() 
    {
        return username;
    }
    public void setUid(Long uid) 
    {
        this.uid = uid;
    }

    public Long getUid() 
    {
        return uid;
    }
    public void setStatus(Short status) 
    {
        this.status = status;
    }

    public Short getStatus() 
    {
        return status;
    }
    public void setCreatetTime(Date createtTime) 
    {
        this.createtTime = createtTime;
    }

    public Date getCreatetTime() 
    {
        return createtTime;
    }
    public void setUpdateAdmin(String updateAdmin) 
    {
        this.updateAdmin = updateAdmin;
    }

    public String getUpdateAdmin() 
    {
        return updateAdmin;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("username", getUsername())
            .append("uid", getUid())
            .append("status", getStatus())
            .append("createtTime", getCreatetTime())
            .append("updateTime", getUpdateTime())
            .append("updateAdmin", getUpdateAdmin())
            .toString();
    }
}
