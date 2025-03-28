package com.excited.system.domain.question.dto;

import com.excited.common.core.domain.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class QuestionQueryDTO extends PageQueryDTO {

    @Schema(description = "题目标题")
    private String title;

    @Schema(description = "题目难度")
    private Integer difficulty;

    // 被排除的题目 Id 集合(用于添加或编辑竞赛时展示题目列表, 需要将已选择的进行排除)
    private Set<Long> excludeIdSet;

    // 被排除的题目 Id 集合组成的字符串, 题目 Id 之间使用分隔符进行区分
    private String excludeIdStr;
}
