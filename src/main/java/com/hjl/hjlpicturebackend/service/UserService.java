package com.hjl.hjlpicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hjl.hjlpicturebackend.model.dto.user.UserQueryRequest;
import com.hjl.hjlpicturebackend.model.entity.User;
import com.hjl.hjlpicturebackend.model.vo.LoginUserVo;
import com.hjl.hjlpicturebackend.model.vo.UserVo;
import java.util.List;
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

  /** 用户注销 */
  Boolean userLogout(HttpServletRequest request);

  /** 获取已登录用户信息 */
  User getLoginUser(HttpServletRequest request);

  /** 获取脱敏的已登录用户信息 */
  LoginUserVo getLoginUserVo(User user);

  /** 获取脱敏后的登录用户信息列表 */
  List<LoginUserVo> getLoginUserVoList(List<User> userList);

  /** 获取查询请求 queryWrapper 对象 */
  QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

  /** 获取用户脱敏后的信息 */
  UserVo getUserVo(User user);

  List<UserVo> getUserVoList(List<User> userList);
}
