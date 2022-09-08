package com.douyiyuan.first.mapper;

import com.douyiyuan.first.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 评论表 Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2022-08-01
 */
public interface CommentMapper extends BaseMapper<Comment> {

    @Select("SELECT c.*, u1.nickname,u1.avatar,u2.nickname as replyName\n" +
            "FROM comment c\n" +
            "left join user u1\n" +
            "on c.user_id = u1.id\n" +
            "LEFT JOIN user u2\n" +
            "on c.reply_id = u2.id\n" +
            "where c.article_id = #{articleId} ORDER BY id desc")
    List<Comment> findCommentTreeListByArticleId(@Param("articleId") Integer articleId);

    @Select("SELECT c.*, u1.nickname,u1.avatar,u2.nickname as replyName\n" +
            "FROM comment c\n" +
            "LEFT JOIN user u1\n" +
            "on c.user_id = u1.id\n" +
            "LEFT JOIN user u2\n" +
            "on c.reply_id = u2.id\n" +
            "where c.id = #{id}")
    Comment findCommentById(@Param("id") Integer id);

    @Select("SELECT c.*, u1.nickname,u1.avatar,u2.nickname as replyName\n" +
            "FROM comment c\n" +
            "LEFT JOIN user u1\n" +
            "on c.user_id = u1.id\n" +
            "LEFT JOIN user u2\n" +
            "on c.reply_id = u2.id\n" +
            "where c.origin_id = #{originId}\n" +
            "ORDER BY id desc")
    List<Comment> findCommentListByOriginId(@Param("originId") Integer originId);
}
