package com.excited.common.core.utils;

import com.excited.common.core.constants.JwtConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Map;

public class JwtUtils {

    /**
     * 生成 token
     * @param claims payload 数据
     * @param secret 加密密钥
     * @return token
     */
    public static String createToken(Map<String, Object> claims, String secret) {
        // 加密算法中, 安全性随之递增, 效率随之递减, 并且 HSXXX 的加密算法都是对称加密
        // 因此为了安全性、效率以及复杂性的折中考虑, 选择了 HS512
        String token = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
        return token;
    }

    /**
     * 解析 token, 获取 payload 数据
     * @param token Jwt token
     * @param secret 加密密钥
     * @return payload 数据
     */
    public static Claims parseToken(String token, String secret) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }

    public static String getUserId(Claims claims) {
        Object userId = claims.get(JwtConstants.LOGIN_USER_ID);

        return toStr(userId);
    }

    public static String getUserKey(Claims claims) {
        Object userKey = claims.get(JwtConstants.LOGIN_USER_KEY);

        return toStr(userKey);
    }

    private static String toStr(Object value) {
        if(value == null) {
            return "";
        }
        return value.toString();
    }
}
