package com.douyiyuan.first.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author baomidou
 * @since 2022-08-10
 */
@Getter
@Setter
  public class Question implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * id
     */
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      /**
     * 课程类型id
     */
      private Integer courseId;

      /**
     * 题目类型（1，选择题；2，判断题；3，填空题）
     */
      private Integer questionType;

      /**
     * 题目
     */
      private String name;

      /**
     * 选项A
     */
      private String a;

      /**
     * 选项B
     */
      private String b;

      /**
     * 选项C
     */
      private String c;

      /**
     * 选项D
     */
      private String d;

      /**
     * 答案
     */
      private String answer;

      /**
     * 解析
     */
      private String analysis;

      /**
      * 分数
      */
      private int score;

}
