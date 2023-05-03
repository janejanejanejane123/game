package com.ruoyi.order.domain.vo;

import java.math.BigDecimal;
import java.util.Date;

public class OrderDetailVo {

    /**
     * 订单编号
     * 表 : t_order_detail
     * 对应字段 : primary_id
     */
    private String primaryId;


    private String referId;


    /**
     * 卖家账号
     * 表 : t_order_detail
     * 对应字段 : saler_name
     */
    private String salerRemark;

    /**
     * 售卖个数
     * 表 : t_order_detail
     * 对应字段 : sale_amout
     */
    private BigDecimal saleAmout;

    /**
     * 收款方式1银行卡2微信3支付宝
     * 表 : t_order_detail
     * 对应字段 : sale_way_id
     */
    private Short payWayId;

    /**
     * 创建时间
     * 表 : t_order_detail
     * 对应字段 : create_time
     */
    private Date createTime;

    /**
     * 买家付款姓名
     * 表 : t_order_detail
     * 对应字段 : buyer_card_name
     */
    private String buyerCardName;

    /**
     * 买家付款卡号/地址
     * 表 : t_order_detail
     * 对应字段 : buyer_card_address
     */
    private String buyerCardAddress;

    /**
     * 买家付款支行/备注
     * 表 : t_order_detail
     * 对应字段 : buyer_card_remark
     */
    private String buyerCardRemark;

    /**
     * 买家留言
     * 表 : t_order_detail
     * 对应字段 : buyer_remark
     */
    private String buyerRemark;

    /**
     * 卖家收款姓名
     * 表 : t_order_detail
     * 对应字段 : saler_card_name
     */
    private String salerCardName;

    /**
     * 卖家收款卡号/地址
     * 表 : t_order_detail
     * 对应字段 : saler_card_address
     */
    private String salerCardAddress;

    /**
     * 卖家收款支行/备注
     * 表 : t_order_detail
     * 对应字段 : saler_card_remark
     */
    private String salerCardRemark;

    /**
     * 支付状态1买家创建2卖家确认3买家已付款4卖家放蛋成功5暂停放蛋6买家取消7超时取消
     * 表 : t_order_detail
     * 对应字段 : pay_status
     */
    private Short payStatus;

    /**
     * 卖家确认时间
     * 表 : t_order_detail
     * 对应字段 : saler_check_time
     */
    private Date salerCheckTime;

    /**
     * 买家付款时间
     * 表 : t_order_detail
     * 对应字段 : buyer_pay_time
     */
    private Date buyerPayTime;

    /**
     * 充值订单号
     * 表 : t_order_detail
     * 对应字段 : buyer_ref_id
     */
    private String buyerRefId;

    /**
     * 卖家放蛋时间
     * 表 : t_order_detail
     * 对应字段 : saler_confirm_time
     */
    private Date salerConfirmTime;

    /**
     * 卖家暂停放蛋时间
     * 表 : t_order_detail
     * 对应字段 : saler_pause_time
     */
    private Date salerPauseTime;

    /**
     * 订单结束时间
     * 表 : t_order_detail
     * 对应字段 : order_finish_time
     */
    private Date orderFinishTime;

    /**
     * 转账成功截图
     * 表 : t_order_detail
     * 对应字段 : success_img
     */
    private String successImg;

    /**
     * 暂停转账截图
     * 表 : t_order_detail
     * 对应字段 : pause_img
     */
    private String pauseImg;


    /**
     * 取订单取消时间
     * 表 : t_order_detail
     * 对应字段 : order_cancel_time
     */
    private Date orderCancelTime;


    //订单超时剩余时间。单位为秒
    private long countTime;

    private String operation_1;

    private String operation_2;

    private String operation_3;

    private String operation_url_1;

    private String operation_url_2;

    private String operation_url_3;

    public String getPrimaryId() {
        return primaryId;
    }

    public void setPrimaryId(String primaryId) {
        this.primaryId = primaryId;
    }

    public String getSalerRemark() {
        return salerRemark;
    }

    public void setSalerRemark(String salerRemark) {
        this.salerRemark = salerRemark;
    }

    public BigDecimal getSaleAmout() {
        return saleAmout;
    }

    public void setSaleAmout(BigDecimal saleAmout) {
        this.saleAmout = saleAmout;
    }

    public Short getPayWayId() {
        return payWayId;
    }

    public void setPayWayId(Short payWayId) {
        this.payWayId = payWayId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getBuyerCardName() {
        return buyerCardName;
    }

    public void setBuyerCardName(String buyerCardName) {
        this.buyerCardName = buyerCardName;
    }

    public String getBuyerCardAddress() {
        return buyerCardAddress;
    }

    public void setBuyerCardAddress(String buyerCardAddress) {
        this.buyerCardAddress = buyerCardAddress;
    }

    public String getBuyerCardRemark() {
        return buyerCardRemark;
    }

    public void setBuyerCardRemark(String buyerCardRemark) {
        this.buyerCardRemark = buyerCardRemark;
    }

    public String getBuyerRemark() {
        return buyerRemark;
    }

    public void setBuyerRemark(String buyerRemark) {
        this.buyerRemark = buyerRemark;
    }

    public String getSalerCardName() {
        return salerCardName;
    }

    public void setSalerCardName(String salerCardName) {
        this.salerCardName = salerCardName;
    }

    public String getSalerCardAddress() {
        return salerCardAddress;
    }

    public void setSalerCardAddress(String salerCardAddress) {
        this.salerCardAddress = salerCardAddress;
    }

    public String getSalerCardRemark() {
        return salerCardRemark;
    }

    public void setSalerCardRemark(String salerCardRemark) {
        this.salerCardRemark = salerCardRemark;
    }

    public Short getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Short payStatus) {
        this.payStatus = payStatus;
    }

    public Date getSalerCheckTime() {
        return salerCheckTime;
    }

    public void setSalerCheckTime(Date salerCheckTime) {
        this.salerCheckTime = salerCheckTime;
    }

    public Date getBuyerPayTime() {
        return buyerPayTime;
    }

    public void setBuyerPayTime(Date buyerPayTime) {
        this.buyerPayTime = buyerPayTime;
    }

    public String getBuyerRefId() {
        return buyerRefId;
    }

    public void setBuyerRefId(String buyerRefId) {
        this.buyerRefId = buyerRefId;
    }

    public Date getSalerConfirmTime() {
        return salerConfirmTime;
    }

    public void setSalerConfirmTime(Date salerConfirmTime) {
        this.salerConfirmTime = salerConfirmTime;
    }

    public Date getSalerPauseTime() {
        return salerPauseTime;
    }

    public void setSalerPauseTime(Date salerPauseTime) {
        this.salerPauseTime = salerPauseTime;
    }

    public Date getOrderFinishTime() {
        return orderFinishTime;
    }

    public void setOrderFinishTime(Date orderFinishTime) {
        this.orderFinishTime = orderFinishTime;
    }

    public String getSuccessImg() {
        return successImg;
    }

    public void setSuccessImg(String successImg) {
        this.successImg = successImg;
    }

    public String getPauseImg() {
        return pauseImg;
    }

    public void setPauseImg(String pauseImg) {
        this.pauseImg = pauseImg;
    }

    public Date getOrderCancelTime() {
        return orderCancelTime;
    }

    public void setOrderCancelTime(Date orderCancelTime) {
        this.orderCancelTime = orderCancelTime;
    }

    public long getCountTime() {
        return countTime;
    }

    public void setCountTime(long countTime) {
        this.countTime = countTime;
    }

    public String getOperation_1() {
        return operation_1;
    }

    public void setOperation_1(String operation_1) {
        this.operation_1 = operation_1;
    }

    public String getOperation_2() {
        return operation_2;
    }

    public void setOperation_2(String operation_2) {
        this.operation_2 = operation_2;
    }

    public String getOperation_3() {
        return operation_3;
    }

    public void setOperation_3(String operation_3) {
        this.operation_3 = operation_3;
    }

    public String getOperation_url_1() {
        return operation_url_1;
    }

    public void setOperation_url_1(String operation_url_1) {
        this.operation_url_1 = operation_url_1;
    }

    public String getOperation_url_2() {
        return operation_url_2;
    }

    public void setOperation_url_2(String operation_url_2) {
        this.operation_url_2 = operation_url_2;
    }

    public String getOperation_url_3() {
        return operation_url_3;
    }

    public void setOperation_url_3(String operation_url_3) {
        this.operation_url_3 = operation_url_3;
    }

    public String getReferId() {
        return referId;
    }

    public void setReferId(String referId) {
        this.referId = referId;
    }
}
