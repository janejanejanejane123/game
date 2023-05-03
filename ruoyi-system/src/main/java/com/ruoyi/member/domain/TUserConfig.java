package com.ruoyi.member.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户配置对象 t_user_config
 * 
 * @author ruoyi
 * @date 2022-04-08
 */
public class TUserConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 中文名 */
    @Excel(name = "中文名")
    private String name;

    /** 配置键 */
    @Excel(name = "配置键")
    private String configKey;

    /** 开关 0 开启 1关闭 */
    @Excel(name = "开关",readConverterExp = "1=开启,0=关闭")
    private Short open;

    @Excel(name = "是否必填",readConverterExp = "1=是,0=否")
    private Short need;

    /** 额外内容 */
    @Excel(name = "额外内容")
    private String extra;

    /** 1 注册相关 */
    @Excel(name = "注册相关",readConverterExp = "1=注册相关")
    private Short type;

    /** 添加时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "添加时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date addTime;

    /** 添加人 */
    @Excel(name = "添加人")
    private String addUser;

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
    public void setConfigKey(String configKey) 
    {
        this.configKey = configKey;
    }

    public String getConfigKey() 
    {
        return configKey;
    }
    public void setOpen(Short open) 
    {
        this.open = open;
    }

    public Short getOpen() 
    {
        return open;
    }
    public void setExtra(String extra) 
    {
        this.extra = extra;
    }

    public String getExtra() 
    {
        return extra;
    }
    public void setType(Short type) 
    {
        this.type = type;
    }

    public Short getType() 
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
    public void setAddUser(String addUser) 
    {
        this.addUser = addUser;
    }

    public String getAddUser() 
    {
        return addUser;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("configKey", getConfigKey())
            .append("open", getOpen())
            .append("extra", getExtra())
            .append("type", getType())
            .append("addTime", getAddTime())
            .append("addUser", getAddUser())
            .toString();
    }

    public Short getNeed() {
        return need;
    }

    public void setNeed(Short need) {
        this.need = need;
    }
}
