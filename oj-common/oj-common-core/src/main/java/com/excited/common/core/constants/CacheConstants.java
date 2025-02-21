package com.excited.common.core.constants;

/**
 * 缓存相关的常量
 */
public class CacheConstants {

    // 存储用户敏感信息时所需要使用的前缀
    public static final String LOGIN_TOKEN_KEY_PREFIX = "loginToken:";

    // 存储用户敏感信息时所需要设置的过期时间: 720min
    public static final long EXP = 720L;

    // 当过期时间小于 180min 时进行延长
    public static final long REFRESH_TIME = 180L;
}
