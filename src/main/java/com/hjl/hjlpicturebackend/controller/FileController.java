package com.hjl.hjlpicturebackend.controller;

import com.hjl.hjlpicturebackend.annotaion.AuthCheck;
import com.hjl.hjlpicturebackend.common.BaseResponse;
import com.hjl.hjlpicturebackend.common.ResultUtils;
import com.hjl.hjlpicturebackend.constant.UserConstant;
import com.hjl.hjlpicturebackend.exception.BusinessException;
import com.hjl.hjlpicturebackend.exception.ErrorCode;
import com.hjl.hjlpicturebackend.manager.CosManager;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import java.io.File;
import java.io.IOException;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author hao jinglian
 * @description 文件控制器
 * @createDate 2025/6/19
 */
@Slf4j
@RestController("/file")
public class FileController {

  @Resource private CosManager cosManager;

  @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
  @PostMapping("/test/upload")
  public BaseResponse<String> testUploadFile(@RequestParam("file") MultipartFile multipartFile) {

    // 文件目录
    String filename = multipartFile.getOriginalFilename();
    String filePath = String.format("/test/%s", filename);
    File file = null;
    try {
      // 上传文件
      file = File.createTempFile(filePath, null);
      multipartFile.transferTo(file);
      cosManager.putObject(filePath, file);
      return ResultUtils.success(filePath);
    } catch (Exception e) {
      log.error("file uplaod error, filepath = " + filePath, e);
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
    } finally {
      if (file != null) {
        boolean delete = file.delete();
        if (!delete) {
          log.error("file delete error, filepath = {}", filePath);
        }
      }
    }
  }

  @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
  @GetMapping("/test/download")
  public void testDownloadFile(String filePath, HttpServletResponse response) throws IOException {
    COSObjectInputStream cosObjectInputStream = null;
    try {
      COSObject cosObject = cosManager.getObject(filePath);
      cosObjectInputStream = cosObject.getObjectContent();
      byte[] fileBytes = IOUtils.toByteArray(cosObjectInputStream);
      response.setContentType("application/octet-stream;charset=utf-8");
      response.setHeader("Content-Disposition", "attachment; filename=" + filePath);
      // 写入流
      response.getOutputStream().write(fileBytes);
      response.getOutputStream().flush();

    } catch (Exception e) {
      log.error("download file error, filePath = " + filePath, e);
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
    } finally {
      if (cosObjectInputStream != null) {
        cosObjectInputStream.close();
      }
    }
  }
}
