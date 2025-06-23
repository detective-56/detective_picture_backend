package com.hjl.hjlpicturebackend.manager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.hjl.hjlpicturebackend.config.CosClientConfig;
import com.hjl.hjlpicturebackend.exception.ErrorCode;
import com.hjl.hjlpicturebackend.exception.ThrowUtils;
import com.hjl.hjlpicturebackend.model.dto.file.UploadPictureResult;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author hao jinglian
 * @description 文件上传服务
 * @createDate 2025/6/23
 */
@Service
@Slf4j
public class FileManager {

  @Resource private CosClientConfig cosClientConfig;

  @Resource private CosManager cosManager;

  /** 上传文件 */
  public UploadPictureResult uploadPicture(MultipartFile multipartFile, String uploadPathPrefix) {
    // 校验图片
    validPicture(multipartFile);
    // 图片上传地址
    String uuId = RandomUtil.randomString(16);
    String dateStr = DateUtil.formatDate(new Date());
    String originalFilename = multipartFile.getOriginalFilename();
    String uploadFileName = String.format("%s_%s.%s", dateStr, uuId, originalFilename);
    String uploadPath = String.format("/%s/%s", uploadPathPrefix, uploadFileName);
    File file = null;
    try {
      file = File.createTempFile(uploadPath, null);
      multipartFile.transferTo(file);
      // 上传图片
      PutObjectResult putObjectResult = cosManager.putObject(uploadPath, file);

      ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
      // 封装返回结果
      UploadPictureResult uploadPictureResult = new UploadPictureResult();
      int picWith = imageInfo.getWidth();
      int picHeight = imageInfo.getHeight();
      double picScale = NumberUtil.round(picWith * 1.0 / picHeight, 2).doubleValue();
      uploadPictureResult.setPicFormat(FileUtil.mainName(originalFilename));
      uploadPictureResult.setPicWidth(imageInfo.getWidth());
      uploadPictureResult.setPicHeight(imageInfo.getHeight());
      uploadPictureResult.setPicScale(picScale);
      uploadPictureResult.setPicFormat(imageInfo.getFormat());
      uploadPictureResult.setPicSize(FileUtil.size(file));
      uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath);
      return uploadPictureResult;
    } catch (IOException e) {
      log.error("图片上传到对象存储失败");
      throw new RuntimeException(e);
    } finally {
      this.deleteTempFile(file);
    }
  }

  /** 校验文件 */
  public void validPicture(MultipartFile multipartFile) {

    ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "文件不能为空");
    // 1. 校验文件大小
    long fileSize = multipartFile.getSize();
    final long ONE_M = 1024 * 1024L;
    ThrowUtils.throwIf(fileSize > 2 * ONE_M, ErrorCode.PARAMS_ERROR, "文件大小不能超过 2M");
    // 2. 校验文件后缀
    String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
    // 允许上传的文件后缀
    final List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpeg", "jpg", "png", "webp");
    ThrowUtils.throwIf(!ALLOW_FORMAT_LIST.contains(fileSuffix), ErrorCode.PARAMS_ERROR, "文件类型错误");
  }

  /** 删除临时文件 */
  public void deleteTempFile(File file) {
    if (file == null) {
      return;
    }
    boolean delete = file.delete();
    if (!delete) {
      log.error("file delete error, file path:{}", file.getAbsolutePath());
    }
  }
}
