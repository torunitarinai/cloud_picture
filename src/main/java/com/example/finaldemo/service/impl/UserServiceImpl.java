package com.example.finaldemo.service.impl;

import cn.hutool.core.util.StrUtil;
import com.example.finaldemo.common.utils.ThrowUtil;
import com.example.finaldemo.dao.domain.User;
import com.example.finaldemo.dao.domain.UserDto;
import com.example.finaldemo.dao.mapper.UserMapper;
import com.example.finaldemo.exception.BusinessException;
import com.example.finaldemo.exception.ErrorCode;
import com.example.finaldemo.manager.UserContext;
import com.example.finaldemo.service.IUserService;
import com.example.finaldemo.manager.JWTManager;
import com.example.finaldemo.manager.KeyManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {
    private final UserMapper userMapper;

    private final KeyManager keyManager;

    private final JWTManager jwtManager;


    public UserServiceImpl(UserMapper userMapper, KeyManager keyManager, JWTManager jwtManager) {
        this.userMapper = userMapper;
        this.keyManager = keyManager;
        this.jwtManager = jwtManager;
    }

    @Override
    public Map<String, String> userRegister(String userAccount, String userPassword, String checkPassword) {
        ThrowUtil.StrParamsBlankCheck(userAccount, userPassword, checkPassword);


        long count = userMapper.countByAccount(userAccount);
        ThrowUtil.throwIf(count != 0, ErrorCode.PARAMS_ERROR, "账号已存在");

        //region 注册账号
        String pwd = keyManager.decrypt(userPassword);
        String checkPwd = keyManager.decrypt(checkPassword);

        formatCheck(userAccount, pwd, checkPwd);

        //插入db
        String salt = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        User user = User.builder()
                .userAccount(userAccount)
                .userPassword(keyManager.md5Encode(userAccount, salt))
                .salt(salt)
                .userRole("user")
                .build();
        int affect = userMapper.insert(user);
        ThrowUtil.throwIf(affect == 0, ErrorCode.OPERATION_ERROR);
        //region 生成jwt
        Map<String, String> jwtMap = jwtManager.getJwtMap(user.getUserAccount(), user.getUserRole(), user.getUserName(), 0);
        //endregion
        return jwtMap;
        //endregion
    }

    @Override
    public Map<String, String> userLogin(String userAccount, String userPassword) {
        ThrowUtil.StrParamsBlankCheck(userAccount, userPassword);

        loginFormatCheck(userAccount, userPassword);


        //db确认账户存在
        User tmpUser = User.builder().userAccount(userAccount).build();
        UserDto dbUser = userMapper.selectByUserAccount(tmpUser);
        ThrowUtil.throwIf(Objects.isNull(dbUser), ErrorCode.PARAMS_ERROR, "账户不存在");
        //查出db账户得到盐值
        String salt = dbUser.getSalt();
        ThrowUtil.throwIf(StrUtil.isBlank(salt), ErrorCode.OPERATION_ERROR, () -> log.error("{}::盐值为空salt::{}", getClass(), salt));
        String decrypt = keyManager.decrypt(userPassword);
        ThrowUtil.throwIf(StrUtil.isBlank(decrypt), ErrorCode.OPERATION_ERROR, () -> log.error("{}::解密失败pwd::{}", getClass(), decrypt));
        //对照密码是否正确
        if (StrUtil.isNotBlank(dbUser.getUserPassword()) &&
                dbUser.getUserPassword().equals(keyManager.md5Encode(decrypt, salt))) {
            //生成&返回jwtMap
            Map<String, String> jwtMap = jwtManager.getJwtMap(dbUser.getUserAccount(), dbUser.getUserRole(), dbUser.getUserName(), 0);
            ThrowUtil.throwIf(Objects.isNull(jwtMap) || jwtMap.size() != 2, ErrorCode.OPERATION_ERROR, () -> log.error("{}::jwtMap生成失败", getClass()));
            return jwtMap;
        } else {
            log.error("{}::dbPassword::{},decrypt::{},encoded::{}", getClass(), dbUser.getUserPassword(), decrypt, keyManager.md5Encode(decrypt, salt));
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }


    }

    @Override
    public String userRefreshAccessToken(String refreshToken) {
        String accessToken = jwtManager.refreshAccessToken(refreshToken);
        ThrowUtil.throwIf(StrUtil.isBlank(accessToken),ErrorCode.OPERATION_ERROR,()->log.error("{}::accessToken生成失败",getClass()));
        return accessToken;
    }

    @Override
    public void userLogout(String accessToken, String refreshToken) {
        //todo 把tokens添加入Redis黑名单
        UserContext.remove();
    }

    private void loginFormatCheck(String userAccount, String userPassword) {
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
    }

    private void formatCheck(String userAccount, String userPassword, String checkPassword) {
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
