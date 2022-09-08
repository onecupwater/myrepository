package com.douyiyuan.first.service.impl;

import com.douyiyuan.first.entity.Video;
import com.douyiyuan.first.mapper.VideoMapper;
import com.douyiyuan.first.service.IVideoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 视频信息表 服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2022-07-27
 */
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements IVideoService {

}
