package com.excited.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.excited.common.core.domain.R;
import com.excited.common.core.enums.ResultCode;
import com.excited.common.security.utils.JwtUtils;
import com.excited.system.domain.SysUser;
import com.excited.system.mapper.SysUserMapper;
import com.excited.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RefreshScope
public class SysUserServiceImpl implements ISysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public R<String> login(String userAccount, String password) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        SysUser sysUser = sysUserMapper.selectOne(queryWrapper.select(SysUser::getPassword)
                .eq(SysUser::getUserAccount, userAccount));

        if (sysUser == null) {
            return R.fail(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        if (!sysUser.getPassword().equals(password)) {
            return R.fail(ResultCode.FAILED_LOGIN);
        }

        // 1. 登录成功之后, 生成 token 返回给客户端
        Map<String, Object> claims = new HashMap<>();
        claims.put("sysUserId", sysUser.getUserId());
        String token = JwtUtils.createToken(claims, secret);
        // 2. 往 Redis 中写入敏感数据

        return R.ok(token);
    }
}
