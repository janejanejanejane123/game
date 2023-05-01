package com.ruoyi.common.serializer;


import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.ruoyi.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * @description: RedisTemplate序列化工具
 **/
@Slf4j
public class ProtostuffSerializer<T> implements RedisSerializer<Object> {
    private final Schema<ProtoWrapper> schema;
    public ProtostuffSerializer() {
        this.schema = RuntimeSchema.getSchema(ProtoWrapper.class);
    }
    private boolean isEmpty(byte[] data) {
        return (data == null || data.length == 0);
    }

    @Override
    public byte[] serialize(Object obj) throws SerializationException {
        if (obj == null) {
            return new byte[0];
        }
        ProtoWrapper wrapper = new ProtoWrapper();
        wrapper.setData(obj);
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);//1024 * 1024
        byte[] protostuff = null;
        try {
            protostuff = ProtostuffIOUtil.toByteArray(wrapper, schema, buffer);
        } catch (Exception e) {
            log.error("Serialized objects erro:{}",e);
            throw new ServiceException("Serialized objects:" + obj.getClass() + "erro!");
        } finally {
            buffer.clear();
        }
        return protostuff;
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (isEmpty(bytes)) {
            return null;
        }
       ProtoWrapper message  = null;
        try {
            message =  new ProtoWrapper();
            ProtostuffIOUtil.mergeFrom(bytes, message, schema);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return message.getData();
    }


    private static class ProtoWrapper {
        private Object data;

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }
}