package com.example.finaldemo.service;

import com.example.finaldemo.common.utils.JWTUtil;
import com.example.finaldemo.exception.BusinessException;
import com.example.finaldemo.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class JWTService {
    @Value("${jwt.secret-key}")
    private String secretKey;

    public Map<String, String> getJwtMap(String userAccount, String role, String userName, int amount) {
        return JWTUtil.generateJwt(secretKey, userAccount, role, userName, amount);
    }

    /**
     * @param token 验证accessToken或refreshToken
     * @return 是否有效 有效->true
     */
    public boolean verify(String token) {
        Claims verify = JWTUtil.verify(secretKey, token);
        return Objects.nonNull(verify);
    }

    public Optional<Claims> getTokenClaims(String token) {
        return Optional.ofNullable(JWTUtil.verify(secretKey, token));
    }

    public String refreshAccessToken(String refreshToken) {
        Optional<Claims> tokenClaims = getTokenClaims(refreshToken);

        tokenClaims.ifPresentOrElse(claims -> claims.put("description", "access"), () -> {
            log.error("{}::refreshToken丢失Claims", getClass());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "token不合法");
        });

        return JWTUtil.generateAccessToken(tokenClaims.get(), this.secretKey, 0);
    }
}
