package com.hjl.hjlpicturebackend.model.dto.picture;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * @author hao jinglian
 * @description 图片修改请求(一般给普通用户用)
 * @createDate 2025/6/25
 */
@Data
public class PictureEditRequest implements Serializable {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty("id")
  private int id;

  @ApiModelProperty("图片名称")
  private String name;

  @ApiModelProperty("简介")
  private String introduction;

  @ApiModelProperty("分类")
  private String category;

  @ApiModelProperty("标签")
  private List<String> tags;
}
