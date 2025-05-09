package com.excited.common.core.domain.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 存储在 Redis 中的用户敏感信息
 */
@Getter
@Setter
public class LoginUser {

    private Integer identity; // 1-普通用户, 2-管理员

    private String nickName; // 用户昵称

    private String headImage;
}
