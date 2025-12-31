package com.hjl.hjlpicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hjl.hjlpicturebackend.model.dto.picture.PictureQueryRequest;
import com.hjl.hjlpicturebackend.model.dto.picture.PictureUploadRequest;
import com.hjl.hjlpicturebackend.model.entity.Picture;
import com.hjl.hjlpicturebackend.model.entity.User;
import com.hjl.hjlpicturebackend.model.vo.PictureVo;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author DELL
 * @description 针对表【picture(图片)】的数据库操作Service
 * @createDate 2025-06-19 14:13:42
 */
public interface PictureService extends IService<Picture> {

  /** 上传图像 */
  PictureVo uploadPicture(
      MultipartFile file, PictureUploadRequest pictureUploadRequest, User loginUser);

  /** 查询请求转换为 QueryWrapper对象 */
  QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

  /** 获取单个图片的封装 */
  PictureVo getPictureVo(Picture picture, HttpServletRequest request);

  /** 分页获取图片 */
  Page<PictureVo> getPictureVoPage(Page<Picture> picturePage, HttpServletRequest request);

  /** 编写图片校验数据方法 */
  void validPicture(Picture picture);
}
