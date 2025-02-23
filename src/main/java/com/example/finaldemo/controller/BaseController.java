package com.example.finaldemo.controller;

import cn.hutool.core.util.StrUtil;
import com.example.finaldemo.common.BaseResponse;
import com.example.finaldemo.common.utils.ResultUtil;
import com.example.finaldemo.common.utils.ThrowUtil;
import com.example.finaldemo.controller.aop.AuthRequired;
import com.example.finaldemo.controller.aop.Login;
import com.example.finaldemo.dao.model.enums.UserRoleEnum;
import com.example.finaldemo.exception.BusinessException;
import com.example.finaldemo.exception.ErrorCode;
import com.example.finaldemo.manager.CosManager;
import com.example.finaldemo.manager.KeyManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@RestController
public class BaseController {


    private final KeyManager keyManager;
    private final CosManager cosManager;


    public BaseController(KeyManager keyManager, CosManager cosManager) {
        this.keyManager = keyManager;
        this.cosManager = cosManager;
    }

    @GetMapping("/alive")
    public BaseResponse<String> isAlive() {
        return ResultUtil.success("alive now");
    }

    @GetMapping("/rsa")
    public BaseResponse<String> getPublicKey() {
        String pubKey = keyManager.getPublicKey();
        return ResultUtil.success(pubKey);
    }

    @Login
    @AuthRequired(level = UserRoleEnum.ADMIN)
    @PostMapping("/test-upload")
    public BaseResponse<String> testUploadFile(@RequestPart("file") MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        ThrowUtil.throwIf(StrUtil.isBlank(originalFilename), ErrorCode.PARAMS_ERROR, "文件名不可为空");
        String path = String.format("/test/%s", originalFilename);

        File tmpFile = null;

        try {
            tmpFile = File.createTempFile(originalFilename, null);
            file.transferTo(tmpFile);
            cosManager.putObject(path, tmpFile);
            return ResultUtil.success(path);
        } catch (IOException e) {
            log.error("{}::{},上传出错，filePath::{}::originalFilename::{}", getClass(), Thread.currentThread().getStackTrace()[2].getLineNumber(), path, originalFilename);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        } finally {
            if (Objects.nonNull(tmpFile)) {
                if (!tmpFile.delete()) {
                    log.error("{}::{},删除失败", getClass(), Thread.currentThread().getStackTrace()[2].getLineNumber());
                }
            }
        }

    }

}
