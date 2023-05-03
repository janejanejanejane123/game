package com.ruoyi.pay.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 商户对象 t_merchant
 *
 * @author ry
 * @date 2022-04-02
 */
public class Merchant extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /**  */
    private Long id;

    /** 商户号 */
    @Excel(name = "商户号")
    private String merNo;

    /** 0正常 1禁用 */
    @Excel(name = "0正常 1禁用")
    private Integer state;

    /** uid */
    @Excel(name = "uid")
    private Long uid;

    /** 公钥 */
    @Excel(name = "公钥")
    private String pubKey;

    /** 私钥 */
    @Excel(name = "私钥")
    private String priKey;

    /** 最后更新人 */
    @Excel(name = "最后更新人")
    private String updateName;

    /** ip */
    @Excel(name = "ip")
    private String ip;

    /**
     * 唯一序列号
     */
    @Excel(name = "序列号")
    private String seqNo;

    public String getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setMerNo(String merNo)
    {
        this.merNo = merNo;
    }

    public String getMerNo()
    {
        return merNo;
    }
    public void setState(Integer state)
    {
        this.state = state;
    }

    public Integer getState()
    {
        return state;
    }
    public void setUid(Long uid)
    {
        this.uid = uid;
    }

    public Long getUid()
    {
        return uid;
    }
    public void setPubKey(String pubKey)
    {
        this.pubKey = pubKey;
    }

    public String getPubKey()
    {
        return pubKey;
    }
    public void setPriKey(String priKey)
    {
        this.priKey = priKey;
    }

    public String getPriKey()
    {
        return priKey;
    }
    public void setUpdateName(String updateName)
    {
        this.updateName = updateName;
    }

    public String getUpdateName()
    {
        return updateName;
    }
    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public String getIp()
    {
        return ip;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("merNo", getMerNo())
                .append("state", getState())
                .append("uid", getUid())
                .append("pubKey", getPubKey())
                .append("priKey", getPriKey())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("updateName", getUpdateName())
                .append("ip", getIp())
                .toString();
    }
}
