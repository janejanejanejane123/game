package com.ruoyi.common.serializer;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * @Description: redis byte序列化
 * @return
 */
public class ByteRedisSerializer implements RedisSerializer<byte[]> {

    public ByteRedisSerializer() {
    }
 
    @Override
    public byte[] serialize(byte[] t) throws SerializationException {
        return t;
    }
 
    @Override
    public byte[] deserialize(byte[] bytes) throws SerializationException {
        return bytes;
    }
 
}