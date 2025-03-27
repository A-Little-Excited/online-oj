package com.excited.system.domain.user.dto;

import com.excited.common.core.domain.dto.PageQueryDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserQueryDTO extends PageQueryDTO {

    private Long userId;

    private String nickName;
}
