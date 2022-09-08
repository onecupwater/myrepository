package com.douyiyuan.first.controller;


import cn.hutool.core.util.ObjectUtil;
import com.douyiyuan.first.common.ReleaseAnnotation;
import com.douyiyuan.first.common.Result;
import com.douyiyuan.first.common.StatusCode;
import com.douyiyuan.first.common.exception.ServiceException;
import com.douyiyuan.first.entity.User;
import com.douyiyuan.first.service.IUserService;
import com.douyiyuan.first.util.TokenUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import com.douyiyuan.first.service.IArticleService;
import com.douyiyuan.first.entity.Article;

import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 文章信息表 前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2022-07-30
 */
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Resource
    private IArticleService articleService;

    @Resource
    private IUserService userService;

    /**
     * 查询所有
     * @return
     */
    @GetMapping
    public Result findAll() {
        return Result.success(articleService.list());
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @ReleaseAnnotation
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        Article article = articleService.getById(id);
        if(ObjectUtil.isEmpty(article)){
            throw new ServiceException(StatusCode.CODE_300,"没有找到文章");
        }
        if(article.getUserId()!=null){
            User user = userService.getById(article.getUserId());
            article.setPublisher(user.getNickname());
        }
        return Result.success(article);
    }

    /**
     * 分页查询
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ReleaseAnnotation
    @GetMapping("/page")
    public Page<Article> findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name) {
        //这里的前端展示页面除了article表的数据，多了一个user表的name数据
        //使用了联合查询，1对1
        Page<Article> objectPage = new Page<>(pageNum, pageSize);
        Page<Article> page = articleService.findPage(objectPage, name);
        return page;
    }

    /**
     * 新增与修改
     * @param article
     * @return
     */
    @PostMapping("save")
    public Result save(@RequestBody Article article) {
        article.setUserId(TokenUtil.getUser().getId());
        article.setPublishTime(LocalDateTime.now());
        return Result.success(articleService.saveOrUpdate(article));
        }

    /**
     * 根据id删除
     * @param id
     * @return
     */
    @DeleteMapping("delete/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(articleService.removeById(id));
        }

    /**
     * 批量删除，因为前端的axios传 "[1,2,3]" 这样的参数只能用post方式来传，不能用delete方式来传
     * @param ids
     * @return
     */
    @PostMapping("removeBatchByIds")
    public Result removeBatchByIds(@RequestBody List<Integer> ids){
        return Result.success(articleService.removeBatchByIds(ids));
        }
}

