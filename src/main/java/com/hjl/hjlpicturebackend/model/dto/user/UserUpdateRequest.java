package com.hjl.hjlpicturebackend.model.dto.user;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Data;

@Data
public class UserUpdateRequest implements Serializable {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "id")
  private Long id;

  /** 用户昵称 */
  @ApiModelProperty(value = "用户昵称")
  private String userName;

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
