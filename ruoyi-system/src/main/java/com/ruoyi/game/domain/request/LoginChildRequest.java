package com.ruoyi.game.domain.request;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class LoginChildRequest {
    private String s;
    private String account;
    private String money;
    private String orderid;
    private String ip;
    private String lineCode;
    private String KindID;
}
