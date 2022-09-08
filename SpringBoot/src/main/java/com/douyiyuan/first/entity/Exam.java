package com.douyiyuan.first.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * 
 * </p>
 *
 * @author baomidou
 * @since 2022-08-18
 */
@Getter
@Setter
@ToString
  public class Exam implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * id
     */
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      /**
     * 考试名称
     */
      private String name;

      /**
     * 课程id
     */
      private Integer courseId;

      /**
     * 监考老师id
     */
      private String teacher;

      /**
     * 考试开始时间
     */
      @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
      private LocalDateTime startTime;

      /**
      * 考试状态
      */
      private String state;

      /**
      * 试卷id
      */
      private Integer paperId;

      /**
      * 试卷名称
      */
      @TableField(exist = false)
      private String paperName;

      /**
      * 试卷名称
      */
      @TableField(exist = false)
      private Boolean enable;

}
