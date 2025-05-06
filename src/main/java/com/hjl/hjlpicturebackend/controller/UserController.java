package com.hjl.hjlpicturebackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.hjl.hjlpicturebackend.annotaion.AuthCheck;
import com.hjl.hjlpicturebackend.common.BaseResponse;
import com.hjl.hjlpicturebackend.common.DeleteRequest;
import com.hjl.hjlpicturebackend.common.ResultUtils;
import com.hjl.hjlpicturebackend.constant.UserConstant;
import com.hjl.hjlpicturebackend.exception.ErrorCode;
import com.hjl.hjlpicturebackend.exception.ThrowUtils;
import com.hjl.hjlpicturebackend.model.dto.user.UserAddRequest;
import com.hjl.hjlpicturebackend.model.dto.user.UserLoginRequest;
import com.hjl.hjlpicturebackend.model.dto.user.UserQueryRequest;
import com.hjl.hjlpicturebackend.model.dto.user.UserRegisterRequest;
import com.hjl.hjlpicturebackend.model.dto.user.UserUpdateRequest;
import com.hjl.hjlpicturebackend.model.entity.User;
import com.hjl.hjlpicturebackend.model.vo.LoginUserVo;
import com.hjl.hjlpicturebackend.model.vo.UserVo;
import com.hjl.hjlpicturebackend.service.UserService;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

  @Resource private UserService userService;

  @ApiOperationSupport(order = 1)
  @ApiOperation("用户注册")
  @PostMapping("/register")
  public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
    ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
    String userAccount = userRegisterRequest.getUserAccount();
    String userPassword = userRegisterRequest.getUserPassword();
    String checkPassword = userRegisterRequest.getCheckPassword();
    long result = userService.userRegister(userAccount, userPassword, checkPassword);
    return ResultUtils.success(result);
  }

  @ApiOperationSupport(order = 2)
  @ApiOperation(value = "用户登录")
  @PostMapping("/login")
  public BaseResponse<LoginUserVo> userLogin(
      @RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
    ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
    String userAccount = userLoginRequest.getUserAccount();
    String userPassword = userLoginRequest.getUserPassword();
    LoginUserVo loginUserVo = userService.userLogin(userAccount, userPassword, request);
    return ResultUtils.success(loginUserVo);
  }

  @ApiOperationSupport(order = 3)
  @ApiOperation(value = "获取当前登录用户")
  @GetMapping("/get/login")
  public BaseResponse<LoginUserVo> getLoginUser(HttpServletRequest request) {
    User loginUser = userService.getLoginUser(request);
    return ResultUtils.success(userService.getLoginUserVo(loginUser));
  }

  @ApiOperationSupport(order = 4)
  @ApiOperation(value = "注销当前登录用户", notes = "注销")
  @PostMapping("/logout")
  public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
    return ResultUtils.success(userService.userLogout(request));
  }

  /** 用户的crud ========================================== */
  @ApiOperationSupport(order = 5)
  @ApiOperation(value = "创建用户")
  @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
  public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
    ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);
    User user = new User();
    BeanUtils.copyProperties(userAddRequest, user);
    // 默认密码 12345678
    final String DEFAULT_PASSWORD = "12345678";
    String encryptPassword = userService.getEncryptPassword(DEFAULT_PASSWORD);
    user.setUserPassword(encryptPassword);
    boolean result = userService.save(user);
    ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    return ResultUtils.success(user.getId());
  }

  @ApiOperationSupport(order = 6)
  @ApiOperation("根据 id 获取用户(仅管理员)")
  @GetMapping("/get")
  @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
  public BaseResponse<User> getUserById(@RequestParam("id") Long id) {
    ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
    User user = userService.getById(id);
    ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
    return ResultUtils.success(user);
  }

  @ApiOperationSupport(order = 7)
  @ApiOperation(value = "根据 id 获取包装类")
  @GetMapping("/get/vo")
  public BaseResponse<UserVo> getUserVoById(@RequestParam("id") Long id) {
    BaseResponse<User> response = getUserById(id);
    User user = response.getData();
    return ResultUtils.success(userService.getUserVo(user));
  }

  @ApiOperationSupport(order = 8)
  @ApiOperation(value = "删除用户")
  @PostMapping("/delete")
  @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
  public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
    ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
    boolean result = userService.removeById(deleteRequest.getId());
    return ResultUtils.success(result);
  }

  @ApiOperationSupport(order = 9)
  @ApiOperation(value = "更新用户")
  @PutMapping("/update")
  @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
  public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
    ThrowUtils.throwIf(
        userUpdateRequest == null || userUpdateRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
    User user = new User();
    BeanUtils.copyProperties(userUpdateRequest, user);
    boolean result = userService.updateById(user);
    ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    return ResultUtils.success(result);
  }

  @ApiOperationSupport(order = 10)
  @ApiOperation("分页获取用户封装的列表")
  @PostMapping("list/page/vo")
  @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
  public BaseResponse<Page<UserVo>> listUserVoByPage(
      @RequestBody UserQueryRequest userQueryRequest) {
    ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
    int current = userQueryRequest.getCurrent();
    int pageSize = userQueryRequest.getPageSize();
    Page<User> userPage =
        userService.page(
            new Page<>(current, pageSize), userService.getQueryWrapper(userQueryRequest));
    Page<UserVo> userVoPage = new Page<>(current, pageSize, userPage.getTotal());
    List<UserVo> userVoList = userService.getUserVoList(userPage.getRecords());
    userVoPage.setRecords(userVoList);
    return ResultUtils.success(userVoPage);
  }
}
