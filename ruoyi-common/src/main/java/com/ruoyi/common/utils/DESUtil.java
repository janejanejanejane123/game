
package com.ruoyi.common.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.IOException;
import java.security.SecureRandom;

/**
 *  * @ClassName DESUtil
 *  * @Description DES加密、解密工具类
 *  * 
 *  * @Date 2018年09月20日 11:22
 *  * @Version 1.0.0
 *  
 **/
public class DESUtil {

    private static final String key = "EPAYAPI$$$";


    private static final String USER_INFO_KEY ="userDesByST57567123";
    

    public static String encryptUserInfo(String input) throws Exception{
        Assert.isNull(input,"系统错误693456");
        String inputStr = String.valueOf(input);
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(USER_INFO_KEY.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, key, sr);
        byte[] data = inputStr.getBytes();
        byte[] encryptedData = cipher.doFinal(data);
        BASE64Encoder b = new BASE64Encoder();
        return b.encode(encryptedData);
    }


    public static String decryptUserInfo(String encrypt) throws Exception{
        DESUtil desUtil = new DESUtil();
        Assert.isFalse(StringUtils.isNotBlank(encrypt),"系统错误693457");
        byte[] bytes = desUtil.base64Decode(encrypt);
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(USER_INFO_KEY.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, key, sr);
        byte[] decryptedData = cipher.doFinal(bytes);
        return new String(decryptedData);
    }

    /**
     * 功能描述:
     * 加密
     * 
     * @Date:2018年09月20日 11:24:53
     * @param input
     * @return: java.lang.String
     **/
    public static String encrypt(String input) throws Exception {

        DESUtil desUtil = new DESUtil();

        return desUtil.base64Encode(desUtil.desEncrypt(input.getBytes())).replaceAll("\\s*", "");
    }

    /**
     * 功能描述:
     * 解密
     * 
     * @Date:2018年09月20日 11:25:05
     * @param input
     * @return: java.lang.String
     **/
    public static String decrypt(String input) throws Exception {
        DESUtil desUtil = new DESUtil();
        byte[] result = desUtil.base64Decode(input);
        return new String(desUtil.desDecrypt(result));
    }

    private byte[] desEncrypt(byte[] plainText) throws Exception {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(key.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, key, sr);
        byte data[] = plainText;
        byte encryptedData[] = cipher.doFinal(data);
        return encryptedData;
    }

    private byte[] desDecrypt(byte[] encryptText) throws Exception {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(key.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, key, sr);
        byte encryptedData[] = encryptText;
        byte decryptedData[] = cipher.doFinal(encryptedData);
        return decryptedData;
    }

    private String base64Encode(byte[] s) {
        if (s == null)
            return null;
        BASE64Encoder b = new BASE64Encoder();
        return b.encode(s);
    }

    private byte[] base64Decode(String s) throws IOException {
        if (s == null) {
            return null;
        }
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] b = decoder.decodeBuffer(s);
        return b;
    }
}
