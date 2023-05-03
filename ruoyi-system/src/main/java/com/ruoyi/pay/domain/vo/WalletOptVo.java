package com.ruoyi.pay.domain.vo;

import com.ruoyi.pay.domain.Wallet;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletOptVo {
    /**
     * 验证码
     */
    private Long code;
    /**
     *
     */
    private Long id;

    /**
     *
     */
    private BigDecimal amount;

    /**
     *
     */
    private Long uid;

    /**
     *
     */
    private String name;

    /**
     *
     */
    private String remark;

    private String type;
}
