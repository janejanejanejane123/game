package com.ruoyi.common.utils;

import com.ruoyi.common.utils.sign.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/4/6,19:53
 * @return:
 **/
public class AESUtils {

    public static byte[] decrypt(String src, byte[] key,String iv) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, "AES");

        IvParameterSpec ivParameterSpec = getIv(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
//        byte[] hexBytes = hexStringToBytes(src);
        //网络传输过来的是一个Base64字串;
        byte[] bytes = Base64.decode(src);
        return cipher.doFinal(bytes);
    }


    private static IvParameterSpec getIv(String iv) {
        if (iv.length()!=16){
            throw new RuntimeException("iv length not 16");
        }
        return new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
    }
}
