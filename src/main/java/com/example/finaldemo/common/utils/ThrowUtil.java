package com.example.finaldemo.common.utils;

import cn.hutool.core.util.StrUtil;
import com.example.finaldemo.exception.BusinessException;
import com.example.finaldemo.exception.ErrorCode;

import java.util.Objects;

public class ThrowUtil {

    /**
     * 条件成立则抛异常
     *
     * @param condition        条件
     * @param runtimeException 异常
     */
    public static void throwIf(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException;
        }
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition 条件
     * @param errorCode 错误码
     */
    public static void throwIf(boolean condition, ErrorCode errorCode) {
        throwIf(condition, new BusinessException(errorCode));
    }


    public static void throwIf(boolean condition, ErrorCode errorCode, Runnable r) {
        if (Objects.nonNull(r)) {
            r.run();
        }
        throwIf(condition, new BusinessException(errorCode));
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition 条件
     * @param errorCode 错误码
     * @param message   错误信息
     */
    public static void throwIf(boolean condition, ErrorCode errorCode, String message) {
        throwIf(condition, new BusinessException(errorCode, message));
    }

    public static void StrParamsBlankCheck(String... params) {
        if (params == null || params.length == 0) {
            return;
        }
        for (String param : params) {
            if (StrUtil.isBlank(param)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
    }
}

