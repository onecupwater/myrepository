package com.douyiyuan.first.service;

import com.douyiyuan.first.entity.Course;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 课程表 服务类
 * </p>
 *
 * @author baomidou
 * @since 2022-07-23
 */
public interface ICourseService extends IService<Course> {

    void saveTeacherIdCourseId(Integer teacherId, Integer courseId);

    void saveStudentIdCourseId(Integer studentId, Integer courseId);

    void deleteCourseIdAndTeacherId(Integer teacherId, Integer courseId);

    void deleteCourseIdAndStudentId(Integer studentId, Integer courseId);
}
