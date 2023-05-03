package com.ruoyi.order.domain.vo;

import com.ruoyi.order.domain.GrabOrder;

public class OrderVo extends GrabOrder {

    private Long countTime;

    private String operation_1;

    private String operation_2;

    private String operation_3;

    private String operation_url_1;

    private String operation_url_2;

    private String operation_url_3;

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

    public Long getCountTime() {
        return countTime;
    }

    public void setCountTime(Long countTime) {
        this.countTime = countTime;
    }
}
