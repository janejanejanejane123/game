package com.ruoyi.game.domain.request;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class GetTokenRequest {
    private String apiKey;
    private String userId;
}
