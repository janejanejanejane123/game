package com.ruoyi.web.controller.tool;

import com.ruoyi.common.bussiness.ContextListener;
import com.ruoyi.common.bussiness.RequestContext;
import com.ruoyi.common.bussiness.captcha.RotateCaptchaParams;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DecryptContext;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.member.vo.CaptchaVo;
import org.springframework.stereotype.Component;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/4/6,19:45
 * @return:
 **/
@Component
public class CaptcachaContextListener implements ContextListener<CaptchaVo> {


    @Override
    public void beforeDecrypt(CaptchaVo captchaVo, DecryptContext context) {
        //开始注册解密
    }

    @Override
    public void afterDecryptField(CaptchaVo captchaVo,DecryptContext context) {
        //获取偏移
        RotateCaptchaParams captcha = context.getObject();
        //角度;
        String a = captchaVo.getA();

        double v = Double.parseDouble(a);

        v=convertAngel(v);
        int angle = captcha.getAngle();

        if ( Math.abs(v-angle) > 10 && Math.abs(360-(v+angle))>10){
            throw new ServiceException(MessageUtils.message("captcha.wrong.time"),1001);
        };
        //验证轨迹
        String captchaID = RequestContext.getParam("CaptContextID");


    }

    private double convertAngel(double v) {
        while (v>360){
            v=v-360;
        }
        return v;
    }
}
