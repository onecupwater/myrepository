package com.douyiyuan.first.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.douyiyuan.first.entity.Notice;
import com.douyiyuan.first.mapper.NoticeMapper;
import com.douyiyuan.first.service.INoticeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2022-08-05
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements INoticeService {

    @Resource
    private NoticeMapper noticeMapper;

    @Override
    public Page<Notice> findNoticePage(Page<Notice> objectPage, String name) {
        Page<Notice> noticePage = noticeMapper.findNoticePage(objectPage,name);
        return noticePage;
    }
}
