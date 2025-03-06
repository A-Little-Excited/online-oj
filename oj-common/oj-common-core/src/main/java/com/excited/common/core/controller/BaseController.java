package com.excited.common.core.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.excited.common.core.domain.entity.R;
import com.excited.common.core.domain.entity.TableDataInfo;
import com.github.pagehelper.PageInfo;

import java.util.List;

public class BaseController {

    public R<Void> toR(int raws) {
        return raws > 0 ? R.ok() : R.fail();
    }

    public R<Void> toR(boolean result) {
        return result ? R.ok() : R.fail();
    }

    public TableDataInfo getTableDataInfo(List<?> list) {
        if (CollectionUtil.isEmpty(list)) {
            return TableDataInfo.empty();
        }

        // 通过 PageHelper 插件提供的方式获取符合查询条件的总记录数
        long total = new PageInfo<>(list).getTotal();
        return TableDataInfo.success(list, total);
    }
}
