package com.excited.system.controller;

import com.excited.common.core.domain.R;
import com.excited.system.domain.LoginDTO;
import com.excited.system.domain.SysUserSaveDTO;
import com.excited.system.domain.SysUserVO;
import com.excited.system.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sysUser")
@Tag(name = "管理员接口")
public class SysUserController {

    @Autowired
    private ISysUserService sysUserService;

    @PostMapping("/login")
    @Operation(summary = "管理员登录", description = "根据提供的账号密码进行管理员登录")
    @ApiResponse(responseCode = "1000", description = "操作成功")
    @ApiResponse(responseCode = "2000", description = "服务繁忙, 请稍后重试")
    @ApiResponse(responseCode = "3102", description = "用户不存在")
    @ApiResponse(responseCode = "3103", description = "用户名或密码错误")
    public R<Void> login(@RequestBody LoginDTO loginDTO) {
        return sysUserService.login(loginDTO.getUserAccount(), loginDTO.getPassword());
    }

    @PostMapping("/add")
    @Operation(summary = "新增管理员", description = "根据所提供的信息新增管理员")
    @ApiResponse(responseCode = "1000", description = "操作成功")
    @ApiResponse(responseCode = "2000", description = "服务繁忙, 请稍后重试")
    @ApiResponse(responseCode = "3101", description = "用户已存在")
    public R<Void> add(@RequestBody SysUserSaveDTO sysUserSaveDTO) {
        return null;
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "删除管理员", description = "根据 userId 删除指定管理员")
    @ApiResponse(responseCode = "1000", description = "操作成功")
    @ApiResponse(responseCode = "2000", description = "服务繁忙, 请稍后重试")
    @ApiResponse(responseCode = "3102", description = "用户不存在")
    @Parameters(value = {
            @Parameter(name = "userId", in = ParameterIn.PATH, description = "管理员ID")
    })
    public R<Void> delete(@PathVariable Long userId) {
        return null;
    }

    @GetMapping("/detail")
    @Operation(summary = "获取管理员详情", description = "根据查询条件查询管理员详情")
    @ApiResponse(responseCode = "1000", description = "操作成功")
    @ApiResponse(responseCode = "2000", description = "服务繁忙, 请稍后重试")
    @ApiResponse(responseCode = "3102", description = "用户不存在")
    public R<SysUserVO> detail(@RequestParam(required = false) Long userId, @RequestParam(required = true) String sex) {
        return null;
    }
}
