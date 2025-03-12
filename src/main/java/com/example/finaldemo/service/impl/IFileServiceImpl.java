package com.example.finaldemo.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import com.example.finaldemo.common.utils.ThrowUtil;
import com.example.finaldemo.dao.model.dto.file.UploadPictureResult;
import com.example.finaldemo.exception.ErrorCode;
import com.example.finaldemo.manager.CosManager;
import com.example.finaldemo.service.IFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Objects;

@Slf4j
@Service
public class IFileServiceImpl implements IFileService {
    public static final long LIMITED = 2 * 1024 * 1024L;
    public static final String[] ALLOWED_SUFFIX = {"jpeg", "jpg", "png", "webp"};

    private final CosManager cosManager;
    public IFileServiceImpl(CosManager cosManager) {
        this.cosManager = cosManager;
    }

    @Override
    public UploadPictureResult uploadPicture(MultipartFile file, String cosPath) {
        ThrowUtil.throwIf(Objects.isNull(file), ErrorCode.SYSTEM_ERROR, () -> log.error("file为空"));
        //文件校验
        isValidated(file);
        //上传文件
        String uuid = IdUtil.simpleUUID();
        String suffix = FileUtil.getSuffix(file.getOriginalFilename());

        cosManager.putPictureObject()
    }


    private void isValidated(MultipartFile file) {
        long size = file.getSize();
        ThrowUtil.throwIf(size > LIMITED, ErrorCode.PARAMS_ERROR, "文件大小超过2M");
        ThrowUtil.throwIf(Arrays.stream(ALLOWED_SUFFIX).anyMatch(
                str -> str.equals(FileUtil.getSuffix(file.getOriginalFilename()))
        ), ErrorCode.PARAMS_ERROR, "文件不支持");
    }

}
