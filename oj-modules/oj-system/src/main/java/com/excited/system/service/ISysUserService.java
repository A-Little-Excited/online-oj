package com.excited.system.service;

import com.excited.common.core.domain.R;

public interface ISysUserService {
    R<String> login(String userAccount, String password);
}
