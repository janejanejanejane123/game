package com.ruoyi.game.domain.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class BetResponse {
    private BigDecimal score;
    private BigDecimal availableScore;//可用分数
    private BigDecimal winScore;//赢得分数
    private List<String> result;//投注结果
}
