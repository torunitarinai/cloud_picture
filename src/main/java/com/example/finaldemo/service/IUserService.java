package com.example.finaldemo.service;

import java.util.Map;

public interface IUserService {
    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    Map<String, String> userRegister(String userAccount, String userPassword, String checkPassword);


    Map<String, String> userLogin(String userAccount, String userPassword);

    String userRefreshAccessToken(String refreshToken);

    void userLogout(String accessToken, String refreshToken);

}
