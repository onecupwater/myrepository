package com.douyiyuan.first.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * 
 * </p>
 *
 * @author baomidou
 * @since 2022-07-17
 */
@Getter
@Setter
@ToString
  public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * id
     */
      @TableId(value = "id", type = IdType.AUTO)
        private Integer id;

      /**
     * 角色名称
     */
      private String name;

      /**
     * 描述
     */
      private String description;

     /**
     * 唯一标识
     */
      private String flag;


}
