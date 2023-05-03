package com.ruoyi.member.vo;

import com.ruoyi.common.bussiness.constants.Constant;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/3/26,12:12
 * @return:
 **/
public class ModifyPasswordVo {


    @NotBlank(message = "member.info.newpasswordEmpty")
    private String password;


    @NotBlank(message = "member.info.passwordEmpty")
    @Pattern(regexp = Constant.PASSWORD_PATTEN , message = "member.info.passwordPatternError")
    private String newPassword;

    public String getPassword() {
        return password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setPassword(String password) {
        this.password = password;
    }



}
