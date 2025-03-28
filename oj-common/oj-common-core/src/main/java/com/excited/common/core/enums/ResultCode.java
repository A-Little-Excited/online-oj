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
    FAILED_LOGIN (3103, "账号或密码错误"),
    FAILED_USER_BANNED (3104, "您已被列入黑名单, 请联系管理员"),
    FAILED_USER_PHONE (3105, "您输入的手机号有误"),
    FAILED_FREQUENT (3106, "操作过于频繁, 请稍后重试"),
    FAILED_TIMES_LIMIT (3107, "当天请求次数已达到上限"),
    FAILED_SEND_CODE (3108, "验证码发送失败"),
    FAILED_INVALID_CODE (3109, "验证码已失效"),
    FAILED_ERROR_CODE (3108, "验证码错误"),

    EXAM_START_TIME_BEFORE_CURRENT_TIME (3201, "竞赛开始时间不能早于当前时间"),
    EXAM_START_TIME_AFTER_END_TIME (3202, "竞赛开始时间不能晚于结束时间"),
    EXAM_QUESTION_NOT_EXISTS (3204, "竞赛中包含不存在的题目"),
    EXAM_STARTED (3205, "竞赛已开赛, 无法操作");

    private int code;

    private String msg;
}
