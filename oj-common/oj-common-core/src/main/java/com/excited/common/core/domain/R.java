package com.excited.common.core.domain;

import com.excited.common.core.enums.ResultCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class R<T> {

    private int code;

    private String msg;

    private T data;

    public static <T> R<T> ok() {
        return assembleResult(null, ResultCode.SUCCESS);
    }

    public static <T> R<T> ok(T data) {
        return assembleResult(data, ResultCode.SUCCESS);
    }

    public static <T> R<T> fail() {
        return assembleResult(null, ResultCode.FAILED);
    }

    /**
     * 指定错误码
     * @param resultCode 特定错误码
     * @return 统一响应数据结构
     * @param <T> 携带数据的类型
     */
    public static <T> R<T> fail(ResultCode resultCode) {
        return assembleResult(null, resultCode);
    }

    private static <T> R<T> assembleResult(T data, ResultCode resultCode) {
        R<T> result = new R<>();
        result.setData(data);
        result.setCode(resultCode.getCode());
        result.setMsg(result.getMsg());
        return result;
    }
}
