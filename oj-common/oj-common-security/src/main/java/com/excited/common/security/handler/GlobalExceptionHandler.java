package com.excited.common.security.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.excited.common.core.domain.entity.R;
import com.excited.common.core.enums.ResultCode;
import com.excited.common.security.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<?> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
                                                    HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址: '{}', 不支持'{}'请求", requestURI, e.getMethod(), e);
        return R.fail(ResultCode.ERROR);
    }

    @ExceptionHandler(ServiceException.class)
    public R<?> handleServiceException(ServiceException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        ResultCode resultCode = e.getResultCode();
        log.error("请求地址: '{}', 发生业务异常: {}", requestURI, resultCode.getMsg(), e);
        return R.fail(resultCode);
    }

    @ExceptionHandler(BindException.class)
    public R<Void> handleBindException(BindException e) {
        log.error(e.getMessage());
        // e.getAllErrors() 返回 List, 包含了所有 BindException 错误信息
        // DefaultMessageSourceResolvable::getDefaultMessage 是一个方法引用, 用于从每个错误中提取默认消息
        String message = join(e.getAllErrors(), DefaultMessageSourceResolvable::getDefaultMessage, ", ");
        return R.fail(ResultCode.FAILED_PARAMS_VALIDATE.getCode(), message);
    }

    /**
     * 用于将所有错误信息提取出来并重新拼接在一起, 错误信息之间使用 delimiter 进行分隔
     * @param collection 错误信息集合
     * @param function 映射函数, 用于从集合中的每个元素提取字符串
     * @param delimiter 分隔符
     * @return 重新拼接的错误信息字符串
     * @param <E> 集合元素
     */
    private <E> String join(Collection<E> collection, Function<E, String> function, CharSequence delimiter) {
        if (CollUtil.isEmpty(collection)) {
            return StrUtil.EMPTY;
        }

        return collection.stream()
                .map(function)             // 将集合中的元素 E 都映射为字符串
                .filter(Objects::nonNull)  // 去除 null 值
                .collect(Collectors.joining(delimiter)); // 使用分隔符拼接为字符长串
    }

    @ExceptionHandler(RuntimeException.class)
    public R<?> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址: '{}', 发生运行时异常", requestURI, e);
        return R.fail(ResultCode.ERROR);
    }

    @ExceptionHandler(Exception.class)
    public R<?> handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址: '{}', 发生异常", requestURI, e);
        return R.fail(ResultCode.ERROR);
    }
}
