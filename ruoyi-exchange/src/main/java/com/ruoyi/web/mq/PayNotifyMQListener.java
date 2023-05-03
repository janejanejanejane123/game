package com.ruoyi.web.mq;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.http.HttpUtil;
import com.ruoyi.pay.domain.vo.PayNotifyVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Decoder;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

@Component
@RocketMQMessageListener(topic = "PAY_NOTIFY_STATUS", selectorExpression = "*", consumerGroup = "payNotifyStatus")
public class PayNotifyMQListener implements RocketMQListener<PayNotifyVO> {

    private Logger logger = LoggerFactory.getLogger(PayNotifyMQListener.class);
    @Resource
    private RedisCache redisCache;

    //static private String PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCoj/RPYiPXNmnystH3O/Lt6fTWzHdeIEk4f0cOIsbnPRKjgXycxE1v/G88Kyha6at8LfgGkwgZ9L6RaaQXskGc+CdARdANvq8Tdm9CpjsvntYGNcD0BkSwTNVG1htN5AZJQxMTTHHVcFwXonR/dQYBl+kT3vmyeaXPYPLnIh0x2QIDAQAB";

    @Override
    public void onMessage(PayNotifyVO orderVo) {
        logger.info("[单号:{}]支付成功回调参数{}:", orderVo.getOrderid(), JSONObject.toJSONString(orderVo));
        String s = null;
        Object cacheObject = redisCache.getCacheObject("API_WITHDRAWALRECORD_NOTIFY:" + orderVo.getMerNo() + ":" + orderVo.getOrderid());
        if (cacheObject != null && Integer.parseInt(cacheObject.toString()) <= 4) {
            int i = Integer.parseInt(cacheObject.toString());
            try {
                String url = orderVo.getNotifyUrl();
                orderVo.setNotifyUrl(null);
                // notify(JSONObject.parseObject(JSONObject.toJSONString(orderVo)));
                s = HttpUtil.toPostJson(JSONObject.toJSONString(orderVo), url);
                logger.info("[单号:{}]回调返回的报文:{}，回调次数:{}", orderVo.getOrderid(), s,i);
            } catch (Exception e) {
                logger.error("回调异常" + orderVo.getOrderid());
            }
            if (!"success".equals(s) && i <= 3) {
                redisCache.setCacheObject("API_WITHDRAWALRECORD_NOTIFY:" + orderVo.getMerNo() + ":" + orderVo.getOrderid(), ++i, 60, TimeUnit.SECONDS);
                logger.info("[单号:{}]回调失败,已重试次数:{}", orderVo.getOrderid(), i);
                throw new ServiceException("回调失败");
            }
        }
    }

    /*  *//**
     * 回调
     *
     * @param data
     * @return
     *//*
    public static String notify(Map<String, Object> data) {
        String status = String.valueOf(data.get("status"));
        data.remove("note");
        if ("1".equals(status)) {
            String sign = String.valueOf(data.remove("sign").toString());
            boolean verify = rsaVerify(PUBLIC, sign, getStringByMap(data));
            //对回调参数进行签名
            //验签
            if (verify) {
                System.out.println("验签通过");
                return "success";
            } else return "fail";
        }
        return "fail";
    }*/

    /**
     * 请求参数排序
     *
     * @param map
     * @return
     */
    public static String getStringByMap(Map<String, Object> map) {
        SortedMap<String, Object> smap = new TreeMap<String, Object>(map);
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> m : smap.entrySet()) {
            sb.append(m.getKey()).append("=").append(m.getValue()).append("&");
        }
        sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }

    /**
     * 验签
     */
    public static boolean rsaVerify(String publicKey, String sign, String signStr) {
        try {
            RSAPublicKey rsaPublicKey = loadPublicKey(publicKey);
            if (publicKey == null) {
                throw new Exception("解密私钥为空, 请设置");
            }
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initVerify(rsaPublicKey);
            signature.update(signStr.getBytes());

            //把签名反解析，并验证
            byte[] decodeSign = new BASE64Decoder().decodeBuffer(sign);
            return signature.verify(decodeSign);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr 公钥数据字符串
     * @throws Exception 加载公钥时产生的异常
     */
    public static RSAPublicKey loadPublicKey(String publicKeyStr) throws Exception {
        try {
            BASE64Decoder base64Decoder = new BASE64Decoder();
            byte[] buffer = base64Decoder.decodeBuffer(publicKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("公钥非法");
        } catch (IOException e) {
            throw new Exception("公钥数据内容读取错误");
        } catch (NullPointerException e) {
            throw new Exception("公钥数据为空");
        }
    }
}
