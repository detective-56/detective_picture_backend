package com.hjl.hjlpicturebackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjl.hjlpicturebackend.constant.UserConstant;
import com.hjl.hjlpicturebackend.exception.BusinessException;
import com.hjl.hjlpicturebackend.exception.ErrorCode;
import com.hjl.hjlpicturebackend.exception.ThrowUtils;
import com.hjl.hjlpicturebackend.mapper.UserMapper;
import com.hjl.hjlpicturebackend.model.dto.user.UserQueryRequest;
import com.hjl.hjlpicturebackend.model.entity.User;
import com.hjl.hjlpicturebackend.model.enums.UserRoleEnum;
import com.hjl.hjlpicturebackend.model.vo.LoginUserVo;
import com.hjl.hjlpicturebackend.model.vo.UserVo;
import com.hjl.hjlpicturebackend.service.UserService;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * @author DELL
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2025-05-06 10:32:42
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService, UserConstant {
  @Override
  public long userRegister(String userAccount, String userPassword, String checkPassword) {

    // 1. 校验
    if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
    }
    if (userAccount.length() < 4) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
    }
    if (userPassword.length() < 8 || checkPassword.length() < 8) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
    }
    if (!userPassword.equals(checkPassword)) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "再次输入的密码不一致");
    }

    // 检查是否有重复
    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("userAccount", userAccount);
    Long count = this.baseMapper.selectCount(queryWrapper);
    if (count > 0) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
    }
    // 3. 加密
    String encryptPassword = getEncryptPassword(userPassword);
    // 4. 插入数据
    User user = new User();
    user.setUserAccount(userAccount);
    user.setUserPassword(encryptPassword);
    user.setUserName("无名");
    user.setUserRole(UserRoleEnum.USER.getValue());
    boolean saveResult = this.save(user);
    if (!saveResult) {
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
    }
    return user.getId();
  }

  @Override
  public String getEncryptPassword(String userPassword) {
    // 盐值， 混淆密码
    final String SALT = "hjl";
    return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
  }

  @Override
  public LoginUserVo userLogin(
      String userAccount, String userPassword, HttpServletRequest request) {

    if (StrUtil.hasBlank(userAccount, userPassword)) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
    }
    if (userAccount.length() < 4) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
    }

    if (userPassword.length() < 4) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
    }
    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("userAccount", userAccount);
    queryWrapper.eq("userPassword", getEncryptPassword(userPassword));
    User user = this.baseMapper.selectOne(queryWrapper);

    if (user == null) {
      log.info("user login failed, userAccount cannot match userPassword");
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
    }

    // 3.记录用户的登录状态
    request.getSession().setAttribute(USER_LOGIN_STATE, user);

    return this.getLoginUserVo(user);
  }

  @Override
  public LoginUserVo getLoginUserVo(User user) {
    if (ObjUtil.isNull(user)) {
      return null;
    }

    LoginUserVo loginUserVo = new LoginUserVo();
    BeanUtils.copyProperties(user, loginUserVo);
    return loginUserVo;
  }

  @Override
  public List<LoginUserVo> getLoginUserVoList(List<User> userList) {
    if (CollUtil.isEmpty(userList)) {
      return new ArrayList<>();
    }
    return BeanUtil.copyToList(userList, LoginUserVo.class);
  }

  @Override
  public Boolean userLogout(HttpServletRequest request) {
    Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
    User currentUser = (User) userObj;
    if (currentUser == null) {
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "用户未登录");
    }
    request.getSession().removeAttribute(USER_LOGIN_STATE);
    return Boolean.TRUE;
  }

  @Override
  public User getLoginUser(HttpServletRequest request) {

    Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
    User currentUser = (User) userObj;
    if (currentUser == null) {
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "当前没有登录用户");
    }

    return currentUser;
  }

  @Override
  public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq(ObjUtil.isNotNull(userQueryRequest.getId()), "id", userQueryRequest.getId());
    queryWrapper.eq(
        StrUtil.isNotBlank(userQueryRequest.getUserRole()),
        "userRole",
        userQueryRequest.getUserRole());
    queryWrapper.eq(
        StrUtil.isNotBlank(userQueryRequest.getUserAccount()),
        "userAccount",
        userQueryRequest.getUserAccount());
    queryWrapper.eq(
        StrUtil.isNotBlank(userQueryRequest.getUserName()),
        "userName",
        userQueryRequest.getUserName());
    queryWrapper.eq(
        StrUtil.isNotBlank(userQueryRequest.getUserProfile()),
        "userProfile",
        userQueryRequest.getUserProfile());
    queryWrapper.orderBy(
        StrUtil.isNotBlank(userQueryRequest.getSortOrder()),
        "ascend".equals(userQueryRequest.getSortOrder()),
        userQueryRequest.getSortField());
    return null;
  }

  @Override
  public UserVo getUserVo(User user) {
    ThrowUtils.throwIf(ObjUtil.isNull(user), ErrorCode.PARAMS_ERROR, "用户为空");
    UserVo userVo = new UserVo();
    BeanUtils.copyProperties(user, userVo);
    return userVo;
  }

  @Override
  public List<UserVo> getUserVoList(List<User> userList) {
    return BeanUtil.copyToList(userList, UserVo.class);
  }
}
