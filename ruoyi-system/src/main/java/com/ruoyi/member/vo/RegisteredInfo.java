package com.ruoyi.member.vo;

import com.ruoyi.common.annotation.DecryptField;
import com.ruoyi.common.bussiness.constants.Constant;
import com.ruoyi.member.util.ConfigCheck;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/3/15,15:48
 * @return:
 **/
@Data
@ToString
public class RegisteredInfo {

    @NotNull
    @Pattern(regexp = Constant.USERNAME_PATTEN,message = "member.info.usernamePatternError")
    private String username;


    @NotNull
    @Pattern(regexp = Constant.PASSWORD_PATTEN,message = "member.info.passwordPatternError")
    private String password;




    @NotNull
    @Pattern(regexp = Constant.REAL_NAME,message = "member.info.realName.patternError")
    private String realName;


    @ConfigCheck(confKey = "telephone",message = "member.info.phone.patternError")
    private String telephone;

    @ConfigCheck(confKey = "invite")
    private String invite;

    @ConfigCheck(confKey = "email",message = "member.info.email.patternError")
    private String email;
}
