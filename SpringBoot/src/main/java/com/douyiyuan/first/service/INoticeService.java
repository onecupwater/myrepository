package com.douyiyuan.first.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.douyiyuan.first.entity.Notice;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author baomidou
 * @since 2022-08-05
 */
public interface INoticeService extends IService<Notice> {

    Page<Notice> findNoticePage(Page<Notice> objectPage, String name);
}
