package com.hjl.hjlpicturebackend.model.dto.picture;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * @author hao jinglian
 * @description 图片更新请求
 * @createDate 2025/6/25
 */
@Data
public class PictureUpdateRequest implements Serializable {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty("id")
  private Long id;

  @ApiModelProperty("图片名称")
  private String name;

  @ApiModelProperty("简介")
  private String introduction;

  @ApiModelProperty("分类")
  private String category;

  @ApiModelProperty("标签")
  private List<String> tags;
}
