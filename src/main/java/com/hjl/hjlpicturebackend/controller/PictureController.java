package com.hjl.hjlpicturebackend.controller;

import com.hjl.hjlpicturebackend.annotaion.AuthCheck;
import com.hjl.hjlpicturebackend.common.BaseResponse;
import com.hjl.hjlpicturebackend.common.ResultUtils;
import com.hjl.hjlpicturebackend.constant.UserConstant;
import com.hjl.hjlpicturebackend.model.dto.picture.PictureUploadRequest;
import com.hjl.hjlpicturebackend.model.entity.User;
import com.hjl.hjlpicturebackend.model.vo.PictureVo;
import com.hjl.hjlpicturebackend.service.PictureService;
import com.hjl.hjlpicturebackend.service.UserService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author hao jinglian
 * @description 图片控制器
 * @createDate 2025/6/25
 */
@RestController
@RequestMapping("picture")
public class PictureController {

  @Resource private UserService userService;

  @Resource private PictureService pictureService;

  @PostMapping("/upload")
  @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
  public BaseResponse<PictureVo> upload(
      @RequestPart("file") MultipartFile file,
      PictureUploadRequest pictureUploadRequest,
      HttpServletRequest request) {
    User loginUser = userService.getLoginUser(request);
    PictureVo pictureVo = pictureService.uploadPicture(file, pictureUploadRequest, loginUser);
    return ResultUtils.success(pictureVo);
  }
}
