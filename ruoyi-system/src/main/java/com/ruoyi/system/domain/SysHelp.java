package com.ruoyi.system.domain;

import com.ruoyi.common.xss.Xss;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 会员帮助对象 sys_help
 * 
 * @author nn
 * @date 2022-03-13
 */
public class SysHelp extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 帮助ID */
    private Long helpId;

    /** 帮助标题 */
    @Excel(name = "帮助标题")
    private String helpTitle;

    /** 帮助类型（1实名认证 2添加收付款 3买蛋 4卖蛋 5充值 6提现） */
    @Excel(name = "帮助类型", readConverterExp = "1=实名认证,2=添加收付款,3=买蛋,4=卖蛋,5=充值,6=提现")
    private String helpType;

    /** 帮助内容 */
    @Excel(name = "帮助内容")
    private String helpContent;

    /** 帮助icon */
    @Excel(name = "帮助icon")
    private String helpIcon;

    /** 帮助状态（0正常 1关闭） */
    @Excel(name = "帮助状态", readConverterExp = "0=正常,1=关闭")
    private String status;

    public void setHelpId(Long helpId) 
    {
        this.helpId = helpId;
    }

    public Long getHelpId() 
    {
        return helpId;
    }
    public void setHelpTitle(String helpTitle) 
    {
        this.helpTitle = helpTitle;
    }

    @Xss(message = "帮助标题不能包含脚本字符")
    @NotBlank(message = "帮助标题不能为空")
    @Size(min = 0, max = 50, message = "帮助标题不能超过50个字符")
    public String getHelpTitle() 
    {
        return helpTitle;
    }
    public void setHelpType(String helpType) 
    {
        this.helpType = helpType;
    }

    public String getHelpType() 
    {
        return helpType;
    }
    public void setHelpContent(String helpContent) 
    {
        this.helpContent = helpContent;
    }

    public String getHelpContent() 
    {
        return helpContent;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public String getHelpIcon() {
        return helpIcon;
    }

    public void setHelpIcon(String helpIcon) {
        this.helpIcon = helpIcon;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("helpId", getHelpId())
            .append("helpTitle", getHelpTitle())
            .append("helpType", getHelpType())
            .append("helpContent", getHelpContent())
            .append("helpIcon", getHelpIcon())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }

}
