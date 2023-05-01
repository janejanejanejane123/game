package com.ruoyi.common.utils;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.spring.SpringUtils;
import org.apache.poi.ss.formula.functions.T;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存工具类
 * 
 * @author nn
 */
public class CacheUtils {

    /**
     * 设置完整的cache key
     * @param constantsKey 键的前缀  Constants类
     * @param key 分类键
     * @return 缓存键key
     */
    public static String getCacheKey(String constantsKey, String key)
    {
        return constantsKey + key;
    }

    /**
     * 设置缓存.
     * @param constantsKey 键的前缀  Constants类
     * @param key 分类键
     * @param object 缓存数据
     * @param timeout 过期时间 颗粒度秒-SECONDS
     */
    public static void setCacheObject(String constantsKey,String key, Object object,Integer timeout)
    {
        SpringUtils.getBean(RedisCache.class).setCacheObject(getCacheKey(constantsKey,key), object,timeout, TimeUnit.SECONDS);
    }

    /**
     * 获取缓存.
     * @param constantsKey 键的前缀  Constants类
     * @param key 分类键
     * @return Object 数据
     */
    public static Object getCacheObject(String constantsKey,String key)
    {
        Object cacheObj = SpringUtils.getBean(RedisCache.class).getCacheObject(getCacheKey(constantsKey,key));
        if (StringUtils.isNotNull(cacheObj))
        {
            return StringUtils.cast(cacheObj);
        }
        return null;
    }

    /**
     * 删除指定缓存.
     * @param constantsKey 键的前缀  Constants类
     * @param key 分类键
     */
    public static void removeCacheObject(String constantsKey,String key)
    {
        SpringUtils.getBean(RedisCache.class).deleteObject(getCacheKey(constantsKey,key));
    }

    /**
     * 清空缓存.
     * @param constantsKey 键的前缀  Constants类
     */
    public static void clearCacheObject(String constantsKey)
    {
        Collection<String> keys = SpringUtils.getBean(RedisCache.class).keys(constantsKey + "*");
        SpringUtils.getBean(RedisCache.class).deleteObject(keys);
    }

}