package com.excited.friend.domain.exam.dto;

import com.excited.common.core.domain.dto.PageQueryDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamQueryDTO extends PageQueryDTO {

    // 由于前端传递的时间是 String 类型, 因此使用 String 来接收
    private String startTime;

    // 由于前端传递的时间是 String 类型, 因此使用 String 来接收
    private String endTime;

    // 0-未完赛, 1-历史竞赛
    private Integer type;
}
