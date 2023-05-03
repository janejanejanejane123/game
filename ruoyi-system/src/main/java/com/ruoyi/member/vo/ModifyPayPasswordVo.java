package com.ruoyi.member.vo;

import com.ruoyi.common.bussiness.constants.Constant;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/3/23,11:21
 * @return:
 **/
public class ModifyPayPasswordVo {

    @NotBlank(message = "member.info.payPasswordEmpty")
    @Pattern(regexp = Constant.PASSWORD_PATTEN)
    private String payPassword;


    private String password;

    public String getPayPassword() {
        return payPassword;
    }

    public void setPayPassword(String payPassword) {
        this.payPassword = payPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //@NotBlank(message = "member.info.passwordEmpty")


}
