package com.ruoyi.common.core.redis;

import com.ruoyi.common.bussiness.ThreadLocalService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.Assert;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.common.utils.SpringContextUtil;
import com.ruoyi.common.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/3/23,12:43
 * @return:
 **/
@Slf4j
public class RedisLock {
    /**
     * 默认请求锁的超时时间(ms 毫秒)
     */
    private static final long TIME_OUT = 100;


    /**
     * 默认锁的有效时间(s)
     */
    public static final int EXPIRE = 60;


    private String lockKey;
    private RedisTemplate redisTemplate;
    private Long mills= (long) EXPIRE;
    private String value;
    private Boolean locked=false;
    private long timeOut = TIME_OUT;
    final Random random = new Random();

    private RedisLock(String key,String value, RedisTemplate redisTemplate,long mills){
        this.lockKey=key+"_lock";
        this.redisTemplate=redisTemplate;
        this.mills=mills;
        this.value=value;

    }

    public static RedisLock redisLock(String key,long seconds){
        RedisTemplate bean = SpringContextUtil.getBean("redisTemplate");
        return new RedisLock(key, UUID.randomUUID().toString(), bean, seconds);
    }


    public static <T> T lockInOneSecondOperate(String key, Supplier<T> supplier){
        try {
            if (RedisLock.lock(key,1)){
                return supplier.get();
            }else {
                throw new ServiceException(MessageUtils.message("no.operate.frequently"));
            }
        }finally {
            RedisLock.unLock();
        }
    }

    public static void limited(String key,int limit,long seconds){
        RedisCache bean = SpringContextUtil.getBean(RedisCache.class);
        Assert.test(bean==null,"system.error");
        long incr = bean.incr(key, 1);
        bean.expire(key,seconds,TimeUnit.SECONDS);
        Assert.test(incr>=limit,"chat.frequently.error", seconds);
    }

    /**
     * 基于ThreadLocal 不支持异步解锁;
     * @param key 执行key
     * @param seconds 锁的秒数;
     * @return 是否成功;
     */
    @SuppressWarnings({"unchecked"})
    public static Boolean lock(String key,long seconds){

        if (StringUtils.isEmpty(key)){
            throw new ServiceException("redis key must be noEmpty CharSequence");
        }
        String lockValue = UUID.randomUUID().toString();

        RedisTemplate bean = SpringContextUtil.getBean("redisTemplate");

        RedisLock redisLock = new RedisLock(key,lockValue, bean,seconds);

        RedisLockContext.setRedisLock(redisLock);

        Boolean aBoolean= redisLock.locked =
                redisLock.redisTemplate.opsForValue().setIfAbsent(redisLock.lockKey, lockValue, seconds, TimeUnit.SECONDS);

        if (aBoolean==null||!aBoolean){
            return false;
        }
        return aBoolean;
    }

    public static boolean lock(String key){
        return lock(key,1);
    }

    @SuppressWarnings("unchecked")
    public static Boolean unLock(){
        RedisLock redisLock = RedisLockContext.popRedisLock();
        if (redisLock==null){
            return false;
        }
        Long execute=0L;
        if (redisLock.locked){
            //获取到KEY
            String lockKey = redisLock.lockKey;
            String value = redisLock.value;
            execute = (Long)redisLock.redisTemplate.execute(RedisScriptStrings.REDIS_UNLOCK_LUA,
                    Collections.singletonList(lockKey), value);
            if (execute==null){
                return false;
            }else {
                redisLock.locked = (execute ==0);
            }
            ;
        }
        return execute==1;

    }

    public static boolean tryLock(String key,long seconds){
        String lockValue = UUID.randomUUID().toString();

        RedisTemplate bean = SpringContextUtil.getBean("redisTemplate");

        Assert.test(bean==null,"system.error");
        RedisLock redisLock = new RedisLock(key,lockValue, bean,seconds);

        RedisLockContext.setRedisLock(redisLock);

        return redisLock.tryLock();
    }

    public static void delayLock() {
        RedisLock redisLock = RedisLockContext.peekRedisLock();
        if (redisLock!=null){
            redisLock.delayLock(EXPIRE);
        }
    }

    public void delayLock(final long seconds){
        redisTemplate.expire(lockKey, seconds, TimeUnit.SECONDS);
    }



    public boolean tryLock() {
        return tryLock(10,50000);
    }

    public boolean tryLock(long millis, int nanos) {
        // 请求锁超时时间，纳秒
        long timeout = timeOut * 1000000;
        // 系统当前时间，纳秒
        long nowTime = System.nanoTime();
        while ((System.nanoTime() - nowTime) < timeout) {
            if (this.set(lockKey, value, mills)) {
                locked = true;
                // 上锁成功结束请求
                return locked;
            }

            // 每次请求等待一段时间
            sleep(millis, nanos);
        }
        return locked;
    }

    @SuppressWarnings("unchecked")
    private Boolean set(String lockKey, String value, Long mills) {
        return redisTemplate.opsForValue().setIfAbsent(lockKey, value, mills, TimeUnit.SECONDS);
    }

    /**
     * @param millis 毫秒
     * @param nanos  纳秒
     * @Title: seleep
     * @Description: 线程等待时间
     * @author yuhao.wang
     */
    private void sleep(long millis, int nanos) {
        try {
            Thread.sleep(millis, random.nextInt(nanos));
        } catch (InterruptedException e) {
            log.info("获取分布式锁休眠被中断：", e);
        }
    }
}
