package com.ruoyi.order.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * order对象 t_user_order_detail
 * 
 * @author mc_dog
 * @date 2022-03-20
 */
public class UserOrderDetail extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    @Excel(name = "主键id")
    private String primaryId;

    /** 对应充值记录id */
    @Excel(name = "出售订单id")
    private String referId;

    /** 卖家id */
    @Excel(name = "卖家id")
    private Long salerId;

    /** 卖家账号 */
    @Excel(name = "卖家账号")
    private String salerName;

    /** 出售价格 */
    @Excel(name = "出售价格")
    private BigDecimal saleAmout;

    /** 收款账号 */
    @Excel(name = "收款账号")
    private String salerCardName;

    /** 收款地址 */
    @Excel(name = "收款地址")
    private String salerCardAddress;

    /** 收款信息备注 */
    @Excel(name = "收款信息备注")
    private String salerCardRemark;

    /** 卖家备注 */
    @Excel(name = "卖家备注")
    private String salerRemark;

    /** 付款名 */
    @Excel(name = "付款名")
    private String buyerCardName;

    /** 付款地址 */
    @Excel(name = "付款地址")
    private String buyerCardAddress;

    /** 付款备注 */
    @Excel(name = "付款备注")
    private String buyerCardRemark;

    /** 买家备注 */
    @Excel(name = "买家备注")
    private String buyerRemark;

    /** 支付方式 */
    @Excel(name = "支付方式")
    private Short payWayId;

    /** 支付状态 */
    @Excel(name = "支付状态")
    private Short payStatus;

    private Short buyRank;

    private Short saleRank;

    /** 卖家确认时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "卖家确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date salerCheckTime;

    /** 买家付款时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "买家付款时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date buyerPayTime;

    /** 卖家放蛋时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "卖家放蛋时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date salerConfirmTime;

    /** 卖家暂停时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "卖家暂停时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date salerPauseTime;

    /** 订单完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "订单完成时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date orderFinishTime;

    /** 付款成功截图 */
    @Excel(name = "付款成功截图")
    private String successImg;

    /** 暂停付款截图 */
    @Excel(name = "暂停付款截图")
    private String pauseImg;

    /** 买家id */
    @Excel(name = "买家id")
    private Long buyerId;

    /** 买家账号 */
    @Excel(name = "买家账号")
    private String buyerName;

    /** 订单取消时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "订单取消时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date orderCancelTime;

    /** 操作人 */
    @Excel(name = "操作人")
    private String operateName;

    public void setPrimaryId(String primaryId) 
    {
        this.primaryId = primaryId;
    }

    public String getPrimaryId() 
    {
        return primaryId;
    }
    public void setReferId(String referId) 
    {
        this.referId = referId;
    }

    public String getReferId() 
    {
        return referId;
    }
    public void setSalerId(Long salerId) 
    {
        this.salerId = salerId;
    }

    public Long getSalerId() 
    {
        return salerId;
    }
    public void setSalerName(String salerName) 
    {
        this.salerName = salerName;
    }

    public String getSalerName() 
    {
        return salerName;
    }
    public void setSaleAmout(BigDecimal saleAmout) 
    {
        this.saleAmout = saleAmout;
    }

    public BigDecimal getSaleAmout() 
    {
        return saleAmout;
    }
    public void setSalerCardName(String salerCardName) 
    {
        this.salerCardName = salerCardName;
    }

    public String getSalerCardName() 
    {
        return salerCardName;
    }
    public void setSalerCardAddress(String salerCardAddress) 
    {
        this.salerCardAddress = salerCardAddress;
    }

    public String getSalerCardAddress() 
    {
        return salerCardAddress;
    }
    public void setSalerCardRemark(String salerCardRemark) 
    {
        this.salerCardRemark = salerCardRemark;
    }

    public String getSalerCardRemark() 
    {
        return salerCardRemark;
    }
    public void setSalerRemark(String salerRemark) 
    {
        this.salerRemark = salerRemark;
    }

    public String getSalerRemark() 
    {
        return salerRemark;
    }
    public void setBuyerCardName(String buyerCardName) 
    {
        this.buyerCardName = buyerCardName;
    }

    public String getBuyerCardName() 
    {
        return buyerCardName;
    }
    public void setBuyerCardAddress(String buyerCardAddress) 
    {
        this.buyerCardAddress = buyerCardAddress;
    }

    public String getBuyerCardAddress() 
    {
        return buyerCardAddress;
    }
    public void setBuyerCardRemark(String buyerCardRemark) 
    {
        this.buyerCardRemark = buyerCardRemark;
    }

    public String getBuyerCardRemark() 
    {
        return buyerCardRemark;
    }
    public void setBuyerRemark(String buyerRemark) 
    {
        this.buyerRemark = buyerRemark;
    }

    public String getBuyerRemark() 
    {
        return buyerRemark;
    }
    public void setPayWayId(Short payWayId)
    {
        this.payWayId = payWayId;
    }

    public Short getPayWayId()
    {
        return payWayId;
    }
    public void setPayStatus(Short payStatus)
    {
        this.payStatus = payStatus;
    }

    public Short getPayStatus()
    {
        return payStatus;
    }
    public void setSalerCheckTime(Date salerCheckTime) 
    {
        this.salerCheckTime = salerCheckTime;
    }

    public Date getSalerCheckTime() 
    {
        return salerCheckTime;
    }
    public void setBuyerPayTime(Date buyerPayTime) 
    {
        this.buyerPayTime = buyerPayTime;
    }

    public Date getBuyerPayTime() 
    {
        return buyerPayTime;
    }
    public void setSalerConfirmTime(Date salerConfirmTime) 
    {
        this.salerConfirmTime = salerConfirmTime;
    }

    public Date getSalerConfirmTime() 
    {
        return salerConfirmTime;
    }
    public void setSalerPauseTime(Date salerPauseTime) 
    {
        this.salerPauseTime = salerPauseTime;
    }

    public Date getSalerPauseTime() 
    {
        return salerPauseTime;
    }
    public void setOrderFinishTime(Date orderFinishTime) 
    {
        this.orderFinishTime = orderFinishTime;
    }

    public Date getOrderFinishTime() 
    {
        return orderFinishTime;
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
    public void setBuyerId(Long buyerId) 
    {
        this.buyerId = buyerId;
    }

    public Long getBuyerId() 
    {
        return buyerId;
    }
    public void setBuyerName(String buyerName) 
    {
        this.buyerName = buyerName;
    }

    public String getBuyerName() 
    {
        return buyerName;
    }
    public void setOrderCancelTime(Date orderCancelTime) 
    {
        this.orderCancelTime = orderCancelTime;
    }

    public Date getOrderCancelTime() 
    {
        return orderCancelTime;
    }
    public void setOperateName(String operateName) 
    {
        this.operateName = operateName;
    }

    public String getOperateName() 
    {
        return operateName;
    }

    public Short getBuyRank() {
        return buyRank;
    }

    public void setBuyRank(Short buyRank) {
        this.buyRank = buyRank;
    }

    public Short getSaleRank() {
        return saleRank;
    }

    public void setSaleRank(Short saleRank) {
        this.saleRank = saleRank;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("primaryId", getPrimaryId())
            .append("referId", getReferId())
            .append("salerId", getSalerId())
            .append("salerName", getSalerName())
            .append("saleAmout", getSaleAmout())
            .append("salerCardName", getSalerCardName())
            .append("salerCardAddress", getSalerCardAddress())
            .append("salerCardRemark", getSalerCardRemark())
            .append("salerRemark", getSalerRemark())
            .append("buyerCardName", getBuyerCardName())
            .append("buyerCardAddress", getBuyerCardAddress())
            .append("buyerCardRemark", getBuyerCardRemark())
            .append("buyerRemark", getBuyerRemark())
            .append("payWayId", getPayWayId())
            .append("payStatus", getPayStatus())
            .append("createTime", getCreateTime())
            .append("salerCheckTime", getSalerCheckTime())
            .append("buyerPayTime", getBuyerPayTime())
            .append("salerConfirmTime", getSalerConfirmTime())
            .append("salerPauseTime", getSalerPauseTime())
            .append("orderFinishTime", getOrderFinishTime())
            .append("successImg", getSuccessImg())
            .append("pauseImg", getPauseImg())
            .append("buyerId", getBuyerId())
            .append("buyerName", getBuyerName())
            .append("orderCancelTime", getOrderCancelTime())
            .append("operateName", getOperateName())
            .append("buyRank", getBuyRank())
            .append("saleRank", getSaleRank())
            .toString();
    }
}
