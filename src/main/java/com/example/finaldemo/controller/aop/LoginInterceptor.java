package com.example.finaldemo.controller.aop;

import cn.hutool.core.util.StrUtil;
import com.example.finaldemo.common.utils.ThrowUtil;
import com.example.finaldemo.exception.BusinessException;
import com.example.finaldemo.exception.ErrorCode;
import com.example.finaldemo.service.JWTService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 2025年2月17日22:30:51
 * <p>AOP拦截器，处理带有 @LoginRequired 注解的方法
 * </p>
 */
@Aspect
@Component
public class LoginInterceptor {

    private final HttpServletRequest httpServletRequest;

    private final JWTService jwtService;

    public LoginInterceptor(HttpServletRequest httpServletRequest, JWTService jwtService) {
        this.httpServletRequest = httpServletRequest;
        this.jwtService = jwtService;
    }

    @Pointcut("@annotation(com.example.finaldemo.controller.aop.LoginRequired)")
    public void pointer() {
    }

    @Before("pointer()")
    public void checkLogin(JoinPoint joinPoint) throws BusinessException {
        String token = httpServletRequest.getHeader("token");

        ThrowUtil.throwIf(StrUtil.isBlank(token), ErrorCode.NOT_LOGIN_ERROR);

        ThrowUtil.throwIf(!jwtService.verify(token),ErrorCode.NOT_LOGIN_ERROR);
    }

}
