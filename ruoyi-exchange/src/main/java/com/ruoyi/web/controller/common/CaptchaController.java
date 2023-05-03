package com.ruoyi.web.controller.common;

import com.alibaba.fastjson.JSON;
import com.google.code.kaptcha.Producer;
import com.ruoyi.common.bussiness.captcha.RotateCaptchaParams;
import com.ruoyi.common.constant.CacheConstant;
import com.ruoyi.common.constant.ConfigKeyConstants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.utils.http.CookiesUtils;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.common.utils.sign.Base64;
import com.ruoyi.member.vo.CaptchaVo;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.web.controller.tool.ImagReader;
import com.ruoyi.web.controller.tool.RSAKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码操作处理
 * 
 * @author ruoyi
 */
@RestController
public class CaptchaController
{
    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    @Resource(name = "captcha2Read")
    private Producer captcha2Read;


    @Autowired
    private RedisCache redisCache;
    
    @Autowired
    private ISysConfigService configService;
    @Resource
    private RSAKeyGenerator rsaKeyGenerator;
    @Resource
    private ImagReader imagReader;
    /**
     * 生成验证码
     */
    @GetMapping("/captchaImage")
    public AjaxResult getCode() throws UnsupportedEncodingException {
        String s = CookiesUtils.trCreate();
        AjaxResult ajax = AjaxResult.success();

        // 保存验证码信息
        Random random = new Random();
        int i = random.nextInt(180) + 90;
        double v = Math.toRadians(i);
        BufferedImage rotateImage = imagReader.getRotateImage(v);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try
        {
            ImageIO.write(rotateImage, "png", os);
        }
        catch (IOException e)
        {
            return AjaxResult.error(e.getMessage());
        }
        Map<String, String> currentPublicKey = rsaKeyGenerator.getCurrentPublicKey();
        RotateCaptchaParams params = new RotateCaptchaParams();
        String iv = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);

        params.setAngle(i);
        params.setIv(iv);
        params.setPointer(currentPublicKey.get("pointer"));

        redisCache.setCacheObject(CacheConstant.CAPTCHA_INFO +s,params,120,TimeUnit.SECONDS);
        redisCache.setCacheObject(CacheConstant.CAPTCHA_STATUS + s,CacheConstant.CAPTCHA_STATUS_AWAIT,120,TimeUnit.SECONDS);
        redisCache.deleteObject("captchaWrongTimes:"+s);
        CookiesUtils.setHeader("Accept-Codes","gzip, deflate, br;"+currentPublicKey.get("pbk"));
        CookiesUtils.setHeader("Accept-Languages","zh-CN,zh;q=0.9,en;q=0.8;country="+iv);
        ajax.put("img", Base64.encode(os.toByteArray()));

        CookiesUtils.setCookie("emp-id", s,3600);

        return ajax;
    }

    @PostMapping("/verifyCaptcha")
    public AjaxResult verifyCaptcha(@RequestBody CaptchaVo captchaVo){
        return AjaxResult.success(true);
    }




    @GetMapping("/captchaImage4Read")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult getCodeForRegistered(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String configVideoReg = configService.selectConfigByKey(ConfigKeyConstants.USER_VIDEO_REGISTERED);
        if (Convert.toBool(configVideoReg)){
            AjaxResult ajax = AjaxResult.success();
            String uuid = CookiesUtils.trCreate();;
            String text = captcha2Read.createText();

            //5分钟过期;
            redisCache.setCacheObject("Captcha2Read:"+uuid,text,5,TimeUnit.MINUTES);


            CookiesUtils.setCookie("emp-id", uuid,3600);
            ajax.put("img",text);
            return ajax;
        }
        return AjaxResult.success();
    }
}
