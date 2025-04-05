package com.excited.common.core.enums;

import lombok.Getter;

@Getter
public enum ExamListType {

    EXAM_TIME_LIST(0),

    EXAM_HISTORY_LIST(1),

    USER_EXAM_LIST(2);

    private final Integer value;

    ExamListType(Integer value) {
        this.value = value;
    }
}
