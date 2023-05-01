package com.ruoyi.common.cache;


import com.ruoyi.common.annotation.Overtime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.io.*;
import java.util.concurrent.Callable;


public class RedisCache4Spring implements Cache {
    private static final String NIL_REDIS_VALUE = "nil:redis:value";
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private RedisTemplate<String, Object> redisTemplate;
    private String name;

    private long defaultexpiretime = 3600L;

    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Object getNativeCache() {
        return this.redisTemplate;
    }

    @Override
    public ValueWrapper get(Object key) {
        String keyStr = null;
        if (key instanceof String) {
            keyStr = (String) key;
        } else {
            keyStr = key.toString();
        }
        final String keyf = keyStr;
        Object object = null;
        object = redisTemplate.execute((RedisCallback<Object>) connection -> {
            byte[] key1 = keyf.getBytes();
            byte[] value = connection.get(key1);
            if (value == null) {
                return null;
            }
            return serializerValue(value);
        });
        if (object instanceof String) {
            if (NIL_REDIS_VALUE.equals(object)) {
                return new SimpleValueWrapper(null);
            }
        }
        return (object != null ? new SimpleValueWrapper(object) : null);
    }

    @Override
    public void put(Object key, Object value) {
        String keyStr = null;
        if (key instanceof String) {
            keyStr = (String) key;
        } else {
            keyStr = key.toString();
        }
        if (value == null) {
            value = NIL_REDIS_VALUE;
        }
        final String keyf = keyStr;
        final Object valuef = value;
        redisTemplate.execute((RedisCallback<Long>) connection -> {
            byte[] keyb = keyf.getBytes();
            byte[] valueb = null;
            RedisSerializer redisSerializer = redisTemplate.getValueSerializer();
            if (redisSerializer != null) {
                valueb = redisSerializer.serialize(valuef);
            } else {
                valueb = toByteArray(valuef);
            }
            Overtime overtime = OvertimeContext.getCurrentOvertime();
            connection.set(keyb, valueb);
            if (overtime != null) {
                connection.expire(keyb, overtime.value());
            } else {
                connection.expire(keyb, defaultexpiretime);
            }
            return 1L;
        });
        logger.debug("put缓存中获取：key:" + keyStr + ",object:" + value);
    }

    /**
     * 描述 : <Object转byte[]>. <br> <p> <使用方法说明> </p>
     *
     * @param obj
     * @return
     */
    private byte[] toByteArray(Object obj) {
        byte[] bytes = null;
        ObjectOutputStream oos = null;
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();

        } catch (Exception ex) {
            logger.error("toByteArray eror", ex);
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    logger.error("oos close toByteArray eror", e);
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    logger.error("bos close toByteArray eror", e);
                }
            }
        }
        return bytes;
    }

    /**
     * 描述 : <byte[]转Object>. <br> <p> <使用方法说明> </p>
     *
     * @param bytes
     * @return
     */
    private Object toObject(byte[] bytes) {
        Object obj = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            bis = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bis);
            obj = ois.readObject();
        } catch (Exception ex) {
            logger.error("toObject eror", ex);

        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    logger.error("ois.close() eror", e);
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    logger.error("bis.close() eror", e);
                }
            }

        }

        return obj;
    }

    @Override
    public void evict(Object key) {
        final String keyf = (String) key;
        redisTemplate.execute((RedisCallback<Long>) connection ->
                connection.del(keyf.getBytes()));
    }

    @Override
    public void clear() {
        redisTemplate.execute((RedisCallback<String>) connection -> {
            connection.flushDb();
            return "ok";
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Object key, Class<T> type) {
        final String keyf = (String) key;
        Object object = null;
        object = redisTemplate.execute((RedisCallback<Object>) connection -> {
            byte[] key1 = keyf.getBytes();
            byte[] value = connection.get(key1);
            if (value == null) {
                return null;
            }
            Object returnValue = serializerValue(value);
            if (returnValue instanceof String) {
                if (NIL_REDIS_VALUE.equals(returnValue)) {
                    returnValue = null;
                }
            }
            return returnValue;
        });
        return (T) object;

    }

    private Object serializerValue(byte[] value) {
        RedisSerializer redisSerializer = redisTemplate.getValueSerializer();
        Object returnValue = null;
        if (redisSerializer != null) {
            returnValue = redisSerializer.deserialize(value);
        } else {
            returnValue = toObject(value);
        }
        return returnValue;
    }

    /**
     * * 从缓存中获取 key 对应的值，如果缓存没有命中，则添加缓存， 此时可异步地从 valueLoader 中获取对应的值（4.3版本新增
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Object key, Callable<T> valueLoader) {

        ValueWrapper val = this.get(key);
        if (val != null) {
            return (T) val.get();
        } else {
            synchronized (key) {
                val = this.get(key);
                if (val != null) {
                    return (T) val.get();
                }
                T value;
                try {
                    value = valueLoader.call();
                } catch (Exception ex) {
                    throw new ValueRetrievalException(key, valueLoader, ex);
                }
                put(key, value);
                return value;
            }
        }
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        //TODO 后期使用分布式锁实现原子操作
        put(key, value);
        return new SimpleValueWrapper(value);

    }

    public long getDefaultexpiretime() {
        return defaultexpiretime;
    }

    public void setDefaultexpiretime(long defaultexpiretime) {
        this.defaultexpiretime = defaultexpiretime;
    }

}