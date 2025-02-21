package com.excited.system.service;

import com.excited.common.core.domain.R;
import com.excited.system.domain.dto.SysUserSaveDTO;

public interface ISysUserService {
    R<String> login(String userAccount, String password);

    int add(SysUserSaveDTO sysUserSaveDTO);
}
