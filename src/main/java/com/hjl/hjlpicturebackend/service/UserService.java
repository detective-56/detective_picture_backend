package com.hjl.hjlpicturebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hjl.hjlpicturebackend.model.entity.User;
import com.hjl.hjlpicturebackend.model.vo.LoginUserVo;
import javax.servlet.http.HttpServletRequest;

/**
 * @author DELL
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2025-05-06 10:32:42
 */
public interface UserService extends IService<User> {

  /** 用户注册 */
  long userRegister(String userAccount, String userPassword, String checkPassword);

  // 加密密码
  String getEncryptPassword(String password);

  /** 用户登录 */
  LoginUserVo userLogin(String userAccount, String userPassword, HttpServletRequest request);

  /** 获取已登录用户信息 */
  User getLoginUser(HttpServletRequest request);

  /** 获取脱敏的已登录用户信息 */
  LoginUserVo getLoginUserVo(User user);

  /** 用户注销 */
  Boolean userLogout(HttpServletRequest request);
}
