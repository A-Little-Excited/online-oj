package com.excited.friend.service;

import com.excited.friend.domain.dto.UserGetCodeDTO;
import com.excited.friend.domain.dto.UserLoginDTO;

public interface IUserService {
    boolean sendCode(UserGetCodeDTO userGetCodeDTO);

    String codeLogin(UserLoginDTO userLoginDTO);

    boolean logout(String token);
}
