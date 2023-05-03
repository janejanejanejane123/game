package com.ruoyi.chatroom.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 趣语对象 t_chat_interest_language
 * 
 * @author nn
 * @date 2022-07-11
 */
public class ChatInterestLanguage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 表id */
    private Long interestId;

    /** 趣语内容 */
    @Excel(name = "趣语内容")
    private String interestContent;

    /** 趣语类型 */
    @Excel(name = "趣语类型")
    private String interestType;

    /** 状态（0:启用 1:禁用） */
    @Excel(name = "状态", readConverterExp = "0=:启用,1=:禁用")
    private String status;

    public void setInterestId(Long interestId) 
    {
        this.interestId = interestId;
    }

    public Long getInterestId() 
    {
        return interestId;
    }
    public void setInterestContent(String interestContent) 
    {
        this.interestContent = interestContent;
    }

    public String getInterestContent() 
    {
        return interestContent;
    }
    public void setInterestType(String interestType) 
    {
        this.interestType = interestType;
    }

    public String getInterestType() 
    {
        return interestType;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("interestId", getInterestId())
            .append("interestContent", getInterestContent())
            .append("interestType", getInterestType())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("status", getStatus())
            .append("remark", getRemark())
            .toString();
    }
}
