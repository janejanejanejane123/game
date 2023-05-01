package com.ruoyi.common.core.redis;

import com.ruoyi.common.bussiness.ThreadLocalService;

import java.util.Stack;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/4/10,13:12
 * @return:
 **/
public class RedisLockContext {



    static void setRedisLock(RedisLock lock){
        Stack<RedisLock> redisLocks = ThreadLocalService.REDIS_LOCK.get();
        if (redisLocks==null){
            redisLocks=new Stack<>();
            ThreadLocalService.REDIS_LOCK.set(redisLocks);
        }
        redisLocks.push(lock);

    }

    /**
     * 获得线程当前的redisLock
     * @return redisLock
     */
    public static RedisLock peekRedisLock(){
        Stack<RedisLock> redisLocks = ThreadLocalService.REDIS_LOCK.get();
        if (redisLocks==null||redisLocks.size()==0){
            return null;
        }
        return redisLocks.peek();
    }


    /**
     * 弹出当前的redisLock
     * @return redisLock
     */
    static RedisLock popRedisLock(){
        Stack<RedisLock> redisLocks = ThreadLocalService.REDIS_LOCK.get();
        if (redisLocks==null||redisLocks.size()==0){
            return null;
        }
        RedisLock pop = redisLocks.pop();

        if (redisLocks.size()==0){
            ThreadLocalService.REDIS_LOCK.remove();
        }
        return pop;
    }

}
