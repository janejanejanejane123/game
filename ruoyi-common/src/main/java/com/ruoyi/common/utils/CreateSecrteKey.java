package com.ruoyi.common.utils;
/**
 * 引进的包都是Java自带的jar包
 * 秘钥相关包
 * base64 编解码
 * 这里只用到了编码
 */

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

public class CreateSecrteKey {


    public static final String KEY_ALGORITHM = "RSA";
    //public static final String SIGNATURE_ALGORITHM = "MD5withRSA";
    private static final String PUBLIC_KEY = "RSAPublicKey";
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    //获得公钥
    private static String getPublicKey(Map<String, Object> keyMap) throws Exception {
        //获得map中的公钥对象 转为key对象
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        //byte[] publicKey = key.getEncoded();
        //编码返回字符串
        return encryptBASE64(key.getEncoded());
    }

    //获得私钥
    private static String getPrivateKey(Map<String, Object> keyMap) throws Exception {
        //获得map中的私钥对象 转为key对象
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        //byte[] privateKey = key.getEncoded();
        //编码返回字符串
        return encryptBASE64(key.getEncoded());
    }

    //解码返回byte
    public static byte[] decryptBASE64(String key) throws Exception {
        return (new BASE64Decoder()).decodeBuffer(key);
    }

    //编码返回字符串
    public static String encryptBASE64(byte[] key) throws Exception {
        return (new BASE64Encoder()).encodeBuffer(key);
    }

    //map对象中存放公私钥
    public static Map<String, Object> initKey() throws Exception {
        //获得对象 KeyPairGenerator 参数 RSA 1024个字节
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        //通过对象 KeyPairGenerator 获取对象KeyPair
        KeyPair keyPair = keyPairGen.generateKeyPair();

        //通过对象 KeyPair 获取RSA公私钥对象RSAPublicKey RSAPrivateKey
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        //公私钥对象存入map中
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    public static Map<String, String> getRsa() {
        try {
            Map rsaMap = new HashMap();
            Map<String, Object> keyMap = initKey();
            String publicKey = getPublicKey(keyMap).replace("\r", "").replace("\n", "").replace(" ", "").trim();

            String privateKey = getPrivateKey(keyMap).replace("\r", "").replace("\n", "").replace(" ", "").trim();
            rsaMap.put("publicKey", publicKey);
            rsaMap.put("privateKey", privateKey);
            return rsaMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) {
        Map<String, Object> keyMap;
        try {
            keyMap = initKey();
            String publicKey = getPublicKey(keyMap).replace("\r", "").replace("\n", "").replace(" ", "").trim();

            System.out.println(publicKey);
            String privateKey = getPrivateKey(keyMap).replace("\r", "").replace("\n", "").replace(" ", "").trim();

            System.out.println(privateKey);

            Map<String, String> rsa = getRsa();
            publicKey = rsa.get("publicKey");
            privateKey = rsa.get("privateKey");

       //     publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCR+6LuPM/nxEK22i1a7sLdAmM4uhtHL00iDSicmNh1JBGnVZPX0dpLnuGZ6/JQNyNt46yO/BLPZZ9KKi5dIe9cDUudF2Un5wATWCWM/g9KtDkHjrtnDCx5BJBRprBDfs3VxcCS1GPTugRfr4Ovz327vnGpOmEAU6EAo5pCBhDhmQIDAQAB";
        //    privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJ8MnGRU0jjlSiH45xe0VlnVa8Bm2djHMhgOkPMWOwzvZCMML8EZ5T8XBonW1oo47/0WNSrUMiPZduzw8BvLMEkiaGuMPRJ/MSZgqpYN8U1LzzuILmZN6R5Nmyls1JukmuoEznm5fbluBrMtv6GVBoE5fBkzVqyXTgJWdGK1yr4xAgMBAAECgYAxcUUot2mGgFjUSMDmEIyLbpoJfK+aAQpkihqrRqzir4SMlJw/xiIKw8injt6wp1SLc5zhjIdtt4uNyrSJn1eJ1yvs0y1jbfRMux8Uriz7R8i97wWN+qhWxVRuq8YZXGYn8W18x6kyRC2jXgr2blFug4ZgT86yW3Jp9/Oc9rjLhQJBAM1Mkar+XGLOpwf8NvAXnZ9QYckTTeN2zMF9LskXxQlOJikFtTianmdH8bkXbdxTLeT40+mWUVnR3vrQfyUp02MCQQDGVAg7twhMagDK3hkvfyuMhzkMIIyB1v05MZ6BcumzPlXdMuHEWjdep9d2T8JQHklUEwlMIE4+2EoUqX6IbR5bAkBnNKDeJzTmbyDFAOPVWungVxZxcQTs3qIcewP0D8D75o+Ee9wYcpJDNYWtGLwf0Kj3vKc7fFe1Ia5IxFvGoPolAkEAxN0S0xuK3Db4lGbDvzbPZo2vXc9NSXC7KLONL9d+DNMabhgcrEgU4Btgr2raaaa2iQLgXrVOlekiF8Jq/Ea8MwJBAKWUVya+9XGv6Ig2RwwISq4QSPEm6Tc2lAG1sToMGfv6n/Uet4FIASmApvU1SAoIzW8HicwggpUei4wc7I7RZco=";
            String sign = RSAUtil.signBySHA1WithRSA("amount=500.00&merNo=QWZA&note=1647254028976_1111&notifyurl=http://baidu.com/1/1647254028976_1&orderid=1647254028976_1&pay_id=8101&returnurl=http://baidu.com", privateKey);
            System.out.println(sign);
            boolean b = RSAUtil.rsaVerify(publicKey, sign, "amount=500.00&merNo=QWZA&note=1647254028976_1111&notifyurl=http://baidu.com/1/1647254028976_1&orderid=1647254028976_1&pay_id=8101&returnurl=http://baidu.com");
            System.out.println(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}