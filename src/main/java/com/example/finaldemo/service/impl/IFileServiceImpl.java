package com.example.finaldemo.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import com.example.finaldemo.common.utils.ThrowUtil;
import com.example.finaldemo.config.CosClientConfig;
import com.example.finaldemo.dao.domain.Picture;
import com.example.finaldemo.dao.mapper.PictureMapper;
import com.example.finaldemo.dao.model.dto.file.UploadPictureResult;
import com.example.finaldemo.dao.model.dto.vo.picture.PictureVO;
import com.example.finaldemo.exception.BusinessException;
import com.example.finaldemo.exception.ErrorCode;
import com.example.finaldemo.manager.CosManager;
import com.example.finaldemo.manager.UserContext;
import com.example.finaldemo.service.IFileService;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

@Slf4j
@Service
public class IFileServiceImpl implements IFileService {
    public static final long LIMITED = 2 * 1024 * 1024L;
    public static final String[] ALLOWED_SUFFIX = {"jpeg", "jpg", "png", "webp"};

    private final CosManager cosManager;
    private final CosClientConfig clientConfig;
    private final PictureMapper pictureMapper;

    public IFileServiceImpl(CosManager cosManager, CosClientConfig clientConfig, PictureMapper pictureMapper) {
        this.cosManager = cosManager;
        this.clientConfig = clientConfig;
        this.pictureMapper = pictureMapper;
    }

    @Override
    public UploadPictureResult uploadPicture(MultipartFile file, String cosPath) {
        ThrowUtil.throwIf(Objects.isNull(file), ErrorCode.SYSTEM_ERROR, () -> log.error("file为空"));
        //文件校验
        isValidated(file);
        //上传文件
        String uuid = IdUtil.simpleUUID();
        String suffix = FileUtil.getSuffix(file.getOriginalFilename());
        String uploadFileName = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid, suffix);
        String uploadFilePath = String.format("/%s/%s", cosPath, uploadFileName);
        File temp = null;
        try {
            temp = File.createTempFile(uploadFilePath, null);
            file.transferTo(temp);
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadFilePath, temp);

            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            //封装返回参数
            int width = imageInfo.getWidth();
            int height = imageInfo.getHeight();
            return UploadPictureResult.builder().picName(FileUtil.mainName(file.getOriginalFilename()))
                    .picWidth(width)
                    .picHeight(height)
                    .picFormat(imageInfo.getFormat())
                    .picScale(NumberUtil.round(width * 1.0 / height, 2).doubleValue())
                    .picSize(FileUtil.size(temp))
                    .url(clientConfig.getHost() + "/" + uploadFilePath).build();

        } catch (IOException e) {
            // 获取异常的第一个堆栈元素
            StackTraceElement stackTraceElement = e.getStackTrace()[0];
            log.error("创建临时文件失败 {}#{} (行: {})",
                    stackTraceElement.getClassName(),
                    stackTraceElement.getMethodName(),
                    stackTraceElement.getLineNumber(),
                    e);  // 将异常对象作为日志的最后一个参数，这样可以打印完整堆栈
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        } finally {
            if (Objects.nonNull(temp) && !temp.delete()) {
                log.error("temp文件删除失败，filepath={}", temp.getAbsolutePath());
            }
        }

    }

    @Override
    public PictureVO uploadPicture(MultipartFile file, long pictureId) {
        long count = pictureMapper.count(pictureId);
        // 新增图片
        if (count == 0) {
            String cosPath = String.format("public/%s", UserContext.getCurrentUserAccount());
            UploadPictureResult uploadPictureResult = uploadPicture(file, cosPath);
            Picture picture = new Picture();
            picture.setUrl(uploadPictureResult.getUrl());
            picture.setName(uploadPictureResult.getPicName());
            picture.setPicSize(uploadPictureResult.getPicSize());
            picture.setPicWidth(uploadPictureResult.getPicWidth());
            picture.setPicHeight(uploadPictureResult.getPicHeight());
            picture.setPicScale(uploadPictureResult.getPicScale());
            picture.setPicFormat(uploadPictureResult.getPicFormat());
            picture.setAccount(UserContext.getCurrentUserAccount());
            int affect = pictureMapper.insert(picture);
            ThrowUtil.throwIf(affect != 1, ErrorCode.SYSTEM_ERROR, () -> log.error("图形信息插入失败"));
            return PictureVO.objToVo(picture);
        } else {
            // 更新操作，暂时觉得没意义，直接阻止
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
    }


    private void isValidated(MultipartFile file) {
        long size = file.getSize();
        ThrowUtil.throwIf(size > LIMITED, ErrorCode.PARAMS_ERROR, "文件大小超过2M");
        ThrowUtil.throwIf(Arrays.stream(ALLOWED_SUFFIX).anyMatch(
                str -> str.equals(FileUtil.getSuffix(file.getOriginalFilename()))
        ), ErrorCode.PARAMS_ERROR, "文件不支持");
    }


}
