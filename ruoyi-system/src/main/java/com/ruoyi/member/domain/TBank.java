package com.ruoyi.member.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 银行卡列表对象 t_bank
 * 
 * @author ruoyi
 * @date 2022-05-20
 */
public class TBank extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /**  */
    private Long id;

    /** 银行中文名称 */
    @Excel(name = "银行中文名称")
    private String name;

    /** 银行缩写代码 */
    @Excel(name = "银行缩写代码")
    private String code;

    /** 0 启用 1 禁用 */
    @Excel(name = "0 启用 1 禁用")
    private Short status;

    /** 添加时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "添加时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date addTime;

    /** 添加人 */
    @Excel(name = "添加人")
    private String addAdmin;

    /** 修改时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "修改时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date upTime;

    /** 修改人 */
    @Excel(name = "修改人")
    private String upAdmin;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setCode(String code) 
    {
        this.code = code;
    }

    public String getCode() 
    {
        return code;
    }
    public void setStatus(Short status) 
    {
        this.status = status;
    }

    public Short getStatus() 
    {
        return status;
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
    public void setUpTime(Date upTime) 
    {
        this.upTime = upTime;
    }

    public Date getUpTime() 
    {
        return upTime;
    }
    public void setUpAdmin(String upAdmin) 
    {
        this.upAdmin = upAdmin;
    }

    public String getUpAdmin() 
    {
        return upAdmin;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("code", getCode())
            .append("status", getStatus())
            .append("addTime", getAddTime())
            .append("addAdmin", getAddAdmin())
            .append("upTime", getUpTime())
            .append("upAdmin", getUpAdmin())
            .toString();
    }
}
