package com.ruoyi.pay.domain.vo;

import com.ruoyi.common.bussiness.constants.Constant;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

@Data
public class PayOrderVo {

    @NotNull(message = "蛋数量不能为空")
    @DecimalMin(value = "1",message = "最低蛋数量不能小于1")
    private String amount;

    @NotNull(message = "商户号不能为空")
    private String merNo;
    @NotNull(message = "签名不能为空")
    private String sign;
    @NotNull(message = "商户单号不能为空")
    private String orderid;

    /**
     * 账号
     */
  //  @NotNull(message = "账号不能为空")
    @Pattern(regexp = Constant.USERNAME_PATTEN,message = "账号只能是a-zA-Z0-9(长度6-20位)")
    private String account;

    /**
     * 真实姓名
     */
    private String realName;

    private String notifyurl;

    private String note;

    private String address;

    private String  payWay;

    private String ip;
}
