package com.excited.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResultCode {
    /**
     * 操作成功
     */
    SUCCESS (1000, "操作成功"),

    /**
     * 服务器内部出错
     */
    ERROR (2000, "服务繁忙, 请稍后重试"),

    /**
     * 业务逻辑出错
     */
    FAILED (3000, "操作失败"),
    FAILED_UNAUTHORIZED (3001, "未授权"),
    FAILED_PARAMS_VALIDATE (3002, "参数校验失败"),
    FAILED_NOT_EXISTS (3003, "资源不存在"),
    FAILED_ALREADY_EXISTS (3004, "资源已存在"),

    FAILED_USER_EXISTS (3101, "用户已存在"),
    FAILED_USER_NOT_EXISTS (3102, "用户不存在"),
    FAILED_LOGIN (3103, "用户名或密码错误"),
    FAILED_USER_BANNED (3104, "您已被列入黑名单, 请联系管理员");

    private int code;

    private String msg;
}
