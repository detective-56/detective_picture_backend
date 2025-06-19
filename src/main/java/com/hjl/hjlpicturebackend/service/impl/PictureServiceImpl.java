package com.hjl.hjlpicturebackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjl.hjlpicturebackend.mapper.PictureMapper;
import com.hjl.hjlpicturebackend.model.entity.Picture;
import com.hjl.hjlpicturebackend.service.PictureService;
import org.springframework.stereotype.Service;

/**
 * @author DELL
 * @description 针对表【picture(图片)】的数据库操作Service实现
 * @createDate 2025-06-19 14:13:42
 */
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService {}
