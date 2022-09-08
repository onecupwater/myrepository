package com.douyiyuan.first.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * 菜单管理
 * </p>
 *
 * @author baomidou
 * @since 2022-07-17
 */
@Getter
@Setter
@ToString
  public class Menu implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * id
     */
      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      /**
     * 菜单名称
     */
      private String name;

      /**
     * 图标
     */
      private String icon;

      /**
     * 路径
     */
      private String path;

      /**
      * 页面路径
      */
      private String pagePath;

      /**
     * 描述
     */
      private String description;

      /**
     * 父级id
     */
      private Integer pid;

      /**
     * 对菜单的排列顺序
     */
      private Integer sort;

      /**
      * 子级目录
      */
      @TableField(exist = false)  //不是数据库的字段
      private List<Menu> children = new ArrayList<>();

}
