package com.douyiyuan.first.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author baomidou
 * @since 2022-08-22
 */
@Getter
@Setter
  @TableName("stu_class")
public class StuClass implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * id
     */
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      /**
     * 班级名称
     */
      private String name;

      /**
     * 专业id
     */
      private Integer majorId;

      /**
     * 学生人数
     */
      private Integer stuNum;

}
