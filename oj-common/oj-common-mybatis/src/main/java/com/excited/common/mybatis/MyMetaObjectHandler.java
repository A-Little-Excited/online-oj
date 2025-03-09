package com.excited.common.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "createBy", Long.class, 1L);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 由于 strictUpdateFill 方法的默认策略是: 如果字段已经有值, 则不会覆盖; 如果填充值为 null, 则不会填充
        // 因此改为使用 setFieldValByName, 以保证每次更新数据时下述两个字段都会自动更新
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("updateBy", 1L, metaObject);
    }
}
