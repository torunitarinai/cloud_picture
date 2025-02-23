package com.example.finaldemo.controller.interceptor;

import cn.hutool.core.util.StrUtil;
import com.example.finaldemo.common.utils.ThrowUtil;
import com.example.finaldemo.controller.aop.Login;
import com.example.finaldemo.dao.domain.UserDto;
import com.example.finaldemo.manager.UserContext;
import com.example.finaldemo.dao.domain.User;
import com.example.finaldemo.exception.BusinessException;
import com.example.finaldemo.exception.ErrorCode;
import com.example.finaldemo.manager.JWTManager;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Optional;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    private final JWTManager jwtManager;

    public LoginInterceptor(JWTManager jwtManager) {
        this.jwtManager = jwtManager;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();



        // 只对打了 @Login 注解的方法进行登录检查
        if (!isLoginRequired(handler)) {

            return true;
        }

        String token = request.getHeader("token");
        ThrowUtil.throwIf(StrUtil.isBlank(token), ErrorCode.NOT_LOGIN_ERROR, () -> log.error("{}::2路径 {}", getClass().getSimpleName(), requestURI));

        Optional<Claims> tokenClaims = jwtManager.getTokenClaims(token);
        if (tokenClaims.isEmpty()) {
            log.error("{}::Token不合法", getClass());
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "非法token");
        } else {
            // 放入上下文
            Claims claims = tokenClaims.get();
            UserDto user = UserDto.builder()
                    .userAccount((String) claims.get("userAccount"))
                    .userRole((String) claims.get("role"))
                    .userName((String) claims.get("userName"))
                    .build();

            UserContext.add(user);
            // 放行
            return true;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (!isLoginRequired(handler)){
            return;
        }
        UserContext.remove();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    private boolean isLoginRequired(Object handler) {
        if (handler instanceof HandlerMethod) {
            Method method = ((HandlerMethod) handler).getMethod();

            // 判断方法是否有 @Login 注解
            return method.isAnnotationPresent(Login.class);
        }
        return false;
    }


}
