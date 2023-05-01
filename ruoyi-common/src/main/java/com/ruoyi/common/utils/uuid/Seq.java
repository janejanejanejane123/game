package com.ruoyi.common.utils.uuid;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.RandomUtil;
import com.ruoyi.common.utils.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ruoyi 序列生成类
 */
public class Seq
{
    // 通用序列类型
    public static final String commSeqType = "COMMON";

    // 上传序列类型
    public static final String uploadSeqType = "UPLOAD";

    // 通用接口序列数
    private static AtomicInteger commSeq = new AtomicInteger(1);

    // 上传接口序列数
    private static AtomicInteger uploadSeq = new AtomicInteger(1);

    // 机器标识
    private static String machineCode = "A";

    /**
     * 获取通用序列号
     * 
     * @return 序列值
     */
    public static String getId()
    {
        return getId(commSeqType);
    }
    
    /**
     * 默认16位序列号 yyMMddHHmmss + 一位机器标识 + 3长度循环递增字符串
     * 
     * @return 序列值
     */
    public static String getId(String type)
    {
        AtomicInteger atomicInt = commSeq;
        if (uploadSeqType.equals(type))
        {
            atomicInt = uploadSeq;
        }
        return getId(atomicInt, 3);
    }

    /**
     * 通用接口序列号 yyMMddHHmmss + 一位机器标识 + length长度循环递增字符串
     * 
     * @param atomicInt 序列数
     * @param length 数值长度
     * @return 序列值
     */
    public static String getId(AtomicInteger atomicInt, int length)
    {
        String result = DateUtils.dateTimeNow();
        result += machineCode;
        result += getSeq(atomicInt, length);
        return result;
    }

    /**
     * 序列循环递增字符串[1, 10 的 (length)幂次方), 用0左补齐length位数
     * 
     * @return 序列值
     */
    private synchronized static String getSeq(AtomicInteger atomicInt, int length)
    {
        // 先取值再+1
        int value = atomicInt.getAndIncrement();

        // 如果更新后值>=10 的 (length)幂次方则重置为1
        int maxSeq = (int) Math.pow(10, length);
        if (atomicInt.get() >= maxSeq)
        {
            atomicInt.set(1);
        }
        // 转字符串，用0左补齐
        return StringUtils.padl(value, length);
    }


    /**
     * 功能描述:
     * 生成订单号
     * @return: java.lang.String
     **/
    public synchronized static String generatorOrderNo( String paymentName) {
        // 14位 当前时间 yyyyMMddHHmmss
        String currTime = DateUtils.dateTimeNow("yyyyMMddHHmmss");
        // 8位日期
        String strTime = currTime.substring(8, currTime.length());
        // 四位随机数
        String strRandom = RandomUtil.generateString(4);//四位随机数
        // 10位序列号,可以自行调整。
        String strReq = strTime + strRandom;
        // 订单号，此处用时间加随机数生成，商户根据自己情况调整，只要保持全局唯一就行
        String orderNo = paymentName.substring(0,2).toUpperCase(Locale.ROOT)  + currTime + strReq;
        return orderNo;
    }

    /**
     * 根据类型生成订单号
     * @param type
     * @return
     */
    public synchronized static String generateId(String type){
        LocalDateTime now = LocalDateTime.now();
        return "EPAY" + type + now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")) + UUID.getRandomStr(6);
    }
}
