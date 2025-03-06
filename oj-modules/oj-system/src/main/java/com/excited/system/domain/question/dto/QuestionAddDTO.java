package com.excited.system.domain.question.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionAddDTO {

    @Schema(description = "题目标题")
    @NotBlank(message = "题目标题不能为空")
    private String title;

    @Schema(description = "题目内容")
    @NotBlank(message = "题目内容不能为空")
    private String content;

    @Schema(description = "题目难度: 1-简单 2-中等 3-困难")
    @NotNull(message = "题目难度不能为空")
    @Max(value = 3, message = "题目难度最大值为 3")
    @Min(value = 1, message = "题目难度最小值为 1")
    private Integer difficulty;

    @Schema(description = "时间限制")
    @NotNull(message = "时间限制不能为空")
    private Long timeLimit;

    @Schema(description = "空间限制")
    @NotNull(message = "空间限制不能为空")
    private Long spaceLimit;

    @Schema(description = "题目用例")
    @NotNull(message = "题目用例不能为空")
    private String questionCase;

    @Schema(description = "默认代码块")
    @NotNull(message = "默认代码块不能为空")
    private String defaultCode;

    @Schema(description = "main方法")
    @NotNull(message = "main方法不能为空")
    private String mainFunc;
}
