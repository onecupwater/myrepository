package com.douyiyuan.first.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.douyiyuan.first.entity.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 文章信息表 Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2022-07-30
 */
public interface ArticleMapper extends BaseMapper<Article> {

    Page<Article> findPageByName(Page<Article> objectPage,String name);

    Page<Article> findPage(Page<Article> objectPage);
}
