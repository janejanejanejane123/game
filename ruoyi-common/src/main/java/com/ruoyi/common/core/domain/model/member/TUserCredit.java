package com.ruoyi.common.core.domain.model.member;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 【请填写功能名称】对象 t_user_credit
 * 
 * @author ruoyi
 * @date 2022-03-27
 */
public class TUserCredit extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long uid;

    /** 内容 */
    @Excel(name = "内容")
    private String content;

    /** 类型，0 银行卡，1 微信，2 支付宝 */
    @Excel(name = "类型，0 银行卡，1 微信，2 支付宝")
    private Long type;

    /** 添加时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "添加时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date addTime;

    /** 删除 0 删除 1 正常 */
    @Excel(name = "删除 0 删除 1 正常")
    private Long isDelete;

    /** 真实姓名 */
    @Excel(name = "真实姓名")
    private String creditName;

    /** 地址 */
    @Excel(name = "地址")
    private String creditAddress;
    @Excel(name = "开户银行")
    private String creditBank;

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
    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }
    public void setType(Long type) 
    {
        this.type = type;
    }

    public Long getType() 
    {
        return type;
    }
    public void setAddTime(Date addTime) 
    {
        this.addTime = addTime;
    }

    public Date getAddTime() 
    {
        return addTime;
    }
    public void setIsDelete(Long isDelete) 
    {
        this.isDelete = isDelete;
    }

    public Long getIsDelete() 
    {
        return isDelete;
    }
    public void setCreditName(String creditName) 
    {
        this.creditName = creditName;
    }

    public String getCreditName() 
    {
        return creditName;
    }
    public void setCreditAddress(String creditAddress) 
    {
        this.creditAddress = creditAddress;
    }

    public String getCreditAddress() 
    {
        return creditAddress;
    }

    public String getCreditBank() {
        return creditBank;
    }

    public void setCreditBank(String creditBank) {
        this.creditBank = creditBank;
    }

    @Override
    public String toString() {
        return "TUserCredit{" +
                "id=" + id +
                ", uid=" + uid +
                ", content='" + content + '\'' +
                ", type=" + type +
                ", addTime=" + addTime +
                ", isDelete=" + isDelete +
                ", creditName='" + creditName + '\'' +
                ", creditAddress='" + creditAddress + '\'' +
                ", creditBank='" + creditBank + '\'' +
                '}';
    }
}
