package com.ruoyi.common.utils.hash;

import java.security.MessageDigest;
import java.util.Date;

public class CryptoUtil {

    private CryptoUtil() {
    }

    public static String getSHA256(String str) {
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (Exception e) {
            System.out.println("getSHA256 is error" + e.getMessage());
        }
        return encodeStr;
    }

    private static String byte2Hex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        String temp;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                builder.append("0");
            }
            builder.append(temp);
        }
        return builder.toString();
    }


    private static String calculateHash(int index, String previousHash, long timestamp, String data,long nonce) {
        StringBuilder builder = new StringBuilder(index);
        builder.append(previousHash).append(timestamp).append(data).append(nonce);
        return CryptoUtil.getSHA256(builder.toString());
    }


    public static void main(String[] args) {
        String s = CryptoUtil.calculateHash(1000, "242142342", new Date().getTime(), "123456", 1000000000);
        System.out.println(s);
    }
}
