package com.example.finaldemo.manager;

import cn.hutool.core.util.StrUtil;
import com.example.finaldemo.dao.domain.User;
import com.example.finaldemo.dao.domain.UserDto;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
@Slf4j
public final class UserContext {
    private static final ThreadLocal<UserDto> USER_THREAD_LOCAL = new ThreadLocal<>();

    public static void add(UserDto user) {
        USER_THREAD_LOCAL.set(user);
    }

    public static String getCurrentUserAccount() {
        UserDto user = USER_THREAD_LOCAL.get();
        if (Objects.isNull(user) || StrUtil.isBlank(user.getUserAccount())) {
            return "";
        } else {
            return user.getUserAccount();
        }

    }

    public static String getCurrentUserRole() {
        UserDto user = USER_THREAD_LOCAL.get();
        if (Objects.isNull(user) || StrUtil.isBlank(user.getUserRole())) {
            return "";
        } else {
            return user.getUserRole();
        }

    }

    public static void remove(){
        StackTraceElement traceElement = Thread.currentThread().getStackTrace()[2];
        log.error("{}::{},user已卸载", traceElement.getClassName(),traceElement.getLineNumber());
        USER_THREAD_LOCAL.remove();
    }
}
