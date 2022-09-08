package com.douyiyuan.first.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2022-08-19
 */
@Getter
@Setter
  @TableName("student_paper")
public class StudentPaper implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * id
     */
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      /**
     * 学生id
     */
      private Integer userId;

      /**
      * 考试id
      */
      private Integer examId;

      /**
     * 试卷内容
     */
      private String paperContent;

      /**
     * 总分
     */
      private Integer score;

      /**
     * 提交时间
     */
      @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
      private LocalDateTime time;


}
