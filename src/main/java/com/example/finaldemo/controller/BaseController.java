package com.example.finaldemo.controller;

import com.example.finaldemo.common.BaseResponse;
import com.example.finaldemo.common.utils.ResultUtil;
import com.example.finaldemo.service.KeyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class BaseController {


    private final KeyService keyService;


    public BaseController(KeyService keyService){
        this.keyService = keyService;
    }

    @GetMapping("/alive")
    public BaseResponse<String> isAlive() {
        return ResultUtil.success("alive now");
    }
    @GetMapping("/rsa")
    public BaseResponse<String> getPublicKey(){
        String pubKey = keyService.getPublicKey();
        return ResultUtil.success(pubKey);
    }
}
