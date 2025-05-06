package com.hjl.hjlpicturebackend.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
@ApiModel(value = "用户Vo对象", description = "用户Vo对象")
public class LoginUserVo implements Serializable {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "用户ID")
  private Long id;

  @ApiModelProperty(value = "账号")
  private String userAccount;

  @ApiModelProperty(value = "用户昵称")
  private String userName;

  @ApiModelProperty(value = "用户头像")
  private String userAvatar;

  @ApiModelProperty(value = "用户简介")
  private String userProfile;

  @ApiModelProperty(value = "用户角色: user/admin")
  private String userRole;

  @ApiModelProperty(value = "创建时间")
  private Date createTime;

  @ApiModelProperty(value = "更新时间")
  private Date updateTime;
}
