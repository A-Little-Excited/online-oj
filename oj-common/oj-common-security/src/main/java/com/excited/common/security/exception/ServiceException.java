package com.excited.common.security.exception;

import com.excited.common.core.enums.ResultCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
public class ServiceException extends RuntimeException {

    private final ResultCode resultCode;

    public ServiceException(ResultCode resultCode) {
        this.resultCode = resultCode;
    }
}
