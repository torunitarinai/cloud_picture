package com.example.finaldemo.service;

import com.example.finaldemo.dao.model.dto.file.UploadPictureResult;
import com.example.finaldemo.dao.model.dto.vo.picture.PictureVO;
import org.springframework.web.multipart.MultipartFile;

public interface IFileService {
    UploadPictureResult uploadPicture(MultipartFile file,String cosPath);
    PictureVO uploadPicture(MultipartFile file, long pictureId);
}
