package com.hjl.hjlpicturebackend.model.vo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
// @ApiModel(value = "用户Vo对象", description = "用户Vo对象")
public class UserVo implements Serializable {

  /** id */
  private Long id;

  /** 账号 */
  private String userAccount;

  /** 用户昵称 */
  private String userName;

  /** 用户头像 */
  private String userAvatar;

  /** 用户简介 */
  private String userProfile;

  /** 用户角色：user/admin */
  private String userRole;

  /** 创建时间 */
  private Date createTime;

  private static final long serialVersionUID = 1L;
}
