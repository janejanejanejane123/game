package com.ruoyi.order.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.Digits;
import java.math.BigDecimal;

/**
 * order对象 t_user_order
 * 
 * @author ruoyi
 * @date 2022-03-23
 */
public class UserOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 出售订单号 */
    @Excel(name = "出售订单号")
    private String id;

    /** 卖家id */
    private Long userId;

    /** 卖家账号 */
    @Excel(name = "卖家账号")
    private String userName;

    /** 卖家图像 */
    @Excel(name = "卖家图像")
    private String userImage;

    /** 出售总数 */
    @Excel(name = "金额")
    @Digits(integer = 6, fraction = 0,message = "只能为1-999999的整数")
    private BigDecimal totalAmout;

    /** 已售个数 */
    private BigDecimal saleAmout;

    /** 剩余个数 */
    private BigDecimal leftAmout;

    /** 收款方式 */
    @Excel(name = "收款方式",readConverterExp = "0=银行卡,1=微信收款码,2=支付宝收款码,3=QQ,4=支付宝账号")
    private Short payWay;

    /** 是否拆分 0自由挂单1代付订单*/
    private Short isSplit;

    /** 订单状态 */
    @Excel(name = "订单状态",readConverterExp = "0=待审核,1=待抢单2=交易中,3=已完成,4=取消,5=驳回")
    private Short orderStatus;

    /** 卖家收款名 */
    @Excel(name = "收款名")
    private String userRealName;

    /** 卖家收款地址 */
    @Excel(name = "收款地址")
    private String userCardAddress;

    /** 卖家收款备注 */
    @Excel(name = "收款银行")
    private String userCardRemark;

    /** 卖家备注 */
    @Excel(name = "备注")
    private String saleRemark;

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
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
    public void setUserImage(String userImage) 
    {
        this.userImage = userImage;
    }

    public String getUserImage() 
    {
        return userImage;
    }
    public void setTotalAmout(BigDecimal totalAmout) 
    {
        this.totalAmout = totalAmout;
    }

    public BigDecimal getTotalAmout() 
    {
        return totalAmout;
    }
    public void setSaleAmout(BigDecimal saleAmout) 
    {
        this.saleAmout = saleAmout;
    }

    public BigDecimal getSaleAmout() 
    {
        return saleAmout;
    }
    public void setLeftAmout(BigDecimal leftAmout) 
    {
        this.leftAmout = leftAmout;
    }

    public BigDecimal getLeftAmout() 
    {
        return leftAmout;
    }
    public void setPayWay(Short payWay) 
    {
        this.payWay = payWay;
    }

    public Short getPayWay() 
    {
        return payWay;
    }
    public void setIsSplit(Short isSplit) 
    {
        this.isSplit = isSplit;
    }

    public Short getIsSplit() 
    {
        return isSplit;
    }
    public void setOrderStatus(Short orderStatus) 
    {
        this.orderStatus = orderStatus;
    }

    public Short getOrderStatus() 
    {
        return orderStatus;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("userName", getUserName())
            .append("userImage", getUserImage())
            .append("totalAmout", getTotalAmout())
            .append("saleAmout", getSaleAmout())
            .append("leftAmout", getLeftAmout())
            .append("payWay", getPayWay())
            .append("isSplit", getIsSplit())
            .append("createTime", getCreateTime())
            .append("orderStatus", getOrderStatus())
            .append("updateTime", getUpdateTime())
            .append("userRealName", getUserRealName())
            .append("userCardAddress", getUserCardAddress())
            .append("userCardRemark", getUserCardRemark())
            .append("saleRemark", getSaleRemark())
            .toString();
    }
}
