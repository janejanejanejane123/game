package com.ruoyi.member.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户注册申请对象 t_user_registered_apply
 * 
 * @author ruoyi
 * @date 2022-03-29
 */
public class TUserRegisteredApply extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 用户名 */
    @Excel(name = "用户名")
    private String username;

    /**  */
    private Long id;

    /** 密码 */
    @Excel(name = "密码")
    private String password;

    /** 视频认证 */
    @Excel(name = "视频认证")
    private String video;

    /** 身份证认证 */
    @Excel(name = "身份证认证")
    private String idCard;

    /** 状态 0 未审核 1 通过 2 驳回 */
    @Excel(name = "状态 0 未审核 1 通过 2 驳回")
    private Short status;

    /** 申请时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "申请时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date appTime;

    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "审核时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date upTime;

    /** 审核人 */
    @Excel(name = "审核人")
    private String upSysUser;

    /** 邮箱 通过之后的通知 */
    @Excel(name = "邮箱 通过之后的通知")
    private String email;

    /** 验证码的内容 */
    @Excel(name = "验证码的内容")
    private String captchaContent;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    private Long uid;

    private String comments;

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUsername() 
    {
        return username;
    }
    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setPassword(String password) 
    {
        this.password = password;
    }

    public String getPassword() 
    {
        return password;
    }
    public void setVideo(String video) 
    {
        this.video = video;
    }

    public String getVideo() 
    {
        return video;
    }
    public void setIdCard(String idCard) 
    {
        this.idCard = idCard;
    }

    public String getIdCard() 
    {
        return idCard;
    }
    public void setStatus(Short status) 
    {
        this.status = status;
    }

    public Short getStatus() 
    {
        return status;
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
    public void setUpSysUser(String upSysUser)
    {
        this.upSysUser = upSysUser;
    }

    public String getUpSysUser()
    {
        return upSysUser;
    }
    public void setEmail(String email) 
    {
        this.email = email;
    }

    public String getEmail() 
    {
        return email;
    }
    public void setCaptchaContent(String captchaContent) 
    {
        this.captchaContent = captchaContent;
    }

    public String getCaptchaContent() 
    {
        return captchaContent;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("username", getUsername())
            .append("id", getId())
            .append("password", getPassword())
            .append("video", getVideo())
            .append("idCard", getIdCard())
            .append("status", getStatus())
            .append("appTime", getAppTime())
            .append("upTime", getUpTime())
            .append("upSysUser", getUpSysUser())
            .append("email", getEmail())
            .append("captchaContent", getCaptchaContent())
            .toString();
    }
}
