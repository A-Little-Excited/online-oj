package com.excited.common.redis.service;

import com.alibaba.fastjson2.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class RedisService {
    @Autowired
    public RedisTemplate redisTemplate;

    // ************************ 操作key ***************************

    /**
     * 判断 key 是否存在
     *
     * @param key 键
     * @return true-存在; false-不存在
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 设置 key 的有效时间, 默认单位为 s
     *
     * @param key 键
     * @param timeout 超时时间
     * @return true-设置成功; false-设置失败
     */
    public boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置 key 的有效时间, 并可以指定单位
     *
     * @param key 键
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return true-设置成功; false-设置失败
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 获取 key 的剩余有效时间
     * @param key 键
     * @param unit 时间单位
     * @return 剩余的有效时间
     */
    public Long getExpire(final String key, final TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
    }

    /**
     * 删除单个对象
     *
     * @param key 键
     * @return 是否删除成功
     */
    public boolean deleteObject(final String key) {
        return redisTemplate.delete(key);
    }

    // ************************ 操作 String 类型 ***************************

    /**
     * 缓存基本的对象: Integer、String、实体类等
     *
     * @param key 缓存的键
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 缓存基本的对象并设置有效时间, 单位可以指定: Integer、String、实体类等
     *
     * @param key 缓存的键
     * @param value 缓存的值
     * @param timeout 有效时间
     * @param timeUnit 时间单位
     */
    public <T> void setCacheObject(final String key, final T value, final Long timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 获取缓存的基本对象。
     *
     * @param key 缓存的键
     * @return 缓存的键对应的数据
     */
    public <T> T getCacheObject(final String key, Class<T> clazz) {
        // 从 Redis 中根据 key 取出 value
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        T t = operation.get(key);
        if (t instanceof String) {
            return t;
        }

        // 如果 value 不是 String 类型则需要反序列为对象
        return JSON.parseObject(String.valueOf(t), clazz);
    }

    /**
     * 自增 +1
     *
     * @param key 缓存的键
     * @return 自增后的结果
     */
    public Long increment(final String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    // *************** 操作list结构 ****************

    /**
     * 获取 list 数据量
     *
     * @param key 缓存的键
     * @return list 数据量
     */
    public Long getListSize(final String key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 获取 list 中指定范围的数据
     *
     * @param key 缓存的键
     * @param start 区间开头
     * @param end 区间末尾
     * @param clazz 对象类型
     * @param <T> 泛型参数
     * @return list 中指定范围的数据
     */
    public <T> List<T> getCacheListByRange(final String key, long start, long end, Class<T> clazz) {
        List range = redisTemplate.opsForList().range(key, start, end);
        if (CollectionUtils.isEmpty(range)) {
            return null;
        }
        return JSON.parseArray(JSON.toJSONString(range), clazz);
    }

    /**
     * 使⽤ list 存储数据(尾插, 批量插⼊)
     */
    public <T> Long rightPushAll(final String key, Collection<T> list) {
        return redisTemplate.opsForList().rightPushAll(key, list);
    }

    /**
     * 使⽤ list 存储数据(头插)
     */
    public <T> Long leftPushForList(final String key, T value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 删除 list 中指定的数据
     */
    public <T> Long removeForList(final String key, T value) {
        return redisTemplate.opsForList().remove(key, 1L, value);
    }

    // ************************ 操作Hash类型 ***************************

    /**
     * 获取单个 Hash 中的数据
     *
     * @param key 键
     * @param hKey Hash键
     * @param clazz 对象类型
     * @return Hash键对应的对象
     * @param <T> 泛型参数
     */
    public <T> T getCacheMapValue(final String key, final String hKey, Class<T> clazz) {
        Object cacheMapValue = redisTemplate.opsForHash().get(key, hKey);
        if (cacheMapValue != null) {
            return JSON.parseObject(String.valueOf(cacheMapValue), clazz);
        }
        return null;
    }

    /**
     * 获取多个 Hash 中的数据
     *
     * @param key 键
     * @param hKeys Hash键
     * @param clazz 对象类型
     * @param <T> 泛型参数
     * @return 多个 Hash键 对应的对象集合
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<String> hKeys, Class<T> clazz) {
        List list = redisTemplate.opsForHash().multiGet(key, hKeys);
        List<T> result = new ArrayList<>();
        for (Object item : list) {
            result.add(JSON.parseObject(JSON.toJSONString(item), clazz));
        }
        return result;
    }

    /**
     * 使用 Hash 存储单个数据
     *
     * @param key 键
     * @param hKey Hash键
     * @param value Hash键对应的值
     */
    public <T> void setCacheMapValue(final String key, final String hKey, final T value) {
        redisTemplate.opsForHash().put(key, hKey, value);
    }

    /**
     * 使用 Hash 存储整个 Map
     *
     * @param key 键
     * @param dataMap 数据Map
     */
    public <K, T> void setCacheMap(final String key, final Map<K, T> dataMap) {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    /**
     * 删除 Hash 中的单个数据
     * @param key 键
     * @param hKey Hash键
     * @return 删除成功的数据个数
     */
    public Long deleteCacheMapValue(final String key, final String hKey) {
        return redisTemplate.opsForHash().delete(key, hKey);
    }
}

