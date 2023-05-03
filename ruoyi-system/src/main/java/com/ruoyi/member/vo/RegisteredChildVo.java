package com.ruoyi.member.vo;

import com.ruoyi.common.bussiness.constants.Constant;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/7/6,11:02
 * @return:
 **/
@Data
public class RegisteredChildVo {

    @NotNull
    @Pattern(regexp = Constant.USERNAME_PATTEN,message = "member.info.usernamePatternError")
    private String username;

    @NotNull
    @Pattern(regexp = Constant.PASSWORD_PATTEN,message = "member.info.passwordPatternError")
    private String password;

    @NotNull
    @Pattern(regexp = Constant.REAL_NAME,message = "member.info.realName.patternError")
    private String realName;
}
