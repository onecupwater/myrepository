package com.douyiyuan.first.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.douyiyuan.first.entity.Exam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2022-08-18
 */
public interface ExamMapper extends BaseMapper<Exam> {

    Page<Exam> findPage(Page<Exam> objectPage,
                        @Param("name") String name,
                        @Param("courseId") Integer courseId);
}
