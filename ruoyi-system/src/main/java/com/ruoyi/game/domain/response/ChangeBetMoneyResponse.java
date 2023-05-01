package com.ruoyi.game.domain.response;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ChangeBetMoneyResponse {
    private String account;
    private BigDecimal changeBetMoney;
    private BigDecimal totalBet;
    private BigDecimal betMultiplier;
}
