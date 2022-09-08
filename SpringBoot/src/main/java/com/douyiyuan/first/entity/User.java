package com.douyiyuan.first.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * 用户信息表
 * </p>
 *
 * @author baomidou
 * @since 2022-07-10
 */
@Getter
@Setter
@ToString
@TableName(value = "user")
  public class User implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * id
     */
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      /**
     * 用户名
     */
      private String name;

      /**
      * 昵称
      */
      private String nickname;

      /**
      * 角色(与role表的外键，等于role表的flag字段)
      */
      private String role;

      /**
      * 角色名称（role表的name字段，user表没有这个字段）
      */
      @TableField(exist = false)
      private String roleName;

      /**
      * 电话
      */
      private String phone;

      /**
     * 密码
     */
      @JsonIgnore
      private String password;

      /**
      * 头像
      */
      private String avatar;

      /**
     * 邮箱
     */
      private String email;

      /**
     * 个性签名
     */
      private String sign;

      /**
     * 地址
     */
      private String address;

      /**
     * 用户新建时间
     */
      @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
      private LocalDateTime createDate;

      /**
     * 最近修改信息时间
     */
      @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
      private LocalDateTime updateDate;

      /**
      * 专业id
      */
      private Integer majorId;

      /**
      * 班级id
      */
      private Integer stuClassId;



}
