package com.ruoyi.pay.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 提现记录对象 t_withdrawal_record
 *
 * @author ry
 * @date 2022-06-05
 */
public class WithdrawalRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /**头像*/
    private String photo;

    /**昵称*/
    private String nickname;

    /**  */
    private Long id;

    /** member_id  会员ID */
  //  @Excel(name = "member_id  会员ID")
    private Long uid;

    /** 订单号 */
    @Excel(name = "订单号")
    private String orderNo;

    /** 会员账号 */
    @Excel(name = "会员账号")
    private String userName;

    /** 金额 */
    @Excel(name = "金额")
    private BigDecimal amount;

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
    private Date payTime;

    /** 手续费 */
    @Excel(name = "手续费")
    private BigDecimal fee;

    /** 商户UID */
    @Excel(name = "商户UID")
    private Long mid;

    /** 商户单号 */
    @Excel(name = "系统单号")
    private String sysOrder;

    /** 钱包地址 */
    @Excel(name = "钱包地址")
    private String address;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "交易时间", width = 30, dateFormat = "yyyy-MM-dd hh:MM:ss")
    private Date createTime;

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

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
    public void setOrderNo(String orderNo)
    {
        this.orderNo = orderNo;
    }

    public String getOrderNo()
    {
        return orderNo;
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
    public void setMid(Long mid)
    {
        this.mid = mid;
    }

    public Long getMid()
    {
        return mid;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getAddress()
    {
        return address;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSysOrder() {
        return sysOrder;
    }

    public void setSysOrder(String sysOrder) {
        this.sysOrder = sysOrder;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("uid", getUid())
                .append("orderNo", getOrderNo())
                .append("userName", getUserName())
                .append("amount", getAmount())
                .append("status", getStatus())
                .append("createTime", getCreateTime())
                .append("ip", getIp())
                .append("merNo", getMerNo())
                .append("version", getVersion())
                .append("notifyUrl", getNotifyUrl())
                .append("payTime", getPayTime())
                .append("fee", getFee())
                .append("mid", getMid())
                .append("merOrder", getSysOrder())
                .append("address", getAddress())
                .append("createTime", getCreateTime())
                .toString();
    }
}
