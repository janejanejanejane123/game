package com.ruoyi.common.utils.number;

import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;

import java.util.UUID;

/**
 * @Description: 10进制、62进制互转
 * @return
 */
public class ConversionUtil {
    /**
     * 初始化 62 进制数据，索引位置代表字符的数值，比如 A代表10，z代表61等
     */
    private static String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static int scale = 62;

    /**
     * 将数字转为62进制
     *
     * @param num    Long 型数字
     * @return 62进制字符串
     */
    public static String encode(long num) {
        StringBuilder sb = new StringBuilder();
        int remainder = 0;

        while (num > scale - 1) {
            /**
             * 对 scale 进行求余，然后将余数追加至 sb 中，由于是从末位开始追加的，因此最后需要反转（reverse）字符串
             */
            remainder = Long.valueOf(num % scale).intValue();
            sb.append(chars.charAt(remainder));

            num = num / scale;
        }

        sb.append(chars.charAt(Long.valueOf(num).intValue()));
        return sb.reverse().toString();
    }

    /**
     * 62进制字符串转为数字
     *
     * @param str 编码后的62进制字符串
     * @return 解码后的 10 进制字符串
     */
    public static long decode(String str) {
        /**
         * 将 0 开头的字符串进行替换
         */
        str = str.replace("^0*", "");
        long num = 0;
        int index = 0;
        for (int i = 0; i < str.length(); i++) {
            /**
             * 查找字符的索引位置
             */
            index = chars.indexOf(str.charAt(i));
            /**
             * 索引位置代表字符的数值
             */
            num += (long) (index * (Math.pow(scale, str.length() - i - 1)));
        }

        return num;
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        SnowflakeIdUtils snowflakeIdUtils = new SnowflakeIdUtils();
        for (int i = 0; i < 10000; i++) {
//            String str62 = encode(snowflakeIdFactory.nextId());
//            System.out.println("62进制：" + str62);
            String str62 = UUID.randomUUID().toString().replaceAll("-","");
            long longValue = decode(str62);
            System.out.println("uuidValue"+str62);
            System.out.println("10进制：" + longValue);
//            System.out.println("encode:"+encode(longValue));
        }
    }
}