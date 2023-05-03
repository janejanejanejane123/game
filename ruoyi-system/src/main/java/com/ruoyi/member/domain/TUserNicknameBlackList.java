package com.ruoyi.member.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import javax.validation.constraints.NotNull;

/**
 * 用户昵称黑名单对象 t_user_nickname_black_list
 * 
 * @author ruoyi
 * @date 2022-08-09
 */
public class TUserNicknameBlackList extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /**  */
    private Long id;

    /** 用户id */
    @Excel(name = "用户id")
    private Long uid;

    /** 用户名 */
    @Excel(name = "用户名")
    @NotNull
    private String username;

    /** 黑名单昵称 */
    @Excel(name = "黑名单昵称")
    private String nickname;

    /** 添加时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "添加时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date addTime;

    /** 添加人 */
    @Excel(name = "添加人")
    private String addAdmin;

    /** 理由 */
    @Excel(name = "理由")
    @NotNull
    private String comment;

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
    public void setNickname(String nickname) 
    {
        this.nickname = nickname;
    }

    public String getNickname() 
    {
        return nickname;
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
    public void setComment(String comment) 
    {
        this.comment = comment;
    }

    public String getComment() 
    {
        return comment;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("uid", getUid())
            .append("username", getUsername())
            .append("nickname", getNickname())
            .append("addTime", getAddTime())
            .append("addAdmin", getAddAdmin())
            .append("comment", getComment())
            .toString();
    }
}
