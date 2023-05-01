package com.ruoyi.common.bussiness;

import com.ruoyi.common.core.redis.RedisLock;

import java.util.Map;
import java.util.Stack;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/3/19,13:35
 * @return:
 **/
public class ThreadLocalService  {

    public static final ThreadLocal<String> USER_TYPE =new ThreadLocal<>();

    public static final ThreadLocal<Stack<RedisLock>> REDIS_LOCK=new ThreadLocal<>();

    public static final ThreadLocal<Map<String,Object>> LOGIN_PARAMS=new ThreadLocal<>();


}
