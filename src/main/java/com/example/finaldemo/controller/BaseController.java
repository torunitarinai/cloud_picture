package com.example.finaldemo.controller;

import com.example.finaldemo.common.BaseResponse;
import com.example.finaldemo.common.utils.ResultUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseController {
    @GetMapping("/alive")
    public BaseResponse<String> isAlive() {
        return ResultUtil.success("alive now");
    }
}
