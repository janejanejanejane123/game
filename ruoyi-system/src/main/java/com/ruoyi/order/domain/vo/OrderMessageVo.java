package com.ruoyi.order.domain.vo;

public class OrderMessageVo {

    private String orderDetailId;

    private String orderPoolId;

    private Short orderBeforeStatus;

    private Short orderAfterStatus;

    public String getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(String orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public String getOrderPoolId() {
        return orderPoolId;
    }

    public void setOrderPoolId(String orderPoolId) {
        this.orderPoolId = orderPoolId;
    }

    public Short getOrderBeforeStatus() {
        return orderBeforeStatus;
    }

    public void setOrderBeforeStatus(Short orderBeforeStatus) {
        this.orderBeforeStatus = orderBeforeStatus;
    }

    public Short getOrderAfterStatus() {
        return orderAfterStatus;
    }

    public void setOrderAfterStatus(Short orderAfterStatus) {
        this.orderAfterStatus = orderAfterStatus;
    }
}
