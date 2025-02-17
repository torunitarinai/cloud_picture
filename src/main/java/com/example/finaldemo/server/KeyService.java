package com.example.finaldemo.server;

import com.example.finaldemo.common.utils.RSAUtil;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class KeyService {
    private String privateKey;
    private String publicKey;

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
}
