package com.excited.system.service.user;

import com.excited.system.domain.user.dto.UserQueryDTO;
import com.excited.system.domain.user.dto.UserUpdateStatusDTO;
import com.excited.system.domain.user.vo.UserVO;

import java.util.List;

public interface IUserService {
    List<UserVO> list(UserQueryDTO userQueryDTO);

    int updateStatus(UserUpdateStatusDTO userUpdateStatusDTO);
}
