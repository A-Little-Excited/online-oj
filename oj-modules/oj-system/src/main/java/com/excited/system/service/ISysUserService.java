package com.excited.system.service;

import com.excited.common.core.domain.R;

public interface ISysUserService {
    R<Void> login(String userAccount, String password);
}
