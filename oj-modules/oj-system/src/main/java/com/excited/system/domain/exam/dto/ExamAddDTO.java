package com.excited.system.domain.exam.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ExamAddDTO {

    private String title;

    // 使用该注解可以自动将前端发送的字符串参数转化为日期类型
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    // 使用该注解可以自动将前端发送的字符串参数转化为日期类型
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
