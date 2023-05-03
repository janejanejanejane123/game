package com.ruoyi.member.domain;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户头像对象 t_user_photo
 * 
 * @author ruoyi
 * @date 2022-05-22
 */
public class TUserPhoto extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /**  */
    private Long id;

    /**  */
    @Excel(name = "")
    private String src;

    /**  */
    @JSONField(format = "yyyy-MM-dd")
    @Excel(name = "", width = 30, dateFormat = "yyyy-MM-dd")
    private Date addTime;

    /**  */
    @Excel(name = "")
    private String addAdmin;

    /**  */
    @Excel(name = "")
    private Short status;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setSrc(String src) 
    {
        this.src = src;
    }

    public String getSrc() 
    {
        return src;
    }
    public void setAddTime(Date addTime) 
    {
        this.addTime = addTime;
    }

    public Date getAddTime() 
    {
        return addTime;
    }
    public void setAddAdmin(String addAdmin) 
    {
        this.addAdmin = addAdmin;
    }

    public String getAddAdmin() 
    {
        return addAdmin;
    }
    public void setStatus(Short status) 
    {
        this.status = status;
    }

    public Short getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("src", getSrc())
            .append("addTime", getAddTime())
            .append("addAdmin", getAddAdmin())
            .append("status", getStatus())
            .toString();
    }
}
