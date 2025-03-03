package com.excited.system.domain.sysUser.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.excited.common.core.domain.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@TableName("tb_sys_user")
@Getter
@Setter
@ToString
public class SysUser extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long userId;

    private String userAccount;

    private String password;

    private String nickName;
}
