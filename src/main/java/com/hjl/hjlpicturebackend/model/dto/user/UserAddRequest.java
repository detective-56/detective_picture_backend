package com.hjl.hjlpicturebackend.model.dto.user;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Data;

@Data
public class UserAddRequest implements Serializable {

  private static final long serialVersionUID = 1L;

  /** 用户昵称 */
  @ApiModelProperty(value = "用户昵称")
  private String userName;

  /** 账号 */
  @ApiModelProperty(value = "账号")
  private String userAccount;

  /** 用户头像 */
  @ApiModelProperty(value = "用户头像")
  private String userAvatar;

  /** 用户简介 */
  @ApiModelProperty(value = "用户简介")
  private String userProfile;

  /** 用户角色: user, admin */
  @ApiModelProperty(value = "用户角色")
  private String userRole;
}
