package com.hjl.hjlpicturebackend.service.impl;

import com.hjl.hjlpicturebackend.exception.ErrorCode;
import com.hjl.hjlpicturebackend.exception.ThrowUtils;
import com.hjl.hjlpicturebackend.manager.upload.PictureUploadTemplate;
import java.io.File;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FilePictureUpload extends PictureUploadTemplate {
  @Override
  protected void validPicture(Object inputSource) {
    MultipartFile multipartFile = (MultipartFile) inputSource;
    ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "文件不能为空");
    int size = 2 * 1024 * 1024;
    ThrowUtils.throwIf(multipartFile.getSize() > size, ErrorCode.PARAMS_ERROR, "文件大小不能超过2MB");
    List<String> pictureCategory = List.of("jpg", "jpeg", "png", "gif", "webp");
    ThrowUtils.throwIf(
        !pictureCategory.contains(multipartFile.getOriginalFilename()),
        ErrorCode.NOT_FOUND_ERROR,
        "文件类型错误");
  }

  @Override
  protected String getOriginFilename(Object inputSource) {
    MultipartFile multipartFile = (MultipartFile) inputSource;
    return multipartFile.getOriginalFilename();
  }

  @Override
  protected void processFile(Object inputSource, File file) throws Exception {
    MultipartFile multipartFile = (MultipartFile) inputSource;
    multipartFile.transferTo(file);
  }
}
