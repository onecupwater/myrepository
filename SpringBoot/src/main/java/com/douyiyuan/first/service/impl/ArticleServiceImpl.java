package com.douyiyuan.first.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.douyiyuan.first.entity.Article;
import com.douyiyuan.first.mapper.ArticleMapper;
import com.douyiyuan.first.service.IArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyiyuan.first.util.TokenUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * <p>
 * 文章信息表 服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2022-07-30
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {

    @Resource
    private ArticleMapper articleMapper;

    @Override
    public Page<Article> findPage(Page<Article> objectPage, String name) {
        if(StrUtil.isNotBlank(name)){
            return articleMapper.findPageByName(objectPage,name);
        }
        return articleMapper.findPage(objectPage);
    }
}
