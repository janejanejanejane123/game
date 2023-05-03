package com.ruoyi.order.domain.vo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * grab对象 t_grab_order
 * 
 * @author ry
 * @date 2022-07-15
 */
public class GrabOrderVo
{
    private static final long serialVersionUID = 1L;

    private String id;

    private BigDecimal amount;

    private Integer rechargeWay;

    private Date createTime;

    private Long countTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getRechargeWay() {
        return rechargeWay;
    }

    public void setRechargeWay(Integer rechargeWay) {
        this.rechargeWay = rechargeWay;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getCountTime() {
        return countTime;
    }

    public void setCountTime(Long countTime) {
        this.countTime = countTime;
    }
}
