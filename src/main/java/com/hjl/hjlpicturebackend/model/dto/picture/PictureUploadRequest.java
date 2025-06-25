package com.hjl.hjlpicturebackend.model.dto.picture;

import java.io.Serializable;
import lombok.Data;

/**
 * @author hao jinglian
 * @description 接收请求参数
 * @createDate 2025/6/19
 */
@Data
public class PictureUploadRequest implements Serializable {
  private static final long serialVersionUID = 1L;

  /** 图片 id （用于修改） */
  private Long id;
}
