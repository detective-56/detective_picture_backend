package com.hjl.hjlpicturebackend.model.dto.user;

import com.hjl.hjlpicturebackend.common.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "id")
  private Long id;

  /** 用户昵称 */
  @ApiModelProperty(value = "用户昵称")
  private String userName;

  /** 用户头像 */
  @ApiModelProperty(value = "用户账号")
  private String userAccount;

  /** 用户简介 */
  @ApiModelProperty(value = "用户简介")
  private String userProfile;

  /** 用户角色: user, admin */
  @ApiModelProperty(value = "用户角色")
  private String userRole;
}
