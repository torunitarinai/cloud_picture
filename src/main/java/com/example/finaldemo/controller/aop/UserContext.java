package com.example.finaldemo.controller.aop;

import cn.hutool.core.util.StrUtil;
import com.example.finaldemo.dao.domain.User;

import java.util.Objects;

public final class UserContext {
    private static final ThreadLocal<User> USER_THREAD_LOCAL = new ThreadLocal<>();

    public static void add(User user) {
        USER_THREAD_LOCAL.set(user);
    }

    public static String getCurrentUserAccount() {
        User user = USER_THREAD_LOCAL.get();
        if (Objects.isNull(user) || StrUtil.isBlank(user.getUserAccount())) {
            return "";
        } else {
            return user.getUserAccount();
        }

    }

    public static void remove(){
        USER_THREAD_LOCAL.remove();
    }
}
