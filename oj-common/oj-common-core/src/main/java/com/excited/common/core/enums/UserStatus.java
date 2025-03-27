package com.excited.common.core.enums;

import lombok.Getter;

@Getter
public enum UserStatus {

    Normal(0),
    Block(1);

    private Integer status;

    UserStatus(Integer status) {
        this.status = status;
    }
}
