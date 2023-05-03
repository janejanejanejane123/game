package com.ruoyi.order.domain.vo;

import com.ruoyi.order.domain.UserOrder;

public class UserOrderVo extends UserOrder {

    private Long cardId;

    private String payPassword;

    private String emailCode;

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getPayPassword() {
        return payPassword;
    }

    public void setPayPassword(String payPassword) {
        this.payPassword = payPassword;
    }

    public String getEmailCode() {
        return emailCode;
    }

    public void setEmailCode(String emailCode) {
        this.emailCode = emailCode;
    }
}
