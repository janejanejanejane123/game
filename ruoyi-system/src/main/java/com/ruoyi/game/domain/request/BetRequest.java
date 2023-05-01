package com.ruoyi.game.domain.request;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class BetRequest {
    private String account;
    private BigDecimal totalBet;//总投注数
    private BigDecimal betMultiplier;//投注乘数
}
