package com.douyiyuan.first.service.impl;

import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.douyiyuan.first.common.StatusCode;
import com.douyiyuan.first.common.exception.ServiceException;
import com.douyiyuan.first.entity.Course;
import com.douyiyuan.first.entity.StudentCourse;
import com.douyiyuan.first.entity.TeacherCourse;
import com.douyiyuan.first.mapper.CourseMapper;
import com.douyiyuan.first.mapper.StudentCourseMapper;
import com.douyiyuan.first.mapper.TeacherCourseMapper;
import com.douyiyuan.first.service.ICourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 课程表 服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2022-07-23
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {

    private static final Log LOGGER = Log.get();


    @Resource
    private TeacherCourseMapper teacherCourseMapper;

    @Resource
    private StudentCourseMapper studentCourseMapper;

    @Transactional
    @Override
    public void saveTeacherIdCourseId(Integer teacherId, Integer courseId) {
        try{
            //先删
            QueryWrapper<TeacherCourse> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("teacher_id",teacherId);
            queryWrapper.eq("course_id",courseId);
            teacherCourseMapper.delete(queryWrapper);

            //后加
            TeacherCourse teacherCourse = new TeacherCourse();
            teacherCourse.setTeacherId(teacherId);
            teacherCourse.setCourseId(courseId);
            teacherCourseMapper.insert(teacherCourse);
        }catch (Exception e){
            LOGGER.error(e);
            throw new ServiceException(StatusCode.CODE_500,"系统错误,老师无法任课");
        }
    }

    @Transactional
    @Override
    public void saveStudentIdCourseId(Integer studentId, Integer courseId) {
        try{
            //先删
            QueryWrapper<StudentCourse> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("student_id",studentId);
            queryWrapper.eq("course_id",courseId);
            studentCourseMapper.delete(queryWrapper);

            //后加
            StudentCourse studentCourse = new StudentCourse();
            studentCourse.setStudentId(studentId);
            studentCourse.setCourseId(courseId);
            studentCourseMapper.insert(studentCourse);
        }catch (Exception e){
            LOGGER.error(e);
            throw new ServiceException(StatusCode.CODE_500,"系统错误,学生无法选课");
        }
    }

    @Override
    public void deleteCourseIdAndTeacherId(Integer teacherId, Integer courseId) {
        try {
            QueryWrapper<TeacherCourse> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("teacher_id",teacherId);
            queryWrapper.eq("course_id",courseId);
            teacherCourseMapper.delete(queryWrapper);
        }catch (Exception e){
            LOGGER.error(e);
            throw new ServiceException(StatusCode.CODE_500,"系统错误，无法取消任课");
        }
    }

    @Override
    public void deleteCourseIdAndStudentId(Integer studentId, Integer courseId) {
        try {
            QueryWrapper<StudentCourse> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("student_id",studentId);
            queryWrapper.eq("course_id",courseId);
            studentCourseMapper.delete(queryWrapper);
        }catch (Exception e){
            LOGGER.error(e);
            throw new ServiceException(StatusCode.CODE_500,"系统错误，无法取消选课");
        }
    }
}
