package com.douyiyuan.first.service.impl;

import com.douyiyuan.first.entity.Comment;
import com.douyiyuan.first.mapper.CommentMapper;
import com.douyiyuan.first.service.ICommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 评论表 服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2022-08-01
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

    @Resource
    private CommentMapper commentMapper;

    @Override
    public List<Comment> findCommentTreeListByArticleId(Integer articleId) {
        List<Comment> comments = commentMapper.findCommentTreeListByArticleId(articleId);
        return comments;
    }
}
