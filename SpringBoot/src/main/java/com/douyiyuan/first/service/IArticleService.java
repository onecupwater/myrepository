package com.douyiyuan.first.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.douyiyuan.first.entity.Article;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 文章信息表 服务类
 * </p>
 *
 * @author baomidou
 * @since 2022-07-30
 */
public interface IArticleService extends IService<Article> {

    Page<Article> findPage(Page<Article> objectPage, String name);
}
