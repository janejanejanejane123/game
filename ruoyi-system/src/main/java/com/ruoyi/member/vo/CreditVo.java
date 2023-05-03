package com.ruoyi.member.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/3/27,13:04
 * @return:
 **/
@Data
public class CreditVo {

    @NotBlank
    private String content;

    @NotNull
    private short creditType;

    private String realName;

    @NotBlank
    private String creditAddress;

    @NotBlank
    private String creditBank;

    @NotBlank
    private String payPassword;

//    @NotBlank
//    private String verify;

    private Long id;

}
