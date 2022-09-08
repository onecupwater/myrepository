package com.douyiyuan.first.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.douyiyuan.first.common.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

import com.douyiyuan.first.service.ISignService;
import com.douyiyuan.first.entity.Sign;

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
@RequestMapping("/sign")
public class SignController {

    @Resource
    private ISignService signService;

    /**
     * 查询所有
     * @return
     */
    @GetMapping
    public Result findAll() {
        return Result.success(signService.list());
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(signService.getById(id));
    }

    @PostMapping("findSign")
    public Result findSign(@RequestBody Sign sign) {
        QueryWrapper<Sign> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("exam_id",sign.getExamId());
        queryWrapper.eq("user_id",sign.getUserId());
        List<Sign> signs = signService.getBaseMapper().selectList(queryWrapper);
        return Result.success(signs);
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
        QueryWrapper<Sign> queryWrapper = new QueryWrapper<>();
        //queryWrapper加条件

        queryWrapper.orderByDesc("id");
        Page<Sign> objectPage = new Page<>(pageNum, pageSize);
        Page<Sign> page = signService.page(objectPage, queryWrapper);
        return Result.success(page);
    }

    /**
     * 新增与修改
     * @param sign
     * @return
     */
    @PostMapping("save")
    public Result save(@RequestBody Sign sign) {
        return Result.success(signService.saveOrUpdate(sign));
        }

    /**
     * 根据id删除
     * @param id
     * @return
     */
    @DeleteMapping("delete/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(signService.removeById(id));
        }

    /**
     * 批量删除，因为前端的axios传 "[1,2,3]" 这样的参数只能用post方式来传，不能用delete方式来传
     * @param ids
     * @return
     */
    @PostMapping("removeBatchByIds")
    public Result removeBatchByIds(@RequestBody List<Integer> ids){
        return Result.success(signService.removeBatchByIds(ids));
        }
}

