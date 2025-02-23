package com.example.finaldemo.controller.aop;

import com.example.finaldemo.common.utils.ThrowUtil;
import com.example.finaldemo.dao.model.enums.UserRoleEnum;
import com.example.finaldemo.exception.BusinessException;
import com.example.finaldemo.exception.ErrorCode;
import com.example.finaldemo.manager.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Aspect
@Component
@Slf4j
public class AuthCheckHandler {
    @Pointcut("@annotation(AuthRequired)")
    public void pointCut() {
    }

    @Before("pointCut()")
    public void authCheck(JoinPoint joinPoint) {
        AuthRequired authRequired = joinPoint.getTarget().getClass().getAnnotation(AuthRequired.class);
        UserRoleEnum requiredLevel = authRequired.level();
        String role = UserContext.getCurrentUserRole();

        UserRoleEnum userLevel = UserRoleEnum.getEnumByValue(role);
        ThrowUtil.throwIf(Objects.isNull(userLevel), ErrorCode.OPERATION_ERROR, () -> log.error("{}::{},UserRole Enum转换失败，结果为空。role::{}", getClass(), Thread.currentThread().getStackTrace()[2].getLineNumber(),role));
        if (requiredLevel.getLevel() - userLevel.getLevel() > 0) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
    }

}
