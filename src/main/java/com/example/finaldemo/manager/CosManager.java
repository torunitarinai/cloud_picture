package com.example.finaldemo.manager;

import com.example.finaldemo.config.CosClientConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class CosManager {
    private final CosClientConfig clientConfig;
    private final COSClient cosClient;

    public CosManager(CosClientConfig cosClientConfig, COSClient cosClient) {
        this.cosClient = cosClient;
        this.clientConfig = cosClientConfig;
    }

    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(clientConfig.getBucket(), key, file);
        return cosClient.putObject(putObjectRequest);
    }


    public PutObjectResult putPictureObject(String key, File file) {

        PutObjectRequest putObjectRequest = new PutObjectRequest(clientConfig.getBucket(), key, file);
        PicOperations picOperations = new PicOperations();

        picOperations.setIsPicInfo(1);
        putObjectRequest.setPicOperations(picOperations);
        return cosClient.putObject(putObjectRequest);
    }



}
