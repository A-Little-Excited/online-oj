package com.excited.system.domain.question.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.excited.common.core.domain.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("tb_question")
public class Question extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long questionId;         // 题目 id

    private String title;            // 题目标题

    private String content;          // 题目内容

    private Integer difficulty;      // 题目难度: 1-简单 2-中等 3-困难

    private Long timeLimit;          // 时间限制

    private Long spaceLimit;         // 空间限制

    private String questionCase;     // 题目用例

    private String defaultCode;      // 默认代码块

    private String mainFunc;         // main 方法
}
