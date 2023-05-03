package com.ruoyi.web.core.config;

import com.ruoyi.common.bussiness.captcha.CaptchaContext;
import com.ruoyi.member.vo.CaptchaVo;
import com.ruoyi.web.controller.tool.CaptcachaContextListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/4/6,19:32
 * @return:
 **/
@Configuration
public class AppConfig {

    @Resource
    private CaptcachaContextListener captcachaContextListener;


    @Bean
    public CaptchaContext captchaContext(){

        CaptchaContext captchaContext = new CaptchaContext();
        captchaContext.addMetaInfo(CaptchaVo.class);
        captchaContext.addListener(CaptchaVo.class,captcachaContextListener);
        return captchaContext;
    }
}
