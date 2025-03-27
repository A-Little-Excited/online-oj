package com.excited.system.service.user.impl;

import com.excited.common.core.enums.ResultCode;
import com.excited.common.security.exception.ServiceException;
import com.excited.system.domain.user.dto.UserQueryDTO;
import com.excited.system.domain.user.dto.UserUpdateStatusDTO;
import com.excited.system.domain.user.entity.User;
import com.excited.system.domain.user.vo.UserVO;
import com.excited.system.mapper.user.UserMapper;
import com.excited.system.service.user.IUserService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<UserVO> list(UserQueryDTO userQueryDTO) {
        PageHelper.startPage(userQueryDTO.getPageNum(), userQueryDTO.getPageSize());
        return userMapper.selectUserList(userQueryDTO);
    }

    @Override
    public int updateStatus(UserUpdateStatusDTO userUpdateStatusDTO) {
        User user = userMapper.selectById(userUpdateStatusDTO.getUserId());
        if (user == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        user.setStatus(userUpdateStatusDTO.getStatus());
        return userMapper.updateById(user);
    }
}
