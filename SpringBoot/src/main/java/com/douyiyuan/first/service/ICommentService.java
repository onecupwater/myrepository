package com.douyiyuan.first.service;

import com.douyiyuan.first.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 评论表 服务类
 * </p>
 *
 * @author baomidou
 * @since 2022-08-01
 */
public interface ICommentService extends IService<Comment> {

    List<Comment> findCommentTreeListByArticleId(Integer articleId);
}
