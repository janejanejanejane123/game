package com.ruoyi.pay.domain.vo;

import com.ruoyi.pay.domain.TransferRecord;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TransferRecordVo extends TransferRecord {

    @NotNull(message = "验证码不能为空")
    private Long code;

  //  @NotNull(message = "密码不能为空")
    private String password;

}
