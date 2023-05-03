package com.ruoyi.pay.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 上下分记录对象 t_amount_record
 *
 * @author ry
 * @date 2022-06-05
 */
public class AmountRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    /** 商户订单号 */
    @Excel(name = "昵称")
    private String nickname;
    /** 商户订单号 */
    @Excel(name = "头像")
    private String photo;

    /**  */
    private Long id;

    /** 商户uid */
    @Excel(name = "商户uid")
    private Long mid;

    /** 系统订单号 */
    @Excel(name = "系统订单号")
    private String sysOrder;

    /** 会员账号 */
    @Excel(name = "会员账号")
    private String userName;

    /** 金额 */
    @Excel(name = "金额")
    private BigDecimal amount;

    /** 商户订单号 */
    @Excel(name = "商户订单号")
    private String merOrder;

    /** 状态 0初始 1成功 2处理中 3失败  */
    @Excel(name = "状态 0初始 1成功 2处理中 3失败 ")
    private Integer status;

    /** ip */
    @Excel(name = "ip")
    private String ip;

    /** 商户号 */
    @Excel(name = "商户号")
    private String merNo;

    /** 版本号 */
    @Excel(name = "版本号")
    private Integer version;

    /** 回调URL */
    @Excel(name = "回调URL")
    private String notifyUrl;

    /** 付款时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "付款时间", width = 30, dateFormat = "yyyy-MM-dd hh:MM:ss")
    private Date payTime;

    /** 手续费 */
    @Excel(name = "手续费")
    private BigDecimal fee;

    /** 付款人 id */
    @Excel(name = "付款人 id")
    private Long payUid;

    /** 支付链接 */
    @Excel(name = "支付链接")
    private String payUrl;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setMid(Long mid)
    {
        this.mid = mid;
    }

    public Long getMid()
    {
        return mid;
    }
    public void setSysOrder(String sysOrder)
    {
        this.sysOrder = sysOrder;
    }

    public String getSysOrder()
    {
        return sysOrder;
    }
    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getUserName()
    {
        return userName;
    }
    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }
    public void setMerOrder(String merOrder)
    {
        this.merOrder = merOrder;
    }

    public String getMerOrder()
    {
        return merOrder;
    }
    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Integer getStatus()
    {
        return status;
    }
    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public String getIp()
    {
        return ip;
    }
    public void setMerNo(String merNo)
    {
        this.merNo = merNo;
    }

    public String getMerNo()
    {
        return merNo;
    }
    public void setVersion(Integer version)
    {
        this.version = version;
    }

    public Integer getVersion()
    {
        return version;
    }
    public void setNotifyUrl(String notifyUrl)
    {
        this.notifyUrl = notifyUrl;
    }

    public String getNotifyUrl()
    {
        return notifyUrl;
    }
    public void setPayTime(Date payTime)
    {
        this.payTime = payTime;
    }

    public Date getPayTime()
    {
        return payTime;
    }
    public void setFee(BigDecimal fee)
    {
        this.fee = fee;
    }

    public BigDecimal getFee()
    {
        return fee;
    }
    public void setPayUid(Long payUid)
    {
        this.payUid = payUid;
    }

    public Long getPayUid()
    {
        return payUid;
    }
    public void setPayUrl(String payUrl)
    {
        this.payUrl = payUrl;
    }

    public String getPayUrl()
    {
        return payUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("mid", getMid())
                .append("sysOrder", getSysOrder())
                .append("userName", getUserName())
                .append("amount", getAmount())
                .append("merOrder", getMerOrder())
                .append("status", getStatus())
                .append("createTime", getCreateTime())
                .append("ip", getIp())
                .append("merNo", getMerNo())
                .append("version", getVersion())
                .append("notifyUrl", getNotifyUrl())
                .append("payTime", getPayTime())
                .append("fee", getFee())
                .append("payUid", getPayUid())
                .append("payUrl", getPayUrl())
                .toString();
    }
}
