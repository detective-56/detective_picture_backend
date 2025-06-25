package com.hjl.hjlpicturebackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjl.hjlpicturebackend.exception.ErrorCode;
import com.hjl.hjlpicturebackend.exception.ThrowUtils;
import com.hjl.hjlpicturebackend.manager.FileManager;
import com.hjl.hjlpicturebackend.mapper.PictureMapper;
import com.hjl.hjlpicturebackend.model.dto.file.UploadPictureResult;
import com.hjl.hjlpicturebackend.model.dto.picture.PictureQueryRequest;
import com.hjl.hjlpicturebackend.model.dto.picture.PictureUploadRequest;
import com.hjl.hjlpicturebackend.model.entity.Picture;
import com.hjl.hjlpicturebackend.model.entity.User;
import com.hjl.hjlpicturebackend.model.vo.PictureVo;
import com.hjl.hjlpicturebackend.model.vo.UserVo;
import com.hjl.hjlpicturebackend.service.PictureService;
import com.hjl.hjlpicturebackend.service.UserService;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author DELL
 * @description 针对表【picture(图片)】的数据库操作Service实现
 * @createDate 2025-06-19 14:13:42
 */
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService {

  @Resource private FileManager fileManager;

  @Resource private UserService userService;

  @Override
  public PictureVo uploadPicture(
      MultipartFile file, PictureUploadRequest pictureUploadRequest, User loginUser) {
    ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);
    Long pictureId = null;
    if (pictureUploadRequest != null) {
      pictureId = pictureUploadRequest.getId();
    }

    if (pictureId != null) {
      boolean exists = this.lambdaQuery().eq(Picture::getId, pictureId).exists();
      ThrowUtils.throwIf(exists, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
    }

    // 上传图片，得到信息
    // 按照用户id 划分目录
    String uploadPathPrefix = String.format("public/%s", loginUser.getId());
    UploadPictureResult uploadPictureResult = fileManager.uploadPicture(file, uploadPathPrefix);
    // 构造要入库的图片信息
    Picture picture = new Picture();
    BeanUtil.copyProperties(
        uploadPictureResult,
        picture,
        CopyOptions.create().setIgnoreNullValue(true).setFieldMapping(Map.of("picName", "name")));
    picture.setUserId(loginUser.getId());
    // 如果 pictureId 不为空表示更新，否则是新增
    if (pictureId != null) {
      picture.setId(pictureId);
      picture.setEditTime(new Date());
    }
    boolean result = this.saveOrUpdate(picture);
    ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "图片上传失败");

    return PictureVo.objToVo(picture);
  }

  @Override
  public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {
    String name = pictureQueryRequest.getName();
    String introduction = pictureQueryRequest.getIntroduction();
    String picFormat = pictureQueryRequest.getPicFormat();
    String searchText = pictureQueryRequest.getSearchText();
    List<String> tags = pictureQueryRequest.getTags();
    String sortField = pictureQueryRequest.getSortField();
    String sortOrder = pictureQueryRequest.getSortOrder();

    Picture picture =
        BeanUtil.toBean(
            pictureQueryRequest,
            Picture.class,
            CopyOptions.create()
                .setIgnoreNullValue(true)
                .setIgnoreProperties("name", "introduction", "picFormat", "searchText", "tags"));
    QueryWrapper<Picture> queryWrapper = new QueryWrapper<>(picture);
    queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
    queryWrapper.like(StringUtils.isNotBlank(introduction), "introduction", introduction);
    queryWrapper.like(StringUtils.isNotBlank(picFormat), "picFormat", picFormat);
    // 从多字段中搜索
    if (StrUtil.isNotBlank(searchText)) {
      // 需要拼接查询条件
      queryWrapper.and(qw -> qw.like("name", searchText).or().like("introduction", searchText));
    }
    if (CollUtil.isNotEmpty(tags)) {
      for (String tag : tags) {
        queryWrapper.like("tags", "\"" + tag + "\"");
      }
    }
    queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortField.equals("ascend"), sortOrder);

    return queryWrapper;
  }

  @Override
  public PictureVo getPictureVo(Picture picture, HttpServletRequest request) {
    // 对象封装到类
    PictureVo pictureVo = PictureVo.objToVo(picture);
    // 关联用户信息
    Long userId = picture.getUserId();
    if (userId != null) {
      User user = userService.getById(userId);
      UserVo userVo = userService.getUserVo(user);
      pictureVo.setUser(userVo);
    }
    return pictureVo;
  }

  @Override
  public Page<PictureVo> getPictureVoPage(Page<Picture> picturePage, HttpServletRequest request) {
    List<Picture> pictureList = picturePage.getRecords();
    Page<PictureVo> pictureVoPage =
        new Page<>(picturePage.getCurrent(), picturePage.getSize(), picturePage.getTotal());
    if (CollUtil.isEmpty(pictureList)) {
      return pictureVoPage;
    }

    // 对象列表 -》 封装列表
    List<PictureVo> pictureVoList =
        pictureList.stream().map(PictureVo::objToVo).collect(Collectors.toList());
    Set<Long> userIdSet = pictureList.stream().map(Picture::getUserId).collect(Collectors.toSet());
    Map<Long, List<User>> userIdUserListMap =
        userService.listByIds(userIdSet).stream()
            .collect(Collectors.groupingBy(User::getId, Collectors.toList()));
    pictureVoList.forEach(
        pictureVo -> {
          Long userId = pictureVo.getUserId();
          User user = null;
          if (userIdUserListMap.containsKey(userId)) {
            user = userIdUserListMap.get(userId).get(0);
          }
          pictureVo.setUser(userService.getUserVo(user));
        });

    pictureVoPage.setRecords(pictureVoList);
    return pictureVoPage;
  }

  @Override
  public void validPicture(Picture picture) {
    ThrowUtils.throwIf(picture == null, ErrorCode.PARAMS_ERROR);
    // 从对象中取出数据
    Long id = picture.getId();
    String url = picture.getUrl();
    String introduction = picture.getIntroduction();
    ThrowUtils.throwIf(ObjUtil.isNull(id), ErrorCode.PARAMS_ERROR, "id不能为空");
    if (StrUtil.isBlank(url)) {
      ThrowUtils.throwIf(url.length() > 1024, ErrorCode.PARAMS_ERROR, "url 过长");
    }
    if (StrUtil.isBlank(introduction)) {
      ThrowUtils.throwIf(introduction.length() > 800, ErrorCode.PARAMS_ERROR, "简介过长");
    }
  }
}
