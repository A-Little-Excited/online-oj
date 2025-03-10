package com.excited.system.domain.exam.dto;

import com.excited.common.core.domain.dto.PageQueryDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ExamQueryDTO extends PageQueryDTO {

    private String title;

    // 由于前端传递的时间是 String 类型, 因此使用 String 来接收
    private String startTime;

    // 由于前端传递的时间是 String 类型, 因此使用 String 来接收
    private String endTime;
}
