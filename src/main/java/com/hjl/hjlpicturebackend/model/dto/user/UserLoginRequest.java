package com.hjl.hjlpicturebackend.model.dto.user;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Data;

@Data
// @ApiModel(value = "用户登录请求Dto", description = "用户登录请求Dto")
public class UserLoginRequest implements Serializable {
  private static final long serialVersionUID = 1L;

  /** 账号 */
  @ApiModelProperty(value = "账号")
  private String userAccount;

  /** 密码 */
  @ApiModelProperty(value = "密码")
  private String userPassword;
}
