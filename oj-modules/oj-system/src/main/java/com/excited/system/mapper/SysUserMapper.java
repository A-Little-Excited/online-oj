package com.excited.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.excited.system.domain.sysUser.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
