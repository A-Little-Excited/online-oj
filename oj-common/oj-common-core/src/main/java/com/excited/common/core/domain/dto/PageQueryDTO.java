package com.excited.common.core.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageQueryDTO {

    /**
     * 当前端没有指定时, 默认为请求第 1 页数据, 单页数据量为 10
     */
    private Integer pageSize = 10;  // 单页记录数

    private Integer pageNum = 1;    // 页码
}
