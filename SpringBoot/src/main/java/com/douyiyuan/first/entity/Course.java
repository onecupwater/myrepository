package com.douyiyuan.first.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 课程表
 * </p>
 *
 * @author baomidou
 * @since 2022-07-23
 */
@Getter
@Setter
  public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * id
     */
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      /**
     * 课程名称
     */
      private String name;

      /**
     * 学分
     */
      private Integer credit;

      /**
     * 总课时
     */
      private Integer lessonHour;

      /**
     * 是否开课(0 代表false   1代表true)
     */
      private Boolean state;

      /**
      * 是否必修
      */
      private String isRequired;


}
