package com.ruoyi.common.bussiness.captcha;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/4/8,11:28
 * @return:
 **/

@Data
public class RotateCaptchaParams {

    private String iv;

    private String pointer;

    private int angle;

    private transient String currentStatus;

    private transient JSONArray keyPair;


}
