package com.ruoyi.common.core.domain.model.member;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/7/19,15:26
 * @return:
 **/
public class TUserVo extends TUser {

    private String walletAddress;

    private BigDecimal balance;

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }
}
