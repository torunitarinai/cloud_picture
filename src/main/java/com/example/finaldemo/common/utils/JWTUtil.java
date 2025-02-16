package com.example.finaldemo.common.utils;

import cn.hutool.core.util.StrUtil;
import com.example.finaldemo.exception.BusinessException;
import com.example.finaldemo.exception.ErrorCode;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public class JWTUtil {
    /**
     * @param userId     用户主键Id
     * @param role       用户角色
     * @param userName   用户昵称
     * @param expireTime 过期时间（毫秒）
     * @return
     */
    public static Map<String, String> generateJwt(String secretKey, long userId, String role, String userName, long expireTime) {
        //默认过期时间1小时
        if (expireTime == 0) {
            expireTime = 60 * 60 * 1000;
        }

        if (StrUtil.isBlank(role) || StrUtil.isBlank(userName)) {
            log.error("role::{},userName::{}", role, userName);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "参数错误");
        }

        long expMillis = System.currentTimeMillis() + expireTime;
        Date expDate = new Date(expMillis);
        //生成 HMAC 密钥，根据提供的字节数组长度选择适当的 HMAC 算法，并返回相应的 SecretKey 对象。
        SecretKey sk = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        //组装claims
        Map<String, Object> claims = new HashMap<>() {{
            put("userId", userId);
            put("role", role);
            put("userName", userName);
            put("description", "access");
        }};


        JwtBuilder accessBuilder = Jwts.builder().signWith(sk)
                .claims(claims)
                .expiration(expDate);

        //refreshToken组装
        long refreshExp = 3 * 24 * 60 * 60 * 1000;
        Date refreshExpDate = new Date(System.currentTimeMillis() + refreshExp);
        Map<String, Object> refreshClaims = new HashMap<>() {{
            put("userId", userId);
            put("role", role);
            put("userName", userName);
            put("description", "refresh");
        }};

        JwtBuilder refreshBuilder = Jwts.builder().signWith(sk)
                .claims(refreshClaims)
                .expiration(refreshExpDate);
        return new HashMap<>() {{
            put("accessToken", accessBuilder.compact());
            put("refreshToken", refreshBuilder.compact());
        }};
    }

    /**
     * @param secretKey 秘钥
     * @param userId    用户主键Id
     * @param role      用户角色
     * @param userName  用户昵称
     * @param amount    几小时后过期
     * @return  Map<String, String> 包含accessToken和refreshToken
     */
    public static Map<String, String> generateJwt(String secretKey, long userId, String role, String userName, int amount) {
        //默认过期时间1小时
        Date expDate;
        Calendar calendar = Calendar.getInstance();
        if (amount <= 0) {
            calendar.add(Calendar.HOUR, 1);
            expDate = calendar.getTime();
        } else {
            calendar.add(Calendar.HOUR,amount);
            expDate = calendar.getTime();
        }


        if (StrUtil.isBlank(role) || StrUtil.isBlank(userName)) {
            log.error("role::{},userName::{}", role, userName);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "参数错误");
        }


        //生成 HMAC 密钥，根据提供的字节数组长度选择适当的 HMAC 算法，并返回相应的 SecretKey 对象。
        SecretKey sk = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        //组装claims
        Map<String, Object> claims = new HashMap<>() {{
            put("userId", userId);
            put("role", role);
            put("userName", userName);
            put("description", "access");
        }};


        JwtBuilder accessBuilder = Jwts.builder().signWith(sk)
                .claims(claims)
                .expiration(expDate);

        //refreshToken组装

        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DAY_OF_MONTH,5);
        Date refreshExpDate = instance.getTime();
        Map<String, Object> refreshClaims = new HashMap<>() {{
            put("userId", userId);
            put("role", role);
            put("userName", userName);
            put("description", "refresh");
        }};

        JwtBuilder refreshBuilder = Jwts.builder().signWith(sk)
                .claims(refreshClaims)
                .expiration(refreshExpDate);
        return new HashMap<>() {{
            put("accessToken", accessBuilder.compact());
            put("refreshToken", refreshBuilder.compact());
        }};
    }

}
