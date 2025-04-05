package com.excited.friend.service.user;

import com.excited.common.core.domain.entity.R;
import com.excited.common.core.domain.vo.LoginUserVO;
import com.excited.friend.domain.user.dto.UserGetCodeDTO;
import com.excited.friend.domain.user.dto.UserLoginDTO;

public interface IUserService {
    boolean sendCode(UserGetCodeDTO userGetCodeDTO);

    String codeLogin(UserLoginDTO userLoginDTO);

    boolean logout(String token);

    R<LoginUserVO> info(String token);
}
