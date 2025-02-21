package com.excited.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.excited.common.core.domain.R;
import com.excited.common.core.enums.ResultCode;
import com.excited.common.core.enums.UserIdentity;
import com.excited.common.security.exception.ServiceException;
import com.excited.common.security.service.JwtService;
import com.excited.system.domain.entity.SysUser;
import com.excited.system.domain.dto.SysUserSaveDTO;
import com.excited.system.mapper.SysUserMapper;
import com.excited.system.service.ISysUserService;
import com.excited.system.utils.BCryptUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RefreshScope
public class SysUserServiceImpl implements ISysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private JwtService jwtService;

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public R<String> login(String userAccount, String password) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        SysUser sysUser = sysUserMapper
                .selectOne(queryWrapper.select(SysUser::getUserId, SysUser::getPassword)
                .eq(SysUser::getUserAccount, userAccount));

        if (sysUser == null) {
            return R.fail(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        if (!BCryptUtils.matchesPassword(password, sysUser.getPassword())) {
            return R.fail(ResultCode.FAILED_LOGIN);
        }

        return R.ok(jwtService.createToken(sysUser.getUserId(), secret,
                UserIdentity.ADMIN.getValue()));
    }

    @Override
    public int add(SysUserSaveDTO sysUserSaveDTO) {
        // 先判断数据库中是否存在相同用户名的管理员
        List<SysUser> sysUserList = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserAccount, sysUserSaveDTO.getUserAccount()));
        if (CollectionUtil.isNotEmpty(sysUserList)) {
            throw new ServiceException(ResultCode.FAILED_USER_EXISTS);
        }

        SysUser sysUser = new SysUser();
        sysUser.setUserAccount(sysUserSaveDTO.getUserAccount());
        sysUser.setPassword(BCryptUtils.encryptPassword(sysUserSaveDTO.getPassword()));
        return sysUserMapper.insert(sysUser);
    }
}
