package com.douyiyuan.first.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.douyiyuan.first.common.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

import com.douyiyuan.first.service.IQuestionService;
import com.douyiyuan.first.entity.Question;

import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2022-08-10
 */
@RestController
@RequestMapping("/question")
public class QuestionController {

    @Resource
    private IQuestionService questionService;

    /**
     * 查询所有
     * @return
     */
    @GetMapping
    public Result findAll() {
        return Result.success(questionService.list());
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(questionService.getById(id));
    }

    /**
     * 根据CourseId查询
     * @param courseId
     * @return
     */
    @GetMapping("/findQuestionsByCourseId/{courseId}")
    public Result findQuestionsByCourseId(@PathVariable("courseId") Integer courseId) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id",courseId);
        List<Question> questions = questionService.list(queryWrapper);
        return Result.success(questions);
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
                           @RequestParam String name) {
        //分页查询如果没有条件，就把new QueryWrapper<>()删了，有条件就在queryWrapper变量加条件
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        //queryWrapper加条件
        if(StrUtil.isNotBlank(name)){
            queryWrapper.like("name",name);
        }
        queryWrapper.orderByDesc("id");
        Page<Question> objectPage = new Page<>(pageNum, pageSize);
        Page<Question> page = questionService.page(objectPage, queryWrapper);
        return Result.success(page);
    }

    /**
     * 新增与修改
     * @param question
     * @return
     */
    @PostMapping("save")
    public Result save(@RequestBody Question question) {
        return Result.success(questionService.saveOrUpdate(question));
        }

    /**
     * 根据id删除
     * @param id
     * @return
     */
    @DeleteMapping("delete/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(questionService.removeById(id));
        }

    /**
     * 批量删除，因为前端的axios传 "[1,2,3]" 这样的参数只能用post方式来传，不能用delete方式来传
     * @param ids
     * @return
     */
    @PostMapping("removeBatchByIds")
    public Result removeBatchByIds(@RequestBody List<Integer> ids){
        return Result.success(questionService.removeBatchByIds(ids));
        }
}

