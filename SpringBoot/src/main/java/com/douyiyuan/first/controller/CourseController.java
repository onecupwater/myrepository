package com.douyiyuan.first.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.douyiyuan.first.common.ReleaseAnnotation;
import com.douyiyuan.first.common.Result;
import com.douyiyuan.first.config.YourRedisConfig;
import com.douyiyuan.first.entity.Files;
import com.douyiyuan.first.mapper.FileMapper;
import com.douyiyuan.first.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

import com.douyiyuan.first.service.ICourseService;
import com.douyiyuan.first.entity.Course;

import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 课程表 前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2022-07-23
 */
@RestController
@RequestMapping("/course")
public class CourseController {

    @Resource
    private ICourseService courseService;

    @Resource
    private FileMapper fileMapper;

    /**
     * 查询所有
     * @return
     */
    @ReleaseAnnotation
    @GetMapping
    public Result findAll() {
        return Result.success(courseService.list());
    }

    /**
     * 先删后加
     * 根据teacherId往TeacherCourse表插入teacherId，courseId
     * @return
     */
    @PostMapping("saveTeacherIdCourseId/{teacherId}/{courseId}")
    public Result saveTeacherIdCourseId(@PathVariable("teacherId") Integer teacherId,
                       @PathVariable("courseId")Integer courseId) {
        courseService.saveTeacherIdCourseId(teacherId,courseId);
        return Result.success("任课成功,请到 '我的任课' 查看情况",null);
    }

    /**
     * 先删后加
     * 根据studentId往TeacherCourse表插入studentId，courseId
     * @return
     */
    @PostMapping("saveStudentIdCourseId/{studentId}/{courseId}")
    public Result saveStudentIdCourseId(@PathVariable("studentId") Integer studentId,
                                        @PathVariable("courseId")Integer courseId) {
        courseService.saveStudentIdCourseId(studentId,courseId);
        return Result.success("选课成功,请到 '我的选课' 查看情况",null);
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(courseService.getById(id));
    }

    /**
     * 分页查询
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name) {
        //分页查询如果没有条件，就把new QueryWrapper<>()删了，有条件就在queryWrapper变量加条件
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        //queryWrapper加条件
        if(StrUtil.isNotBlank(name)){
            queryWrapper.like("name",name);
        }
        queryWrapper.orderByDesc("id");
        Page<Course> objectPage = new Page<>(pageNum, pageSize);
        Page<Course> page = courseService.page(objectPage, queryWrapper);
        return Result.success(page);
    }

    /**
     * 新增与修改
     * @param course
     * @return
     */
    @PostMapping("save")
    public Result save(@RequestBody Course course) {
        return Result.success(courseService.saveOrUpdate(course));
        }

    /**
     * 根据id删除
     * @param id
     * @return
     */
    @DeleteMapping("delete/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(courseService.removeById(id));
        }

    /**
     * 批量删除，因为前端的axios传 "[1,2,3]" 这样的参数只能用post方式来传，不能用delete方式来传
     * @param ids
     * @return
     */
    @PostMapping("removeBatchByIds")
    public Result removeBatchByIds(@RequestBody List<Integer> ids){ return Result.success(courseService.removeBatchByIds(ids)); }

    @DeleteMapping("deleteCourseIdAndTeacherId/{teacherId}/{courseId}")
    public Result deleteCourseIdAndTeacherId(
                         @PathVariable("teacherId") Integer teacherId,
                         @PathVariable("courseId") Integer courseId) {
        courseService.deleteCourseIdAndTeacherId(teacherId,courseId);
        return Result.success("取消任课成功",null);
    }

    @DeleteMapping("deleteCourseIdAndStudentId/{studentId}/{courseId}")
    public Result deleteCourseIdAndStudentId(
                         @PathVariable("studentId") Integer studentId,
                         @PathVariable("courseId") Integer courseId) {
        courseService.deleteCourseIdAndStudentId(studentId,courseId);
        return Result.success("取消选课成功",null);
    }


    @ReleaseAnnotation
    @GetMapping("getAllFiles")
//    @Cacheable(key = "'getAllFiles'", cacheNames = "files")
    public Result getAllFiles(){
        List<Files> files = fileMapper.selectList(null);
        return Result.success(files);
    }
}

