package com.excited.system.domain.sysUser.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SysUserVO {

    @Schema(description = "管理员账号")
    private String userAccount;

    @Schema(description = "管理员用户昵称")
    private String nickname;
}
