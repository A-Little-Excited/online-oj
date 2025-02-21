package com.excited.common.core.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * 存储在 Redis 中的用户敏感信息
 */
@Getter
@Setter
public class LoginUser {

    private Integer identity; // 1-普通用户, 2-管理员
}
