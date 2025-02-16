package com.excited.common.core.constants;

/**
 * 缓存相关的常量
 */
public class CacheConstants {

    // 存储用户敏感信息时所需要使用的前缀
    public static final String LOGIN_TOKEN_KEY_PREFIX = "loginToken: ";

    // 存储用户敏感信息时所需要设置的过期时间: 720 min
    public static final long EXP = 720L;
}
