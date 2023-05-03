package com.ruoyi.member.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * member对象 t_user_mail_box
 * 
 * @author ry
 * @date 2022-06-06
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMailBox extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 标题 */
    @Excel(name = "标题")
    private String title;

    /** 内容 */
    @Excel(name = "内容",width = 150)
    private String content;

    /** 用户类型(0前台用户1后台用户) */
    @Excel(name = "用户类型",readConverterExp="0=前台用户,1=后台用户")
    private Integer userType;

    /** 指定用户id */
    private String userIds;

    /** 发送对象 */
    @Excel(name = "发送对象",defaultValue="所有用户")
    private String userNames;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date creatTime;

    /** 发送时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发送时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date sendTime;

    /** 0未读1已读 */
    @Excel(name = "状态",readConverterExp="0=未读,1=已读")
    private Integer state;

    /** 主题 */
    private String topic;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }
    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }
    public void setUserType(Integer userType) 
    {
        this.userType = userType;
    }

    public Integer getUserType() 
    {
        return userType;
    }
    public void setUserIds(String userIds) 
    {
        this.userIds = userIds;
    }

    public String getUserIds() 
    {
        return userIds;
    }
    public void setUserNames(String userNames) 
    {
        this.userNames = userNames;
    }

    public String getUserNames() 
    {
        return userNames;
    }
    public void setCreatTime(Date creatTime) 
    {
        this.creatTime = creatTime;
    }

    public Date getCreatTime() 
    {
        return creatTime;
    }
    public void setSendTime(Date sendTime) 
    {
        this.sendTime = sendTime;
    }

    public Date getSendTime() 
    {
        return sendTime;
    }
    public void setState(Integer state) 
    {
        this.state = state;
    }

    public Integer getState() 
    {
        return state;
    }
    public void setTopic(String topic) 
    {
        this.topic = topic;
    }

    public String getTopic() 
    {
        return topic;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("title", getTitle())
            .append("content", getContent())
            .append("userType", getUserType())
            .append("userIds", getUserIds())
            .append("userNames", getUserNames())
            .append("creatTime", getCreatTime())
            .append("sendTime", getSendTime())
            .append("state", getState())
            .append("topic", getTopic())
            .append("remark", getRemark())
            .toString();
    }
}
