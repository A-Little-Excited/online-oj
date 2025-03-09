package com.excited.system.domain.question.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionDetailVO {

    // 虽然前端展示不需要 questionId, 但是后续编辑题目需要借助于 questionId 进行
    // 前端接收该参数会由于雪花算法生成的 questionId 过长发生数据截断, 因此转化为 String 类型进行传递
    @JsonSerialize(using = ToStringSerializer.class)
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
