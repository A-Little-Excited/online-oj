package com.excited.common.redis.config;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 将自定义序列化器配置到 Redis 中
 */
@Configuration
public class RedisConfig extends CachingConfigurerSupport {

    // 初始化 RedisTemplate 对象, 后续将使用该对象操作 Redis
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        // 以下是对 template 中的属性进行设置
        // 1. 与 Redis 的连接
        template.setConnectionFactory(connectionFactory);
        // 2. 对 key 和 value 所使用的序列化器分别进行设置
        // 由于 Redis 中的 key 都是 String 类型, 因此对于 key 和 hashKey 都使用默认提供的 String 类的序列化器即可
        // 由于 value 可能会存放 Java 中的对象, 因此对于 value 即使用自定义的序列化器
        JsonRedisSerializer<Object> serializer = new JsonRedisSerializer<>(Object.class);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}
