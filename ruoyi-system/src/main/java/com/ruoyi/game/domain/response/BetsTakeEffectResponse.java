package com.ruoyi.game.domain.response;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class BetsTakeEffectResponse {
    private String account;
    private BigDecimal winScore;
}
