package com.douyiyuan.first.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author baomidou
 * @since 2022-08-05
 */
@Getter
@Setter
  public class Notice implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * id
     */
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      /**
     * 标题
     */
      private String name;

      /**
     * 内容
     */
      private String content;

      /**
     * 公告发布人id
     */
      private Integer userId;

      /**
     * 发布时间
     */
      @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
      private LocalDateTime time;

      /**
      * user表的nickname
      */
      @TableField(exist = false)
      private String nickname;


}
