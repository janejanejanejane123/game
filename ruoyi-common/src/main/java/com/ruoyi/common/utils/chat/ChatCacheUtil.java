package com.ruoyi.common.utils.chat;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.SpringContextUtil;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/7/24,20:38
 * @return:
 **/
public class ChatCacheUtil {

    public static String getNodeServerByIdentifier(String identifier){
        RedisCache bean = SpringContextUtil.getBean(RedisCache.class);
        return bean.getCacheObject("nodeMapping:"+identifier);
    }

}
