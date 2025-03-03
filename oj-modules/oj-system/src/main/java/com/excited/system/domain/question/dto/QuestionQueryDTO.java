package com.excited.system.domain.question.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionQueryDTO {

    private String title;

    private Integer difficulty;

    /**
     * 当前端没有指定时, 默认为请求第 1 页数据, 单页数据量为 10
     */
    private Integer pageSize = 10;  // 单页数据量, 必要参数

    private Integer pageNum = 1;    // 当前页数
}
