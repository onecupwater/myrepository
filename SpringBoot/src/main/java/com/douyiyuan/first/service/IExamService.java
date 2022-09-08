package com.douyiyuan.first.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.douyiyuan.first.entity.Exam;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author baomidou
 * @since 2022-08-18
 */
public interface IExamService extends IService<Exam> {

    Page<Exam> findPage(Page<Exam> objectPage, String name, Integer courseId);
}
