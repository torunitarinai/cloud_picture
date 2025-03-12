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
        // 获取异常的第一个堆栈元素
        StackTraceElement stackTraceElement = e.getStackTrace()[0];
        log.error("解密错误发生于 {}#{} (行: {})",
                stackTraceElement.getClassName(),
                stackTraceElement.getMethodName(),
                stackTraceElement.getLineNumber(),
                e);  // 将异常对象作为日志的最后一个参数，这样可以打印完整堆栈
        return ResultUtil.error(ErrorCode.SYSTEM_ERROR);
    }


}


