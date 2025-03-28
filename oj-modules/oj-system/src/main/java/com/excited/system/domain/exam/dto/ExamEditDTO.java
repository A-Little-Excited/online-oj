package com.excited.system.domain.exam.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class ExamEditDTO extends ExamAddDTO {

    private Long examId;
}
