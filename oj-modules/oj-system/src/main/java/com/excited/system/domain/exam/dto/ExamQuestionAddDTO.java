package com.excited.system.domain.exam.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;

@Getter
@Setter
public class ExamQuestionAddDTO {

    private Long examId;

    // 使用 set 对 questionId 进行自动去重, 防止同一竞赛中添加了重复的题目
    // 使用 LinkedHashSet 保证了元素顺序, 因此存储到数据库中的竞赛题目是有"顺序"字段的
    private LinkedHashSet<Long> questionIdSet;
}
