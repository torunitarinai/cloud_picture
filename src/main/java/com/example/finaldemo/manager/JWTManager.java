package com.example.finaldemo.manager;

import cn.hutool.core.util.StrUtil;
import com.example.finaldemo.common.utils.JWTUtil;
import com.example.finaldemo.common.utils.ThrowUtil;
import com.example.finaldemo.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class JWTManager {

    private final SecretKey SECRET_KEY = JWTUtil.getSecretKey();

    public Map<String, String> getJwtMap(String userAccount, String role, String userName, int amount) {
        return JWTUtil.generateJwt(SECRET_KEY, userAccount, role, userName, amount);
    }

    /**
     * @param token 验证accessToken或refreshToken
     * @return 是否有效 有效->true
     */
    public boolean verify(String token) {
        if (StrUtil.isBlank(token)) {
            return false;
        }
        Claims verify = JWTUtil.verify(SECRET_KEY, token);
        return Objects.nonNull(verify);
    }

    public Optional<Claims> getTokenClaims(String token) {
        return Optional.ofNullable(JWTUtil.verify(SECRET_KEY, token));
    }

    public String refreshAccessToken(String refreshToken) {
        Optional<Claims> tokenClaims = getTokenClaims(refreshToken);

        ThrowUtil.throwIf(tokenClaims.isEmpty(), ErrorCode.PARAMS_ERROR,"非法token");

        Claims claims = tokenClaims.get();
        Map<String, Object> newClaims = new HashMap<>(claims);

        return JWTUtil.generateAccessToken(newClaims, this.SECRET_KEY, 0);
    }
}
