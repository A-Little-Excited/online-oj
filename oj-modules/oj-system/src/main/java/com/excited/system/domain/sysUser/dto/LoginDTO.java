package com.excited.system.domain.sysUser.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDTO {

    @Schema(description = "管理员账号")
    @NotBlank(message = "账号不能为空")
    private String userAccount;

    @Schema(description = "管理员密码")
    @NotBlank(message = "密码不能为空")
    private String password;
}
