package com.ruoyi.common.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.net.util.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 加解密工具
 */
public class Encrypt {


    public static byte[] encryptECB(byte[] data, byte[] key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
        byte[] result = cipher.doFinal(data);
        return result;
    }




    /**
     * MD5 32位加密
     *
     * @param sourceStr
     * @return
     */
    public static String MD5(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes("UTF-8"));
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * AES加密
     *
     * @param
     * @return
     * @throws Exception
     */
    public static String AESEncrypt(String value, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        byte[] raw = key.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(value.getBytes("UTF-8"));
        String base64 = new BASE64Encoder().encode(encrypted);// 此处使用BASE64做转码
        return URLEncoder.encode(base64, "UTF-8");//URL加密
    }




    /**
     *幸运棋牌
     *  AES加密
     *
     * @param
     * @return
     * @throws Exception
     */
    public static String XYQPEncrypt(String value, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        byte[] raw = key.getBytes("UTF-8");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(value.getBytes("UTF-8"));
        String base64 = new BASE64Encoder().encode(encrypted);// 此处使用BASE64做转码
        return base64;//URL加密
//        return URLEncoder.encode(base64, "UTF-8");//URL加密
    }


    /**
     * AES加密 不进行URLEncoder
     *
     * @param
     * @return
     * @throws Exception
     */
    public static String AESUNURLEncrypt(String value, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        byte[] raw = key.getBytes("UTF-8");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(value.getBytes("UTF-8"));
        return new BASE64Encoder().encode(encrypted);// 此处使用BASE64做转码
    }

    /**
     * AES 解密
     *
     * @param
     * @return
     * @throws Exception
     */
    public static String AESDecrypt(String value, String key, boolean isDecodeURL) throws Exception {
        try {
            byte[] raw = key.getBytes("UTF-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            if (isDecodeURL) {
                value = URLDecoder.decode(value, "UTF-8");
            }
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(value);// 先用base64解密
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, "UTF-8");
            return originalString;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * JDB AES 加密
     *
     * @param data
     * @param key
     * @param iv
     * @return
     * @throws Exception
     */
    public static String encrypt(String data, String key, String iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        int blockSize = cipher.getBlockSize();
        byte[] dataBytes = data.getBytes("UTF-8");
        int plainTextLength = dataBytes.length;
        if (plainTextLength % blockSize != 0) {
            plainTextLength = plainTextLength + (blockSize - plainTextLength % blockSize);
        }
        byte[] plaintext = new byte[plainTextLength];
        System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
        SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
        byte[] encrypted = cipher.doFinal(plaintext);
        return Base64.encodeBase64URLSafeString(encrypted);
    }

    /**
     * 加密并发送
     *
     * @throws Exception
     */
    public static String encryptByPost(Map map, Map params, boolean zipResponse) throws Exception {
        HttpResponse response = null;
        String result = null;
        String x;
        String aes_key = (String) params.get("key");
        String aes_iv = (String) params.get("IV");
        String api_url = (String) params.get("api_url");
        String dc = (String) params.get("DC");
// get a client
        HttpClient httpClient = new DefaultHttpClient();
        String vString = JSONObject.toJSONString(map);
// encrypt
        x = encrypt(vString, aes_key, aes_iv);
// build request
        List<NameValuePair> paramList = new ArrayList<>();
        paramList.add(new BasicNameValuePair("dc", dc));
        paramList.add(new BasicNameValuePair("x", x));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, "UTF-8");
        HttpPost httpPost = new HttpPost(api_url);
        httpPost.setEntity(entity);
        if (zipResponse) {
            httpPost.setHeader("Accept-Encoding", "gzip");
        }
        HttpEntity httpEntity = null;
        try {
// Resolve response
            response = httpClient.execute(httpPost);
            httpEntity = response.getEntity();
            result = EntityUtils.toString(httpEntity);
//            JsonElement element = Gson.fromJson(string, JsonElement.class);
//            result = element.getAsJsonObject();
        } finally {
            EntityUtils.consume(httpEntity);
        }
        return result;
    }


    /**
     * 加密并发送
     *
     * @throws Exception
     */


    public static void main(String[] a) {
        //http://14.29.47.112:89/channelHandle?agent=10066&timestamp=1497024103000&param=dTYWCf9V7KZWweVuRJ%2Fjr2T6TH4Ckzx3vwkSJJ%2FcNPE%3D&key=7d41837eaaf324d408e8ee2029f0d49e
        String value = "vWappyp5yL1zQH9G6NUlo%2FXZrsfPX0aWm0CAz75eK1hmJ4rgb6wApu9JmgIwgDN3QVxNz1otN2DndepbzZYrXA%3D%3D";
        try {
            System.out.println(AESDecrypt(value, "8092955993034d57", true));
//			System.out.println(AESEncrypt("s=0&account=KYTest_001&money=0&orderid=1003120170713094921095&ip=127.0.0.1&lineCode=10031", "c42e60b1d28a45f2"));
//			System.out.println(MD5("10031149991023934690ad30d5286d451a"));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
