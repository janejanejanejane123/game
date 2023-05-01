
package com.ruoyi.common.utils;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 *  * @ClassName RSAUtil
 *  * @Description RSA加密工具类
 * @Author: 
 * @Date: 2018年12月09日 15:26:20
 *  * @Version 1.0.0
 *  
 **/
public class RSAUtil {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(RSAUtil.class);

    private static final String ALG_RSA = "RSA";

    private static final String ALG_DSA = "DSA";

    public static final String SIGNATURE_ALGORITHM_MD5 = "MD5withRSA";

    public static final String SIGNATURE_ALGORITHM_SHA = "SHA1WithRSA";

    public static final String CIPHER_INSTANCE = "RSA/ECB/PKCS1Padding";

    public static final String CHARSET = "UTF-8";

    /** *//**
     * RSA最大加密明文大小
     */
    public static final int MAX_ENCRYPT_BLOCK = 117;
    /** *//**
     * RSA最大解密密文大小
     */
    public static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 功能描述:
     * 签名
     * 私钥加密
     * @param data
     * @param privateKey
     * @Author: 典韦
     * @Date: 2022-03-14 15:26:20
     * @return: java.lang.String
     **/
    public static String signBySHA1WithRSA(String data, String privateKey) throws Exception {
        logger.info("=======================签名开始=====================================");
        try {
            byte[] priKeyByte = Base64.getDecoder().decode(privateKey.getBytes());
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(priKeyByte);
            KeyFactory keyFactory = KeyFactory.getInstance(ALG_RSA);
            PrivateKey priKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM_SHA);
            signature.initSign(priKey);
            signature.update(data.getBytes(CHARSET));
            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("签名异常:" + e.getMessage());
            throw new Exception("签名异常!");
        }
    }


    /** *//**
     * <p>
     * 私钥加密   分段加密
     * </p>
     *
     * @param data 源数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static String encryptByPrivateKey(String  data, String privateKey)
            throws Exception {
        byte[] bytes = data.getBytes("UTF-8");
        byte[] keyBytes = Base64.getDecoder().decode(privateKey.getBytes());
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALG_RSA);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateK);
        int inputLen = bytes.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(bytes, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(bytes, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return Base64.getEncoder().encodeToString(encryptedData);

    }

    /**
     * 功能描述:
     * 签名
     * 私钥加密
     * @param data
     * @param privateKey
     * @Author: 
     * @Date: 2018年12月09日 15:26:20
     * @return: java.lang.String
     **/
    public static String signByMd5WithRSA(String data, String privateKey) throws Exception {
        logger.info("=======================签名开始=====================================");
        try {
            byte[] priKeyByte = Base64.getDecoder().decode(privateKey.getBytes());
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(priKeyByte);
            KeyFactory keyFactory = KeyFactory.getInstance(ALG_RSA);
            PrivateKey priKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM_MD5);
            signature.initSign(priKey);
            signature.update(data.getBytes(CHARSET));
            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("签名异常:" + e.getMessage());
            throw new Exception("签名异常!");
        }
    }

    /**
     * 功能描述:
     * 验签
     *
     * @param source    原串
     * @param sign
     * @param publicKey
     * @Author: 
     * @Date: 2018年12月09日 15:26:20
     * @return: void
     **/
    public static boolean verifySignBySHA1WithRSA(String source, String sign, String publicKey) throws Exception {
        logger.info("==============================验签开始======================================");
        try {
            byte[] pubKeyByte = Base64.getDecoder().decode(publicKey.getBytes());
            KeyFactory keyFactory = KeyFactory.getInstance(ALG_RSA);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(pubKeyByte));
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM_SHA);
            signature.initVerify(pubKey);
            signature.update(source.getBytes(CHARSET));
            byte[] signByte = Base64.getDecoder().decode(sign.getBytes());
            if (signature.verify(signByte)) {
                return true;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.error("验签异常:" + e.getMessage());
        }
        return false;
    }

    /**
     * 功能描述:
     * 验签
     *
     * @param source    原串
     * @param sign
     * @param publicKey
     * @Author: 
     * @Date: 2018年12月09日 15:26:20
     * @return: void
     **/
    public static boolean verifySignByMD5withRSA(String source, String sign, String publicKey) throws Exception {
        logger.info("==============================验签开始======================================");
        try {
            byte[] pubKeyByte = Base64.getDecoder().decode(publicKey.getBytes());
            KeyFactory keyFactory = KeyFactory.getInstance(ALG_RSA);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(pubKeyByte));
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM_MD5);
            signature.initVerify(pubKey);
            signature.update(source.getBytes(CHARSET));
            byte[] signByte = Base64.getDecoder().decode(sign.getBytes());
            if (signature.verify(signByte)) {
                return true;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.error("验签异常:" + e.getMessage());
        }
        return false;
    }



    /**
     * 功能描述:
     * 私钥签名
     * @Author: 
     * @Date: 2018年12月09日 15:26:20
     * @param signSrc
     * @param priKey
     * @return: java.lang.String
     **/
    public static String signByHexString2ByteArr(String signSrc,String priKey) throws Exception {
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(hexString2ByteArr(priKey));
            KeyFactory keyFactory = KeyFactory.getInstance(ALG_RSA);
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            Signature si = Signature.getInstance(SIGNATURE_ALGORITHM_SHA);
            si.initSign(privateKey);
            si.update(signSrc.getBytes(CHARSET));
            byte[] dataSign = si.sign();
            return byteArr2HexString(dataSign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("RSA加密异常:"+e.getMessage());
            throw new Exception("RSA加密异常!");
        }
    }

    /**
     * 功能描述:
     * 验签
     * @Author: 
     * @Date: 2018年12月09日 15:26:20
     * @param data
     * @param sign
     * @param publicKey
     * @return: boolean
     **/
    public static boolean verifyByHexString2ByteArr(String data, String sign, String publicKey)throws Exception{
        try {
            Signature verf = Signature.getInstance(SIGNATURE_ALGORITHM_SHA);
            KeyFactory keyFac = KeyFactory.getInstance(ALG_RSA);
            PublicKey puk = keyFac.generatePublic(new X509EncodedKeySpec(hexString2ByteArr(publicKey)));
            verf.initVerify(puk);
            verf.update(data.getBytes(CHARSET));
            return verf.verify(hexString2ByteArr(sign));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("RSA验签异常:"+e.getMessage());
            throw new Exception("RSA验签异常!");
        }
    }

    /**
     * 功能描述:
     * 公钥加密
     * @Author: 
     * @Date: 2018年12月09日 15:26:20
     * @param data
     * @param pubKey
     * @return: java.lang.String
     **/
    public static String encrypt(String data,String pubKey) throws Exception{
        try {
            byte[] key = Base64.getDecoder().decode(pubKey);
            byte[] bytes = data.getBytes(CHARSET);
            // 实例化密钥工厂
            KeyFactory keyFactory = KeyFactory.getInstance(ALG_RSA);
            // 密钥材料转换
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
            // 产生公钥
            PublicKey publickey = keyFactory.generatePublic(x509KeySpec);
            // 数据加密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, publickey);
            int blockSize = cipher.getOutputSize(bytes.length) - 11;
            byte[] dataStr = getCipher(bytes, cipher,blockSize);
            return new BASE64Encoder().encode(dataStr);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("公钥加密异常:"+e.getMessage());
            throw new Exception("公钥加密异常!");
        }
    }

    /**
     * 功能描述:
     * 私钥解密 这个可以用
     * @Author: 
     * @Date: 2018年12月09日 15:26:20
     * @param data
     * @param priKey
     * @return: java.lang.String
     **/
    public static String decrypt(String data,String priKey) throws Exception{
        try {
            byte[] key = Base64.getDecoder().decode(priKey);
            byte[] bytes = new BASE64Decoder().decodeBuffer(data);
            // 取得私钥
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
            KeyFactory keyFactory = KeyFactory.getInstance(ALG_RSA);
            // 生成私钥
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            // 数据解密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            int blockSize = cipher.getOutputSize(bytes.length);
            byte[] dataStr = getCipher(bytes,cipher,blockSize);
            return new String(dataStr,CHARSET);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("私钥解密异常:"+e.getMessage());
            throw new Exception("私钥解密异常!");
        }
    }



    /**
     * 功能描述:
     * RSA-byte数组转字符串
     * @Author: 
     * @Date: 2018年12月09日 15:26:20
     * @param bytearr
     * @return: java.lang.String
     **/
    private static String byteArr2HexString(byte[] bytearr) {
        if (bytearr == null) {
            return "null";
        }
        StringBuffer sb = new StringBuffer();
        for (int k = 0; k < bytearr.length; k++) {
            if ((bytearr[k] & 0xFF) < 16) {
                sb.append("0");
            }
            sb.append(Integer.toString(bytearr[k] & 0xFF, 16));
        }
        return sb.toString();
    }

    /**
     * 功能描述:
     * RSA字符串转byte数组
     * @Author: 
     * @Date: 2018年12月09日 15:26:20
     * @param hexString
     * @return: byte[]
     **/
    private static byte[] hexString2ByteArr(String hexString) {
        if ((hexString == null) || (hexString.length() % 2 != 0)) {
            return new byte[0];
        }
        byte[] dest = new byte[hexString.length() / 2];
        for (int i = 0; i < dest.length; i++) {
            String val = hexString.substring(2 * i, 2 * i + 2);
            dest[i] = ((byte) Integer.parseInt(val, 16));
        }
        return dest;
    }

    private static byte[] getCipher(byte[] decryptData, Cipher cipher, int blockSize) throws Exception {
        int offSet = 0;
        byte[] cache = null;
        int i = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while (decryptData.length - offSet > 0) {
            if (decryptData.length - offSet > blockSize) {
                cache = cipher.doFinal(decryptData, offSet, blockSize);
            } else {
                cache = cipher.doFinal(decryptData, offSet, decryptData.length - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * blockSize;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    private static int getBlockSize(final Key key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final String alg = key.getAlgorithm();
        final KeyFactory keyFactory = KeyFactory.getInstance(alg);
        if (key instanceof PublicKey) {
            final BigInteger prime;
            if (ALG_RSA.equals(alg)) {
                prime = keyFactory.getKeySpec(key, RSAPublicKeySpec.class).getModulus();
            } else if (ALG_DSA.equals(alg)) {
                prime = keyFactory.getKeySpec(key, DSAPublicKeySpec.class).getP();
            } else {
                throw new NoSuchAlgorithmException("不支持的解密算法：" + alg);
            }
            return prime.toString(2).length() / 8;
        } else if (key instanceof PrivateKey) {
            final BigInteger prime;
            if (ALG_RSA.equals(alg)) {
                prime = keyFactory.getKeySpec(key, RSAPrivateKeySpec.class).getModulus();
            } else if (ALG_DSA.equals(alg)) {
                prime = keyFactory.getKeySpec(key, DSAPrivateKeySpec.class).getP();
            } else {
                throw new NoSuchAlgorithmException("不支持的解密算法：" + alg);
            }
            return prime.toString(2).length() / 8;
        } else {
            throw new RuntimeException("不支持的密钥类型：" + key.getClass());
        }
    }

    public static void main(String[] args) throws Exception {
        String data="lXEQmO90WnRQADE15QBs0bsMrUqekNW1tbbxIjb5K2L5vxjQx3TwYzmR9iuLSmLXhXQ1c3C30q6lT7SBaTHK4NoQYXsuIURnO2bnauoKXzqwsyaxFu3MefmyYwy/SsViQKXFWWcMM4HJKA/E3D8fXzXWQQWbJ5sBbrlVYgFLNIsWIdnrvZ8qI7gi5zN+YRXj9lGKSbVR6ZjERuT1rgm3uEjVu5h8vtp9KPRH7GCwIs95OiYc8YBzW2t5n4YVbcnPPYn2Zj6/gSI+vfoKbyXaIQ/jqD4T9ulG86Hi3RQVelY/r7Qn/7MT4MZIbpKrMEGHmbd8ja/JWUgZsGRooPZISfJNCeGejT4rVBKu0EWHXYozT+mSZPtzSIJcsttQCUBda7CMfLsIbhYu2TQZcynkV6achMNLoXjuMwd37s38w4SebcqGu3WYNEH4+6unl5kRIv8txNAUNdhRnEp8VMUXrj4gY4MTJFXjMASMDyoPV2/Yg5RPRZ6YKr/0DMWsKOKqfBeuICrYgwj/IDOHQwvjLuaXb1H/v5qKnphQ60ERBpnub+EMeUiSPNm6gCa3B87q/fedOg7Ut5e0p5M3Apd3NPoUedxvuEwheHptHlh7BpxMW5gSADfv/djGcMWMQa1EFEJNb7HuuEXy49hrjFWQO3iPuj4nRzdiJB18CS9KgOY=";
        String key="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";
        System.out.println(decrypt(data,key));




    }

    /**
     * 签名
     *
     * @param privateKey 私钥
     * @return
     * @throws SignatureException
     */
    public static String rsaSign(String privateKey, String signStr) throws SignatureException {
        try {
            RSAPrivateKey rsaPrivateKey = loadPrivateKey(privateKey);
            if (privateKey == null) {
                throw new Exception("加密公钥为空, 请设置");
            }
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM_SHA);
            signature.initSign(rsaPrivateKey);
            signature.update(signStr.getBytes());

            byte[] signed = signature.sign();
            String output = new BASE64Encoder().encode(signed);
            return output;
        } catch (Exception e) {
            throw new SignatureException("RSAcontent = " + signStr);
        }
    }

    /**
     * 从文件中加载私钥
     *
     * @return 是否成功
     * @throws Exception
     */
    public void loadPrivateKey(InputStream in) throws Exception {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String readLine = null;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                if (readLine.charAt(0) == '-') {
                    continue;
                } else {
                    sb.append(readLine);
                    sb.append('\r');
                }
            }
            loadPrivateKey(sb.toString());
        } catch (IOException e) {
            throw new Exception("私钥数据读取错误");
        } catch (NullPointerException e) {
            throw new Exception("私钥输入流为空");
        }
    }

    /**SHA1withRSA 验签
     * 验签
     */
    public static boolean rsaVerify(String publicKey, String sign, String signStr) {
        try {
            RSAPublicKey rsaPublicKey = loadPublicKey(publicKey);
            if (publicKey == null) {
                throw new Exception("解密私钥为空, 请设置");
            }
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM_SHA);
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
     * 验签
     */
    public static boolean rsaVerifyByMD5withRSA(String publicKey, String sign, String signStr) {
        try {
            RSAPublicKey rsaPublicKey = loadPublicKey(publicKey);
            if (publicKey == null) {
                throw new Exception("解密私钥为空, 请设置");
            }
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM_MD5);
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

    public static RSAPrivateKey loadPrivateKey(String privateKeyStr) throws Exception {
        try {
            BASE64Decoder base64Decoder = new BASE64Decoder();
            byte[] buffer = base64Decoder.decodeBuffer(privateKeyStr);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("私钥非法");
        } catch (IOException e) {
            throw new Exception("私钥数据内容读取错误");
        } catch (NullPointerException e) {
            throw new Exception("私钥数据为空");
        }
    }


    /**
     * 验签
     * @param data
     * @return
     * @throws Exception
     */
    public static boolean verify(Map data,String publicKey,String removeKey) throws Exception {

        Map<String,String> map = new HashMap<>();
        map.putAll(data);
       // Map<String,String> map = JSONObject.toJavaObject(data, Map.class);
        String sign = map.remove(removeKey).toString();
        String signData =  new TreeMap<>(map).entrySet().stream().filter(e -> !StringUtils.isEmpty(e.getValue())).map(e -> e.getKey().concat("=").concat(e.getValue())).collect(Collectors.joining("&"));
        logger.info("签名串:"+signData);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM_SHA);
        signature.initVerify(parsePublicKey(publicKey));
        signature.update(signData.getBytes("UTF-8"));
        return signature.verify(org.apache.commons.codec.binary.Base64.decodeBase64(sign.getBytes("UTF-8")));
    }

    /**
     * RSA公钥转换
     * @param publicKeyStr 公钥字符串
     * @return 公钥
     * @throws
     */
    public static PublicKey parsePublicKey(String publicKeyStr) throws InvalidKeySpecException {
        PublicKey publicKey = null;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.getDecoder().decode(publicKeyStr.getBytes(StandardCharsets.UTF_8));
            publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
        } catch (NoSuchAlgorithmException e) {
        }
        return publicKey;
    }

}
