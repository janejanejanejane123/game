package com.ruoyi.common.core.redis;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.Assert;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.common.utils.StringUtils;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * spring redis 工具类
 *
 * @author ruoyi
 **/
@SuppressWarnings(value = { "unchecked", "rawtypes" })
@Component
public class RedisCache
{
    @Autowired
    public RedisTemplate redisTemplate;

    /**
     * 登录后踢人的脚本
     * @param redisKickKey  uid 和 当前登录的token 的 mapping 的key;
     * @param loginTokenKey token 和loginUser mapping 的key;
     * @param token 登录之后需要更新的mapping的token;
     * @return 上一个登录的token;
     */
    public String kickLastAfterLogin(String redisKickKey, String loginTokenKey, String token) {
        return (String)redisTemplate.execute(RedisScriptStrings.KICK_LAST_AFTER_LOGIN,
                Collections.singletonList(redisKickKey),loginTokenKey,token);

    }

    public <T> T execute(RedisScript script,List<String> keys,Object...args){
        return (T)redisTemplate.execute(script,keys,args);
    }


    /**
     * 对出登录之后调用的
     * @param redisKickKey
     * @param token
     */
    public void logoutList(String redisKickKey,String token){
        this.delListElement(redisKickKey,token);
    }

    public void logout(String loginKey,String kickKey,String token){
        redisTemplate.delete(loginKey);
        this.logoutList(kickKey,token);
//        List<String> keys = new ArrayList<>(2);
//        keys.add(loginKey);
//        keys.add(kickKey);
//        redisTemplate.execute(RedisScriptStrings.LOGOUT_LUA_SCRIPT,
//                keys,token);
    }




    /**
     *
     */
    public void delListElement(String key,Object value){
        redisTemplate.opsForList().remove(key,0,value);
    }

    /***
     * 比较并交换;
     * @param key
     * @param arg1
     * @param arg2
     * @return
     */
    @SuppressWarnings("unchecked")
    public Boolean compareAndSwap(String key,String arg1,String arg2){
        if (arg1==null||arg2==null){
            throw new ServiceException("system error");
        }

        Object execute = redisTemplate.execute(RedisScriptStrings.COMPARE_AND_SWAP, Collections.singletonList(key), arg1,arg2);
        if (execute==null){
            return false;
        }
        return 1== (Long)execute;

    }

    public Long existAndIncr(String key){
        if (StringUtils.isBlank(key)){
            throw new ServiceException("system error");
        }

        return (Long) redisTemplate.execute(RedisScriptStrings.EXIST_AND_INCR,Collections.singletonList(key));
    };

    /**
     * 获取并删除;
     * @param key key;
     * @return
     */
    public String getAndDelete(String key){
        if (StringUtils.isBlank(key)){
            throw new ServiceException("system error");
        }
        return (String)redisTemplate.execute(RedisScriptStrings.GET_AND_DEL,Collections.singletonList(key));
    }



    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key 缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value)
    {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key 缓存的键值
     * @param value 缓存的值
     * @param timeout 时间
     * @param timeUnit 时间颗粒度
     */
    public <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit)
    {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 设置有效时间
     *
     * @param key Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout)
    {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key Redis键
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit)
    {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 判断 key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key){
        return redisTemplate.hasKey(key);
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(final String key)
    {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public boolean deleteObject(final String key)
    {
        return redisTemplate.delete(key);
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     * @return
     */
    public long deleteObject(final Collection collection)
    {
        return redisTemplate.delete(collection);
    }

    /**
     * 缓存List数据
     *
     * @param key 缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public <T> long setCacheList(final String key, final List<T> dataList)
    {
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getCacheList(final String key)
    {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 缓存Set
     *
     * @param key 缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> BoundSetOperations<String, T> setCacheSet(final String key, final Set<T> dataSet)
    {
        BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
        Iterator<T> it = dataSet.iterator();
        while (it.hasNext())
        {
            setOperation.add(it.next());
        }
        return setOperation;
    }

    /**
     * 获得缓存的set
     *
     * @param key
     * @return
     */
    public <T> Set<T> getCacheSet(final String key)
    {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param dataMap
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap)
    {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    /**
     * 获得缓存的Map
     *
     * @param key
     * @return
     */
    public <T> Map<String, T> getCacheMap(final String key)
    {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 往Hash中存入数据
     *
     * @param key Redis键
     * @param hKey Hash键
     * @param value 值
     */
    public <T> void setCacheMapValue(final String key, final String hKey, final T value)
    {
        redisTemplate.opsForHash().put(key, hKey, value);
    }

    /**
     * 获取Hash中的数据
     *
     * @param key Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public <T> T getCacheMapValue(final String key, final String hKey)
    {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, hKey);
    }

    /**
     * 删除Hash中的数据
     * 
     * @param key
     * @param hkey
     */
    public void delCacheMapValue(final String key, final String hkey)
    {
        HashOperations hashOperations = redisTemplate.opsForHash();
        hashOperations.delete(key, hkey);
    }

    /**
     * 获取多个Hash中的数据
     *
     * @param key Redis键
     * @param hKeys Hash键集合
     * @return Hash对象集合
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys)
    {
        return redisTemplate.opsForHash().multiGet(key, hKeys);
    }

    /**
     * 删除Hash中的某条数据
     *
     * @param key Redis键
     * @param hKey Hash键
     * @return 是否成功
     */
    public boolean deleteCacheMapValue(final String key, final String hKey)
    {
        return Boolean.TRUE.equals(redisTemplate.opsForHash().delete(key, hKey));
    }

    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public Collection<String> keys(final String pattern)
    {
        return redisTemplate.keys(pattern);
    }


    public long incr(String redisKey,long delta) {
        Long increment = redisTemplate.opsForValue().increment(redisKey, delta);
        return increment==null?0:increment;
    }

    public long decr(String redisKey,long delta){
        Long decrement = redisTemplate.opsForValue().decrement(redisKey, delta);
        return decrement==null?0:decrement;
    }

    public Boolean setIfAbsent(String key,Object value,long expire,TimeUnit timeUnit){
        return redisTemplate.opsForValue().setIfAbsent(key,value,expire,timeUnit);

    }
}
