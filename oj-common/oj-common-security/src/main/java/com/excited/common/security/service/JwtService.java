package com.excited.common.security.service;

import cn.hutool.core.lang.UUID;
import com.excited.common.core.constants.CacheConstants;
import com.excited.common.core.constants.JwtConstants;
import com.excited.common.core.enums.UserIdentity;
import com.excited.common.redis.service.RedisService;
import com.excited.common.core.domain.LoginUser;
import com.excited.common.core.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class JwtService {

    @Autowired
    private RedisService redisService;

    /**
     * 为登录成功的用户生成 token 并存储敏感信息
     * @param userId 用户 ID
     * @param secret 生成 token 所需要的密钥
     * @param identity 用户身份
     * @return 生产的 token
     */
    public String createToken(Long userId, String secret, Integer identity) {
        // 1. 登录成功之后, 生成 token 返回给客户端
        // token 的载荷部分存储 用户id 和 存储敏感信息所需要的 uuid
        String uuid = UUID.fastUUID().toString();
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtConstants.LOGIN_USER_ID, userId);
        claims.put(JwtConstants.LOGIN_USER_KEY, uuid);
        String token = JwtUtils.createToken(claims, secret);
        // 2. 往 Redis 中写入敏感数据
        // key: 前缀加上 UUID, 使用 uuid 作为存储用户敏感信息的唯一标识
        // value: 类型为 String 即可, 存储 LoginUser, 由于设置了自定义的序列化器, 因此会被自动进行序列化
        String redisKey = CacheConstants.LOGIN_TOKEN_KEY_PREFIX + uuid;
        LoginUser loginUser = new LoginUser();
        loginUser.setIdentity(UserIdentity.ADMIN.getValue());
        redisService.setCacheObject(redisKey, loginUser, CacheConstants.EXP, TimeUnit.MINUTES);

        return token;
    }

    /**
     * 延长 token 的有效时间
     * @param token token
     */
    public void extendToken(String token, String secret) {
        // 虽然延长 token 是在拦截器处被调用, 而如果请求能够到达拦截器, 说明已经通过网关处过滤器的过滤了
        // 保证了是一个有效的 token, 载荷部分肯定正确, 但是此处还是进行校验
        // 但是此处如果真的发生异常, 不能说明用户请求携带的 token 异常, 因为该 token 已经通过了过滤器
        // 因此直接进行返回, 不进行有效时间的延长即可, 不做程序中断处理
        Claims claims;
        try {
            claims = JwtUtils.parseToken(token, secret);
            if (claims == null) {
                log.error("延长 token 时解析 token 出现异常, token: {}", token);
                return;
            }
        } catch (Exception e) {
            log.error("延长 token 时解析 token 出现异常, token: {}", token, e);
            return;
        }

        String userKey = JwtUtils.getUserKey(claims);
        String redisKey = getRedisKey(userKey);

        // 查询剩余有效时间并判断是否需要进行延长
        Long expire = redisService.getExpire(redisKey, TimeUnit.MINUTES);
        if (expire != null && expire < CacheConstants.REFRESH_TIME) {
            redisService.expire(redisKey, CacheConstants.EXP, TimeUnit.MINUTES);
        }
    }

    /**
     * 获取缓存中敏感信息对应的 key
     * @param userKey 用户敏感信息的唯一标识
     * @return key
     */
    private String getRedisKey(String userKey) {
        // 前缀 + 用户敏感信息的唯一标识
        return CacheConstants.LOGIN_TOKEN_KEY_PREFIX + userKey;
    }
}
