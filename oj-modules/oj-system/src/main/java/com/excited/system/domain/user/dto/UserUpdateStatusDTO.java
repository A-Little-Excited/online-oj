package com.excited.system.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateStatusDTO {

    private Long userId;

    private Integer status;
}
