package com.example.finaldemo.controller;

import cn.hutool.core.util.StrUtil;
import com.example.finaldemo.common.BaseResponse;
import com.example.finaldemo.common.utils.ResultUtil;
import com.example.finaldemo.common.utils.ThrowUtil;
import com.example.finaldemo.dao.model.dto.user.UserLoginRequest;
import com.example.finaldemo.dao.model.dto.user.UserRefreshRequest;
import com.example.finaldemo.dao.model.dto.user.UserRegisterRequest;
import com.example.finaldemo.exception.BusinessException;
import com.example.finaldemo.exception.ErrorCode;
import com.example.finaldemo.service.IUserService;
import com.example.finaldemo.service.JWTService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;


/**
 * @Since 2025年2月17日22:17:56
 * <p>用户登入，注册，注销，获得当前登入用户</p>
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {


    private final IUserService userService;


    public UserController(IUserService userService, JWTService jwtService) {
        this.userService = userService;

    }


    @PostMapping("/register")
    public BaseResponse<Map<String, String>> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (Objects.isNull(userRegisterRequest) ||
                Objects.isNull(userRegisterRequest.getUserAccount()) ||
                Objects.isNull(userRegisterRequest.getUserPassword()) ||
                Objects.isNull(userRegisterRequest.getCheckPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }

        Map<String, String> jwtMap = userService.userRegister(userRegisterRequest.getUserAccount(),
                userRegisterRequest.getUserPassword(),
                userRegisterRequest.getCheckPassword());

        ThrowUtil.throwIf(Objects.isNull(jwtMap) || jwtMap.size() != 2, ErrorCode.OPERATION_ERROR, () -> log.error("jwtMap生成错误"));

        return ResultUtil.success(jwtMap);
    }


    @PostMapping("/login")
    public BaseResponse<Map<String, String>> login(@RequestBody UserLoginRequest loginRequest) {
        if (Objects.isNull(loginRequest) || StrUtil.isBlank(loginRequest.getUserAccount()) || StrUtil.isBlank(loginRequest.getUserPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }
        Map<String, String> jwtMap = userService.userLogin(loginRequest.getUserAccount(), loginRequest.getUserPassword());
        return ResultUtil.success(jwtMap);
    }

    @GetMapping("/refresh")
    public BaseResponse<String> refreshAccessToken(@RequestBody UserRefreshRequest refreshRequest) {
        ThrowUtil.throwIf(Objects.isNull(refreshRequest) || StrUtil.isBlank(refreshRequest.getRefreshToken()),
                ErrorCode.PARAMS_ERROR,
                () -> log.error("{}::request为空或者refreshToken为空", getClass()));
        String accessToken = userService.userRefreshAccessToken(refreshRequest.getRefreshToken());
        return ResultUtil.success(accessToken);
    }
}
