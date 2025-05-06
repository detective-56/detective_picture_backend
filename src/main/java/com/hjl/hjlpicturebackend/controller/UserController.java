package com.hjl.hjlpicturebackend.controller;

import com.hjl.hjlpicturebackend.common.BaseResponse;
import com.hjl.hjlpicturebackend.common.ResultUtils;
import com.hjl.hjlpicturebackend.exception.ErrorCode;
import com.hjl.hjlpicturebackend.exception.ThrowUtils;
import com.hjl.hjlpicturebackend.model.dto.user.UserLoginRequest;
import com.hjl.hjlpicturebackend.model.dto.user.UserRegisterRequest;
import com.hjl.hjlpicturebackend.model.entity.User;
import com.hjl.hjlpicturebackend.model.vo.LoginUserVo;
import com.hjl.hjlpicturebackend.service.UserService;
import io.swagger.annotations.ApiOperation;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

  @Resource private UserService userService;

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

  @ApiOperation(value = "获取当前登录用户")
  @GetMapping("/get/login")
  public BaseResponse<LoginUserVo> getLoginUser(HttpServletRequest request) {
    User loginUser = userService.getLoginUser(request);
    return ResultUtils.success(userService.getLoginUserVo(loginUser));
  }

  @ApiOperation(value = "注销当前登录用户", notes = "注销")
  @PostMapping("/logout")
  public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
    return ResultUtils.success(userService.userLogout(request));
  }
}
