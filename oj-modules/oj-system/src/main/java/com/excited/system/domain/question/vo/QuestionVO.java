package com.excited.system.domain.question.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class QuestionVO {

    // 前端接收该参数会由于雪花算法生成的 questionId 过长发生数据截断, 因此转化为 String 类型进行传递
    @JsonSerialize(using = ToStringSerializer.class)
    private Long questionId;

    private String title;

    private Integer difficulty;

    private String createName;

    // 将日期转化为特定格式的字符串, 前端展示更加优雅
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
