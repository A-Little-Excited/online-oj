package com.excited.system.service.sysUser;

import com.excited.common.core.domain.entity.R;
import com.excited.common.core.domain.vo.LoginUserVO;
import com.excited.system.domain.sysUser.dto.SysUserAddDTO;

public interface ISysUserService {

    R<String> login(String userAccount, String password);

    boolean logout(String token);

    R<LoginUserVO> info(String token);

    int add(SysUserAddDTO sysUserAddDTO);
}
