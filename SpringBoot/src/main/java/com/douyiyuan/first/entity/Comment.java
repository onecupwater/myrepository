package com.douyiyuan.first.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * 评论表
 * </p>
 *
 * @author baomidou
 * @since 2022-08-01
 */
@Getter
@Setter
@ToString
  public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * id,楼层（评论从2楼开始）
     */
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      /**
     * 内容
     */
      private String content;

      /**
     * 评论人id
     */
      private Integer userId;

      /**
     * 被回复人id
     */
      private Integer replyId;

      /**
     * 最顶级评论id
     */
      private Integer originId;

      /**
     * 被回复的评论id
     */
      private Integer pid;

      /**
     * 文章关联id
     */
      private Integer articleId;

      /**
      * 楼层，originid = 0的记录
      */
      private Integer floorNum;

      /**
     * 评论时间
     */
      @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
      private LocalDateTime commentTime;

      //下面是关联表的字段

      //第一次left连接user表里的nickname
      @TableField(exist = false)
      private String nickname;

      //第一次left连接user表里的avatar
      @TableField(exist = false)
      private String avatar;

      //第二次left连接user表里的nickname
      @TableField(exist = false)
      private String replyName;

      @TableField(exist = false)
      private List<Comment> children;

}
