package com.excited.system.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.excited.system.domain.user.dto.UserQueryDTO;
import com.excited.system.domain.user.entity.User;
import com.excited.system.domain.user.vo.UserVO;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {
    List<UserVO> selectUserList(UserQueryDTO userQueryDTO);
}
