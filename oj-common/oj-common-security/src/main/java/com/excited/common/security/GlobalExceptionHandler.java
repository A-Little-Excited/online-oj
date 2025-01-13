package com.excited.common.security;

import com.excited.common.core.domain.R;
import com.excited.common.core.enums.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<?> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
                                                    HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址: '{}', 不支持'{}'请求", requestURI, e.getMethod());
        return R.fail(ResultCode.ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public R<?> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址: '{}', 发生运行时异常", requestURI);
        return R.fail(ResultCode.ERROR);
    }

    @ExceptionHandler(Exception.class)
    public R<?> handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址: '{}', 发生异常", requestURI);
        return R.fail(ResultCode.ERROR);
    }
}
