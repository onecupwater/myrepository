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
 * @since 2022-08-11
 */
@Getter
@Setter
  public class Paper implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * id
     */
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      /**
     * 课程id
     */
      private Integer courseId;

      /**
     * 试卷名称
     */
      private String name;

      /**
     * 考试时长（分）
     */
      private Integer duration;

      /**
     * 总分
     */
      private Integer score;


}
