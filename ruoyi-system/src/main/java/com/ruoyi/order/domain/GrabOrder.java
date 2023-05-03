package com.ruoyi.order.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * grab对象 t_grab_order
 * 
 * @author ry
 * @date 2022-07-15
 */
public class GrabOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private String id;

    /** 商户号 */
    @Excel(name = "商户号")
    private String merchant;

    /** 充值订单号 */
    @Excel(name = "充值订单号")
    private String orderId;

    /** 充值金额 */
    @Excel(name = "充值金额")
    private BigDecimal amount;

    /** 充值方式0银行卡1微信2支付宝3QQ */
    @Excel(name = "充值方式0银行卡1微信2支付宝3QQ")
    private Integer rechargeWay;

    /** 订单状态1买家创建 2卖家确认 3买家已付款 4卖家放蛋成功 5暂停放蛋 6买家取消 7超时取消 */
    @Excel(name = "订单状态1买家创建 2卖家确认 3买家已付款 4卖家放蛋成功 5暂停放蛋 6买家取消 7超时取消")
    private Short orderStatus;

    /** 抢单时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "抢单时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date grabTime;

    /** 支付时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "支付时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date payTime;

    /** 暂停时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "暂停时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date pauseTime;

    /** 完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "完成时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date finishTime;

    /** 取消时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "取消时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date cancelTime;

    /** 卖家id */
    @Excel(name = "卖家id")
    private Long userId;

    /** 卖家账号 */
    @Excel(name = "卖家账号")
    private String userName;

    /** 卖家收款名 */
    @Excel(name = "卖家收款名")
    private String userRealName;

    /** 卖家收款地址 */
    @Excel(name = "卖家收款地址")
    private String userCardAddress;

    /** 卖家收款备注 */
    @Excel(name = "卖家收款备注")
    private String userCardRemark;

    /** 卖家备注 */
    @Excel(name = "卖家备注")
    private String saleRemark;

    /** 成功付款截图 */
    @Excel(name = "成功付款截图")
    private String successImg;

    /** 暂停付款截图 */
    @Excel(name = "暂停付款截图")
    private String pauseImg;

    /** 佣金费率 */
    @Excel(name = "佣金费率")
    private BigDecimal feeRate;

    /** 返还佣金 */
    @Excel(name = "返还佣金")
    private BigDecimal fee;

    /** 回调地址 */
    @Excel(name = "回调地址")
    private String callBackUrl;

    /** 操作人 */
    @Excel(name = "操作人")
    private String operateName;

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }
    public void setMerchant(String merchant) 
    {
        this.merchant = merchant;
    }

    public String getMerchant() 
    {
        return merchant;
    }
    public void setOrderId(String orderId) 
    {
        this.orderId = orderId;
    }

    public String getOrderId() 
    {
        return orderId;
    }
    public void setAmount(BigDecimal amount) 
    {
        this.amount = amount;
    }

    public BigDecimal getAmount() 
    {
        return amount;
    }
    public void setRechargeWay(Integer rechargeWay) 
    {
        this.rechargeWay = rechargeWay;
    }

    public Integer getRechargeWay() 
    {
        return rechargeWay;
    }
    public void setOrderStatus(Short orderStatus)
    {
        this.orderStatus = orderStatus;
    }

    public Short getOrderStatus()
    {
        return orderStatus;
    }
    public void setGrabTime(Date grabTime) 
    {
        this.grabTime = grabTime;
    }

    public Date getGrabTime() 
    {
        return grabTime;
    }
    public void setPayTime(Date payTime) 
    {
        this.payTime = payTime;
    }

    public Date getPayTime() 
    {
        return payTime;
    }
    public void setPauseTime(Date pauseTime) 
    {
        this.pauseTime = pauseTime;
    }

    public Date getPauseTime() 
    {
        return pauseTime;
    }
    public void setFinishTime(Date finishTime) 
    {
        this.finishTime = finishTime;
    }

    public Date getFinishTime() 
    {
        return finishTime;
    }
    public void setCancelTime(Date cancelTime) 
    {
        this.cancelTime = cancelTime;
    }

    public Date getCancelTime() 
    {
        return cancelTime;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setUserName(String userName) 
    {
        this.userName = userName;
    }

    public String getUserName() 
    {
        return userName;
    }
    public void setUserRealName(String userRealName) 
    {
        this.userRealName = userRealName;
    }

    public String getUserRealName() 
    {
        return userRealName;
    }
    public void setUserCardAddress(String userCardAddress) 
    {
        this.userCardAddress = userCardAddress;
    }

    public String getUserCardAddress() 
    {
        return userCardAddress;
    }
    public void setUserCardRemark(String userCardRemark) 
    {
        this.userCardRemark = userCardRemark;
    }

    public String getUserCardRemark() 
    {
        return userCardRemark;
    }
    public void setSaleRemark(String saleRemark) 
    {
        this.saleRemark = saleRemark;
    }

    public String getSaleRemark() 
    {
        return saleRemark;
    }
    public void setSuccessImg(String successImg) 
    {
        this.successImg = successImg;
    }

    public String getSuccessImg() 
    {
        return successImg;
    }
    public void setPauseImg(String pauseImg) 
    {
        this.pauseImg = pauseImg;
    }

    public String getPauseImg() 
    {
        return pauseImg;
    }
    public void setFeeRate(BigDecimal feeRate) 
    {
        this.feeRate = feeRate;
    }

    public BigDecimal getFeeRate() 
    {
        return feeRate;
    }
    public void setFee(BigDecimal fee) 
    {
        this.fee = fee;
    }

    public BigDecimal getFee() 
    {
        return fee;
    }
    public void setCallBackUrl(String callBackUrl) 
    {
        this.callBackUrl = callBackUrl;
    }

    public String getCallBackUrl() 
    {
        return callBackUrl;
    }
    public void setOperateName(String operateName) 
    {
        this.operateName = operateName;
    }

    public String getOperateName() 
    {
        return operateName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("merchant", getMerchant())
            .append("orderId", getOrderId())
            .append("amount", getAmount())
            .append("rechargeWay", getRechargeWay())
            .append("orderStatus", getOrderStatus())
            .append("createTime", getCreateTime())
            .append("grabTime", getGrabTime())
            .append("payTime", getPayTime())
            .append("pauseTime", getPauseTime())
            .append("finishTime", getFinishTime())
            .append("cancelTime", getCancelTime())
            .append("userId", getUserId())
            .append("userName", getUserName())
            .append("userRealName", getUserRealName())
            .append("userCardAddress", getUserCardAddress())
            .append("userCardRemark", getUserCardRemark())
            .append("saleRemark", getSaleRemark())
            .append("successImg", getSuccessImg())
            .append("pauseImg", getPauseImg())
            .append("feeRate", getFeeRate())
            .append("fee", getFee())
            .append("callBackUrl", getCallBackUrl())
            .append("operateName", getOperateName())
            .append("remark", getRemark())
            .toString();
    }
}
