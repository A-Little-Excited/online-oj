package com.excited.common.security.service;

import cn.hutool.core.lang.UUID;
import com.excited.common.core.constants.CacheConstants;
import com.excited.common.core.constants.JwtConstants;
import com.excited.common.core.enums.UserIdentity;
import com.excited.common.redis.service.RedisService;
import com.excited.common.security.domain.LoginUser;
import com.excited.common.security.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
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
}
