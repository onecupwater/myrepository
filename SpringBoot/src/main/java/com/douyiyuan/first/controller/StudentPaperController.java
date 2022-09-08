package com.douyiyuan.first.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.douyiyuan.first.common.Result;
import com.douyiyuan.first.common.StatusCode;
import com.douyiyuan.first.common.exception.ServiceException;
import com.douyiyuan.first.util.TokenUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import com.douyiyuan.first.service.IStudentPaperService;
import com.douyiyuan.first.entity.StudentPaper;

import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2022-08-19
 */
@RestController
@RequestMapping("/studentPaper")
public class StudentPaperController {

    @Resource
    private IStudentPaperService studentPaperService;

    /**
     * 查询所有
     * @return
     */
    @GetMapping
    public Result findAll() {
        return Result.success(studentPaperService.list());
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(studentPaperService.getById(id));
    }

    /**
     * 分页查询
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                                    @RequestParam Integer pageSize) {
        //分页查询如果没有条件，就把new QueryWrapper<>()删了，有条件就在queryWrapper变量加条件
        QueryWrapper<StudentPaper> queryWrapper = new QueryWrapper<>();
        //queryWrapper加条件

        queryWrapper.orderByDesc("id");
        Page<StudentPaper> objectPage = new Page<>(pageNum, pageSize);
        Page<StudentPaper> page = studentPaperService.page(objectPage, queryWrapper);
        return Result.success(page);
    }

    /**
     * 新增与修改
     * @param studentPaper
     * @return
     */
    @PostMapping("save")
    public Result save(@RequestBody StudentPaper studentPaper) {
        Integer userId = TokenUtil.getUser().getId();
        //避免重复提交
        QueryWrapper<StudentPaper> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("exam_id",studentPaper.getExamId());
        List<StudentPaper> studentPapers = studentPaperService.getBaseMapper().selectList(queryWrapper);
        if(CollUtil.isNotEmpty(studentPapers)){
            throw new ServiceException(StatusCode.CODE_300,"您已提交试卷");
        }
        studentPaper.setUserId(userId);
        studentPaper.setTime(LocalDateTime.now());
        return Result.success(studentPaperService.saveOrUpdate(studentPaper));
        }

    /**
     * 根据id删除
     * @param id
     * @return
     */
    @DeleteMapping("delete/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(studentPaperService.removeById(id));
        }

    /**
     * 批量删除，因为前端的axios传 "[1,2,3]" 这样的参数只能用post方式来传，不能用delete方式来传
     * @param ids
     * @return
     */
    @PostMapping("removeBatchByIds")
    public Result removeBatchByIds(@RequestBody List<Integer> ids){
        return Result.success(studentPaperService.removeBatchByIds(ids));
        }
}

