package com.example.finaldemo.manager;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import com.example.finaldemo.common.utils.RSAUtil;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class KeyManager {
    private String privateKey;
    private String publicKey;

    private final MD5 md5 = new MD5();

    @PostConstruct
    public void init() {
        Map<String, String> keyMap = RSAUtil.generateKeyMap();
        this.publicKey = keyMap.get("public");
        this.privateKey = keyMap.get("private");
    }

    public String getPublicKey() {
        return this.publicKey;
    }

    public String getPrivateKey() {
        return this.privateKey;
    }

    public String decrypt(String userPassword) {
        return RSAUtil.decrypt(userPassword, this.privateKey);
    }


    public String md5Encode(String text, String salt) {
        if (StrUtil.isBlank(text)) {
            return "";
        }

        return md5.digestHex(text + salt);
    }

}
