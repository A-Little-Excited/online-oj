package com.excited.common.redis.config;

import com.alibaba.fastjson2.JSON;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;

/**
 * 自定义序列化器：通过使用 fastjson 对 Redis 中的数据进行序列化和反序列化
 * 往 Redis 中存储数据时需要先对于对象进行序列化
 * 从 Redis 中取出数据时需要先进行反序列化为对象
 */
public class JsonRedisSerializer<T> implements RedisSerializer<T> {

    // 序列化和反序列化时使用的编码
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private Class<T> clazz;

    public JsonRedisSerializer(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }

    // 序列化
    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null) {
            return new byte[0];
        }
        return JSON.toJSONString(t).getBytes(DEFAULT_CHARSET);
    }

    // 反序列化
    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        String str = new String(bytes, DEFAULT_CHARSET);
        return JSON.parseObject(str, clazz);
    }
}
