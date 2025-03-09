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
// 由于 edit 功能所需参数只是比 add 功能多了 questionId, 因此直接继承 QuestionAddDTO
public class QuestionEditDTO extends QuestionAddDTO{

    @Schema(description = "题目Id")
    @NotNull(message = "题目Id不能为空")
    private Long questionId;
}
