package com.hjl.hjlpicturebackend.manager;

import com.hjl.hjlpicturebackend.config.CosClientConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import java.io.File;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @author hao jinglian
 * @description
 * @createDate 2025/6/19
 */
@Component
public class CosManager {

  @Resource private CosClientConfig cosClientConfig;

  @Resource private COSClient cosClient;

  // 一些操作 COS 的方法

  /**
   * 上传对象
   *
   * @param key 唯一键
   * @param file 文件
   */
  public PutObjectResult putObject(String key, File file) {
    PutObjectRequest putObjectRequest =
        new PutObjectRequest(cosClientConfig.getBucket(), key, file);
    return cosClient.putObject(putObjectRequest);
  }

  /**
   * 下载对象
   *
   * @param key 唯一键
   */
  public COSObject getObject(String key) {
    GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
    return cosClient.getObject(getObjectRequest);
  }

  public PutObjectResult putPictureObject(String key, File file) {
    PutObjectRequest putObjectRequest =
        new PutObjectRequest(cosClientConfig.getBucket(), key, file);
    // 对图片进行处理（获取基本信息也被视为一种处理）
    PicOperations picOperations = new PicOperations();
    // 1 表示返回图像信息
    picOperations.setIsPicInfo(1);
    // 构造处理参数
    putObjectRequest.setPicOperations(picOperations);
    return cosClient.putObject(putObjectRequest);
  }
}
