package com.excited.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.excited.common.core.domain.R;
import com.excited.common.core.enums.ResultCode;
import com.excited.system.domain.SysUser;
import com.excited.system.mapper.SysUserMapper;
import com.excited.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl implements ISysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public R<Void> login(String userAccount, String password) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        SysUser sysUser = sysUserMapper.selectOne(queryWrapper.select(SysUser::getPassword)
                .eq(SysUser::getUserAccount, userAccount));

        R<Void> loginResult = new R<>();
        if (sysUser == null) {
            loginResult.setCode(ResultCode.FAILED_USER_EXISTS.getCode());
            loginResult.setMsg(ResultCode.FAILED_USER_EXISTS.getMsg());
            return loginResult;
        }
        if (!sysUser.getPassword().equals(password)) {
            loginResult.setCode(ResultCode.FAILED_LOGIN.getCode());
            loginResult.setMsg(ResultCode.FAILED_LOGIN.getMsg());
            return loginResult;
        }

        loginResult.setCode(ResultCode.SUCCESS.getCode());
        loginResult.setMsg(ResultCode.SUCCESS.getMsg());
        return loginResult;
    }
}
