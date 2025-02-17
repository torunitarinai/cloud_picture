package com.example.finaldemo.common.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.AsymmetricAlgorithm;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.example.finaldemo.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;


/**
 * @since 2025/2/17 12:08:20
 *
 * <p>负责私钥公钥的生成和解密<p/>
 */
@Slf4j
public class RSAUtil {

    private RSAUtil() {
    }

    /**
     * @return Map key:String值为public/private,value:String 值为Base64编码后的钥匙字符串
     */
    public static Map<String, String> generateKeyMap() {
        KeyPair keyPair = SecureUtil.generateKeyPair(AsymmetricAlgorithm.RSA.getValue());

        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        String privateKeyStr = Base64.encode(privateKey.getEncoded());
        String publicKeyStr = Base64.encode(publicKey.getEncoded());

        ThrowUtil.throwIf(StrUtil.isBlank(privateKeyStr) || StrUtil.isBlank(publicKeyStr), ErrorCode.OPERATION_ERROR, () -> log.error("{}::秘钥生成失败", RSAUtil.class));

        return new HashMap<>() {{
            put("public", publicKeyStr);

            put("private", privateKeyStr);
        }};
    }

    public static String decrypt(String password,String privateKeyStr) {
        RSA rsa = new RSA(privateKeyStr, null);
        byte[] decrypt = rsa.decrypt(password, KeyType.PrivateKey);
        return StrUtil.str(decrypt, StandardCharsets.UTF_8);
    }

}
