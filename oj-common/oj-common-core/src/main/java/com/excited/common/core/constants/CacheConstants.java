package com.excited.common.core.constants;

/**
 * 缓存相关的常量
 */
public class CacheConstants {

    // 存储用户敏感信息时所需要使用的前缀
    public static final String LOGIN_TOKEN_KEY_PREFIX = "loginToken:";

    // 存储验证码时所需要使用的前缀
    public static final String PHONE_CODE_KEY_PREFIX = "p:c:";

    // 存储每天获取验证码次数所需要使用的前缀
    public static final String CODE_TIMES_KEY_PREFIX = "c:t:";

    // 存储用户敏感信息时所需要设置的过期时间: 720min
    public static final long EXP = 720L;

    // 当过期时间小于 180min 时进行延长
    public static final long REFRESH_TIME = 180L;

    public static final String EXAM_TIME_LIST_KEY_PREFIX = "e:t:l";

    public static final String EXAM_HISTORY_LIST_KEY_PREFIX = "e:h:l";

    public static final String EXAM_DETAIL_KEY_PREFIX = "e:d:";
}
