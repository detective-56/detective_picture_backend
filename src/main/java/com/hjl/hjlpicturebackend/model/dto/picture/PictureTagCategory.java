package com.hjl.hjlpicturebackend.model.dto.picture;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * @author hao jinglian
 * @description 标签分类
 * @createDate 2025/7/1
 */
@Data
public class PictureTagCategory implements Serializable {
  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "标签列表")
  private List<String> tagList;

  @ApiModelProperty(value = "种类列表")
  private List<String> categoryList;
}
