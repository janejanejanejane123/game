package com.ruoyi.common.utils.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 *  * @ClassName Base64Util
 *  * @Description base64工具类
 *  * @Author aping
 *  * @Date 2010年02月28日 15:11
 *  * @Version 1.0.0
 *  
 **/
public class Base64Util {

    //日志
    public static final Logger logger = LoggerFactory.getLogger(Base64Util.class);

    static final String CHARSET_UTF_8="UTF-8";

    /**
     * 功能描述:
     * BASE64加密算法
     * @Author: Hardy
     * @Date: 2018年08月31日 15:14:29
     * @param data
     * @return: java.lang.String
     **/
    private String encryptBASE64(byte[] data) throws Exception{
        String encode = new BASE64Encoder().encode(data);
        return encode;
    }

    /**
     * 功能描述:
     * BASE64解密算法
     * @Author: Hardy
     * @Date: 2018年08月31日 15:14:39
     * @param data
     * @return: byte[]
     **/
    private byte[] decryptBASE64(String data) throws Exception {
        byte[] code = new BASE64Decoder().decodeBuffer(data);
        return code;
    }

    /**
     * 功能描述:
     * 加密
     * @Author: Hardy
     * @Date: 2018年08月31日 15:23:11
     * @param source 待加密原串
     * @return: java.lang.String
     **/
    public static String encryptStr(String source) throws Exception{
        Base64Util base = new Base64Util();
        byte[] strByte = source.getBytes(CHARSET_UTF_8);
        String str = base.encryptBASE64(strByte);
        return str;
    }

    /**
     * 功能描述:
     * 待解密原串
     * @Author: Hardy
     * @Date: 2018年08月31日 15:23:52
     * @param source
     * @return: java.lang.String
     **/
    public static String decryptStr(String source) throws Exception{
        Base64Util base = new Base64Util();
        byte[] strByte = base.decryptBASE64(source);
        String str = new String(strByte);
        return str;
    }

    public static void main(String[] args) {
        String str = "我们是冠军!";
        try {
            String encryptStr = encryptStr(str);
            System.out.println("加密后结果:"+encryptStr);
            String decryptStr = decryptStr(encryptStr);
            System.out.println("解密后结果:"+decryptStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
