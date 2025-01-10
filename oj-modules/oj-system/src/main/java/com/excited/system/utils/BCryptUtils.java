package com.excited.system.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 加密算法工具类
 */
public class BCryptUtils {

    /**
     * 对密码进行加密
     * @param password 原始密码
     * @return 加密密码
     */
    public static String encryptPassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    /**
     * 对原始密码和加密密码校验是否匹配
     * @param rawPassword 原始密码
     * @param encodePassword 加密密码
     * @return 校验结果
     */
    public static boolean matchesPassword(String rawPassword, String encodePassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(rawPassword, encodePassword);
    }
}
