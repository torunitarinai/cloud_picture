package com.example.finaldemo.service.impl;

import cn.hutool.core.util.StrUtil;
import com.example.finaldemo.common.utils.JWTUtil;
import com.example.finaldemo.common.utils.RSAUtil;
import com.example.finaldemo.common.utils.ThrowUtil;
import com.example.finaldemo.dao.mapper.UserMapper;
import com.example.finaldemo.exception.BusinessException;
import com.example.finaldemo.exception.ErrorCode;
import com.example.finaldemo.service.IUserRegister;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserRegisterImpl implements IUserRegister {
    private final UserMapper userMapper;
    @Value("${jwt.secret-key}")
    private String secretKey;
    public UserRegisterImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        ThrowUtil.StrParamsBlankCheck(userAccount, userPassword, checkPassword);



        long count = userMapper.countByAccount(userAccount);
        ThrowUtil.throwIf(count != 0,ErrorCode.PARAMS_ERROR,"账号已存在");

        //region 注册账号

        //region 生成jwt
        //JWTUtil.generateJwt(secretKey,)
        //endregion

    }

    private void formatCheck(String userAccount, String userPassword, String checkPassword){
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
    }
}
