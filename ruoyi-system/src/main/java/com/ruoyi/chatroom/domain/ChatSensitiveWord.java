package com.ruoyi.chatroom.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 敏感词库对象 t_chat_sensitive_word
 * 
 * @author nn
 * @date 2022-07-11
 */
public class ChatSensitiveWord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 表id */
    private Long id;

    /** 敏感词 */
    @Excel(name = "敏感词")
    private String sensitiveWord;

    /** 过滤方式(0:替换敏感词,1屏蔽敏感词,2停顿词) */
    @Excel(name = "过滤方式(0:替换敏感词,1屏蔽敏感词,2停顿词)")
    private String filterType;

    /** 替换字符 */
    @Excel(name = "替换字符")
    private String replaceStr;

    /**  使用类型（1:聊天；2:昵称） */
    @Excel(name = " 使用类型", readConverterExp = "1=:聊天；2:昵称")
    private String useType;

    /** 敏感词类型(1色情,2政治,3暴恐,4民生,5反动,6贪腐,7其他'; */
    @Excel(name = "敏感词类型(1色情,2政治,3暴恐,4民生,5反动,6贪腐,7其他';")
    private String strType;

    /** 词的前缀,支持正则 */
    @Excel(name = "词的前缀,支持正则")
    private String prefix;

    /** 词的后缀,支持正则 */
    @Excel(name = "词的后缀,支持正则")
    private String suffix;

    /** 前缀长度 */
    @Excel(name = "前缀长度")
    private Integer prefixLength;

    /** 后缀长度 */
    @Excel(name = "后缀长度")
    private Integer suffixLength;

    /** 状态（0:启用 1:禁用） */
    @Excel(name = "状态", readConverterExp = "0=:启用,1=:禁用")
    private String status;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setSensitiveWord(String sensitiveWord) 
    {
        this.sensitiveWord = sensitiveWord;
    }

    public String getSensitiveWord() 
    {
        return sensitiveWord;
    }
    public void setFilterType(String filterType) 
    {
        this.filterType = filterType;
    }

    public String getFilterType() 
    {
        return filterType;
    }
    public void setReplaceStr(String replaceStr) 
    {
        this.replaceStr = replaceStr;
    }

    public String getReplaceStr() 
    {
        return replaceStr;
    }
    public void setUseType(String useType) 
    {
        this.useType = useType;
    }

    public String getUseType() 
    {
        return useType;
    }
    public void setStrType(String strType) 
    {
        this.strType = strType;
    }

    public String getStrType() 
    {
        return strType;
    }
    public void setPrefix(String prefix) 
    {
        this.prefix = prefix;
    }

    public String getPrefix() 
    {
        return prefix;
    }
    public void setSuffix(String suffix) 
    {
        this.suffix = suffix;
    }

    public String getSuffix() 
    {
        return suffix;
    }
    public void setPrefixLength(Integer prefixLength) 
    {
        this.prefixLength = prefixLength;
    }

    public Integer getPrefixLength() 
    {
        return prefixLength;
    }
    public void setSuffixLength(Integer suffixLength) 
    {
        this.suffixLength = suffixLength;
    }

    public Integer getSuffixLength() 
    {
        return suffixLength;
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
            .append("id", getId())
            .append("sensitiveWord", getSensitiveWord())
            .append("filterType", getFilterType())
            .append("replaceStr", getReplaceStr())
            .append("useType", getUseType())
            .append("strType", getStrType())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("prefix", getPrefix())
            .append("suffix", getSuffix())
            .append("prefixLength", getPrefixLength())
            .append("suffixLength", getSuffixLength())
            .append("status", getStatus())
            .append("remark", getRemark())
            .toString();
    }
}
