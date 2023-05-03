package com.ruoyi.member.util;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.core.redis.RedisLock;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.function.Supplier;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/6/26,12:57
 * @return:
 **/
@Component
public class ShortUrlUtil {

    @Resource
    private RedisCache redisCache;

    private String keyPre ="shortUrl:";

    private String lockKey ="Lock:shortUrl:";


    public Long getShortUrl(String key, int delta, Supplier<Long> dataIfAbsent){
        Object cacheObject = redisCache.getCacheObject(keyPre + key);
        if (null==cacheObject){
            try {
                if (RedisLock.tryLock(lockKey+key,60*1000)){
                    cacheObject = redisCache.getCacheObject(keyPre + key);
                    if (null==cacheObject){
                        cacheObject = dataIfAbsent.get();
                        cacheObject = redisCache.incr(keyPre + key, (Long) cacheObject + delta);
                    }else {
                        cacheObject=redisCache.incr(keyPre+key,delta);
                    }
                };
            }finally {
                RedisLock.unLock();
            }
        }else {
            cacheObject=redisCache.incr(keyPre+key,delta);
        }

        return (Long)cacheObject;
    }

    /**
     * 此方法不能保证唯一，一千万次可能重复50多次，所以在保存前需要判断
     * @return
     */
    public String getShortUrl(int length){
        StringBuilder result = new StringBuilder(12);
        for(int index = 0 ;index < length;index++){
//            result.append(NumberUtils.decimal2base62( ((int)(63 * Math.random()))));
        }
        return result.toString();
    }
}
