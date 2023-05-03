package com.ruoyi.pay.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 转账记录对象 t_transfer_record
 *
 * @author ruoyi
 * @date 2022-07-31
 */
public class TransferRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /**  */
    private Long id;

    /** 单号 */
    @Excel(name = "单号")
    private String orderno;

    /** 收款人地址 */
    @Excel(name = "收款人地址")
    private String address;

    /** 蛋数量 */
    @Excel(name = "蛋数量")
    private BigDecimal amount;

    /** 手续费 */
    @Excel(name = "手续费")
    private BigDecimal fee;

    /** 状态 0代审核 1审核通过2不通过 */
    @Excel(name = "状态 0代审核 1审核通过2不通过")
    private Integer status;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createDate;

    /** 转账人UID */
    @Excel(name = "转账人UID")
    private Long transefUid;

    /** 收账人UID */
    @Excel(name = "收账人UID")
    private Long payeeUid;

    /** 转账人账号 */
    @Excel(name = "转账人账号")
    private String transefName;

    /** 收款人账号 */
    @Excel(name = "收款人账号")
    private String payeeName;

    /** 版本号 */
    @Excel(name = "版本号")
    private Integer version;

    /** 审核人 */
    @Excel(name = "审核人")
    private String sysUsername;

    /** 拒绝理由 */
    @Excel(name = "拒绝理由")
    private String reason;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setOrderno(String orderno)
    {
        this.orderno = orderno;
    }

    public String getOrderno()
    {
        return orderno;
    }
    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getAddress()
    {
        return address;
    }
    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }
    public void setFee(BigDecimal fee)
    {
        this.fee = fee;
    }

    public BigDecimal getFee()
    {
        return fee;
    }
    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Integer getStatus()
    {
        return status;
    }
    public void setCreateDate(Date createDate)
    {
        this.createDate = createDate;
    }

    public Date getCreateDate()
    {
        return createDate;
    }
    public void setTransefUid(Long transefUid)
    {
        this.transefUid = transefUid;
    }

    public Long getTransefUid()
    {
        return transefUid;
    }
    public void setPayeeUid(Long payeeUid)
    {
        this.payeeUid = payeeUid;
    }

    public Long getPayeeUid()
    {
        return payeeUid;
    }
    public void setTransefName(String transefName)
    {
        this.transefName = transefName;
    }

    public String getTransefName()
    {
        return transefName;
    }
    public void setPayeeName(String payeeName)
    {
        this.payeeName = payeeName;
    }

    public String getPayeeName()
    {
        return payeeName;
    }
    public void setVersion(Integer version)
    {
        this.version = version;
    }

    public Integer getVersion()
    {
        return version;
    }
    public void setSysUsername(String sysUsername)
    {
        this.sysUsername = sysUsername;
    }

    public String getSysUsername()
    {
        return sysUsername;
    }
    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public String getReason()
    {
        return reason;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("orderno", getOrderno())
                .append("address", getAddress())
                .append("amount", getAmount())
                .append("fee", getFee())
                .append("status", getStatus())
                .append("createDate", getCreateDate())
                .append("transefUid", getTransefUid())
                .append("payeeUid", getPayeeUid())
                .append("transefName", getTransefName())
                .append("payeeName", getPayeeName())
                .append("version", getVersion())
                .append("updateTime", getUpdateTime())
                .append("sysUsername", getSysUsername())
                .append("reason", getReason())
                .toString();
    }
}
