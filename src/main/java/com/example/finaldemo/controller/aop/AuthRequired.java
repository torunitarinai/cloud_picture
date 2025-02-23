package com.example.finaldemo.controller.aop;

import com.example.finaldemo.dao.model.enums.UserRoleEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthRequired {
    UserRoleEnum level();
}
