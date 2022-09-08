package com.douyiyuan.first.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
@ToString
  public class Sign implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * id
     */
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      /**
     * 考试id
     */
      private Integer examId;

      /**
     * 学生id
     */
      private Integer userId;

      /**
     * 审核状态
     */
      private String state;


}
