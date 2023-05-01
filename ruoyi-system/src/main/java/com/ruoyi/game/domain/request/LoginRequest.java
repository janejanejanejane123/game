package com.ruoyi.game.domain.request;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class LoginRequest {
    private String agent;
    private String timestamp;
    private String param;
    private String key;
}
