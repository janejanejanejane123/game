package com.ruoyi.member.vo;

import com.ruoyi.common.bussiness.constants.Constant;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/5/17,12:17
 * @return:
 **/
@Data
public class EmailVo {

    @NotNull
    @Pattern(regexp = Constant.EMAIL_PATTEN)
    private String email;

    private String verify;

}
