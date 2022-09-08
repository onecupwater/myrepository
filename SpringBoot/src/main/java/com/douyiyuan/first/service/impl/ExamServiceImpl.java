package com.douyiyuan.first.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.douyiyuan.first.entity.Exam;
import com.douyiyuan.first.mapper.ExamMapper;
import com.douyiyuan.first.service.IExamService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2022-08-18
 */
@Service
public class ExamServiceImpl extends ServiceImpl<ExamMapper, Exam> implements IExamService {

    @Resource
    private ExamMapper examMapper;

    @Override
    public Page<Exam> findPage(Page<Exam> objectPage, String name, Integer courseId) {
        return examMapper.findPage(objectPage,name,courseId);
    }
}
