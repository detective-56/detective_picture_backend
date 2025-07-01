package com.hjl.hjlpicturebackend.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hjl.hjlpicturebackend.annotaion.AuthCheck;
import com.hjl.hjlpicturebackend.common.BaseResponse;
import com.hjl.hjlpicturebackend.common.DeleteRequest;
import com.hjl.hjlpicturebackend.common.ResultUtils;
import com.hjl.hjlpicturebackend.constant.UserConstant;
import com.hjl.hjlpicturebackend.exception.BusinessException;
import com.hjl.hjlpicturebackend.exception.ErrorCode;
import com.hjl.hjlpicturebackend.exception.ThrowUtils;
import com.hjl.hjlpicturebackend.model.dto.picture.PictureEditRequest;
import com.hjl.hjlpicturebackend.model.dto.picture.PictureQueryRequest;
import com.hjl.hjlpicturebackend.model.dto.picture.PictureTagCategory;
import com.hjl.hjlpicturebackend.model.dto.picture.PictureUpdateRequest;
import com.hjl.hjlpicturebackend.model.dto.picture.PictureUploadRequest;
import com.hjl.hjlpicturebackend.model.entity.Picture;
import com.hjl.hjlpicturebackend.model.entity.User;
import com.hjl.hjlpicturebackend.model.vo.PictureVo;
import com.hjl.hjlpicturebackend.service.PictureService;
import com.hjl.hjlpicturebackend.service.UserService;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

  /** 分页获取图片列表 (仅管理员能用) */
  @PostMapping("/list/page")
  @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
  public BaseResponse<Page<Picture>> listPictureByPage(@RequestBody PictureQueryRequest request) {
    int current = request.getCurrent();
    int size = request.getPageSize();
    Page<Picture> picturePage =
        pictureService.page(new Page<>(current, size), pictureService.getQueryWrapper(request));
    return ResultUtils.success(picturePage);
  }

  /** 根据 id 获取图片 (仅管理员可用) */
  @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
  @GetMapping("/get")
  public BaseResponse<Picture> getPictureById(Long id, HttpServletRequest request) {
    ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
    // 查询数据库
    Picture picture = pictureService.getById(id);
    ThrowUtils.throwIf(picture == null, ErrorCode.PARAMS_ERROR);
    return ResultUtils.success(picture);
  }

  @PostMapping("/list/page/vo")
  public BaseResponse<Page<PictureVo>> listPictureByPageVo(
      @RequestBody PictureQueryRequest request, HttpServletRequest req) {

    int current = request.getCurrent();
    int pageSize = request.getPageSize();
    // 限制爬虫
    ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
    // 查询数据库
    Page<Picture> picturePage =
        pictureService.page(new Page<>(current, pageSize), pictureService.getQueryWrapper(request));
    // 获取封装类
    return ResultUtils.success(pictureService.getPictureVoPage(picturePage, req));
  }

  /** 根据 id 获取图片 */
  @GetMapping("/get/vo")
  public BaseResponse<PictureVo> getPictureVoById(Long id, HttpServletRequest request) {
    ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
    // 查询数据库
    Picture picture = pictureService.getById(id);
    ThrowUtils.throwIf(picture == null, ErrorCode.PARAMS_ERROR);
    // 获取封装类
    return ResultUtils.success(pictureService.getPictureVo(picture, request));
  }

  /** 更新图片 */
  @PostMapping("/update")
  @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
  public BaseResponse<Boolean> updatePicture(@RequestBody PictureUpdateRequest updateRequest) {
    if (Objects.isNull(updateRequest) || updateRequest.getId() <= 0) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }

    // 将实体类和 DTO 进行转换
    Picture picture = new Picture();
    BeanUtils.copyProperties(updateRequest, picture);
    // 将 list 转为 string
    picture.setTags(JSONUtil.toJsonStr(updateRequest.getTags()));
    // 数据校验
    pictureService.validPicture(picture);
    // 判断是否存在
    Long id = updateRequest.getId();
    Picture oldPicture = pictureService.getById(id);
    ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
    // 更新
    boolean result = pictureService.updateById(picture);
    ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    return ResultUtils.success(result);
  }

  /** 删除图片 */
  @PostMapping("/delete")
  public BaseResponse<Boolean> deletePicture(
      @RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {

    if (Objects.isNull(deleteRequest) || deleteRequest.getId() <= 0) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }
    User loginUser = userService.getLoginUser(request);
    Long id = deleteRequest.getId();

    Picture picture = pictureService.getById(id);
    ThrowUtils.throwIf(Objects.isNull(picture), ErrorCode.NOT_FOUND_ERROR, "图片不存在");

    // 仅本人或管理员可删除
    if (!picture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
      throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
    }

    boolean result = pictureService.removeById(id);
    ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    return ResultUtils.success(result);
  }

  /** 用户更新图片 */
  @PostMapping("/edit")
  public BaseResponse<Boolean> edit(
      @RequestBody PictureEditRequest editRequest, HttpServletRequest request) {
    if (editRequest == null || editRequest.getId() <= 0) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }

    // 查询库中是否存在
    Picture oldPicture = pictureService.getById(editRequest.getId());
    ThrowUtils.throwIf(oldPicture == null, ErrorCode.NO_AUTH_ERROR);
    // 校验用户
    User loginUser = userService.getLoginUser(request);
    if (!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
      throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
    }

    Picture picture = BeanUtil.toBean(editRequest, Picture.class);
    picture.setTags(JSONUtil.toJsonStr(picture.getTags()));
    picture.setEditTime(new Date());
    // 校验图片
    pictureService.validPicture(picture);

    boolean result = pictureService.updateById(picture);
    ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    return ResultUtils.success(result);
  }

  @GetMapping("/tag_category")
  public BaseResponse<PictureTagCategory> listPictureTagCategory() {
    PictureTagCategory pictureTagCategory = new PictureTagCategory();
    List<String> tagList = Arrays.asList("热门", "搞笑", "生活", "高清", "艺术", "校园", "背景", "简历", "创意");
    List<String> categoryList = Arrays.asList("模板", "电商", "表情包", "素材", "海报");
    pictureTagCategory.setTagList(tagList);
    pictureTagCategory.setCategoryList(categoryList);
    return ResultUtils.success(pictureTagCategory);
  }
}
