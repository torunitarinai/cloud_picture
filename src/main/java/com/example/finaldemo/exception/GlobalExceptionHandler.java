package com.example.finaldemo.exception;

import cn.hutool.crypto.CryptoException;
import com.example.finaldemo.common.BaseResponse;
import com.example.finaldemo.common.utils.ResultUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        return ResultUtil.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return ResultUtil.error(ErrorCode.SYSTEM_ERROR, "系统错误");
    }

    @ExceptionHandler(CryptoException.class)
    public BaseResponse<?> decryptionErrorHandler(CryptoException e){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
        log.error("{}::解密发生错误,位于::{}", stackTraceElement.getClassName(),stackTraceElement.getLineNumber());
        return ResultUtil.error(ErrorCode.SYSTEM_ERROR);
    }


}


