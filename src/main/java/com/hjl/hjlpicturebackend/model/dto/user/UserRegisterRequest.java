package com.hjl.hjlpicturebackend.model.dto.user;

import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import lombok.Data;

@Data
@ApiModel(value = "用户注册请求Dto", description = "用户注册请求Dto")
public class UserRegisterRequest implements Serializable {
  private static final long serialVersionUID = 1L;

  /** 账号 */
  private String userAccount;

  /** 密码 */
  private String userPassword;

  /** 确认密码 */
  private String checkPassword;
}
