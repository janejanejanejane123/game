package com.ruoyi.game.domain.request;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class BetsTakeEffectRequest {
    private String account;
    private BigDecimal winScore;
}
