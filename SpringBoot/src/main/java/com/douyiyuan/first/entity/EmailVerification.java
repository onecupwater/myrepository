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
 * @since 2022-08-25
 */
@Getter
@Setter
  @TableName("email_verification")
public class EmailVerification implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * id
     */
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      /**
     * 邮箱
     */
      private String email;

      /**
     * 验证码
     */
      private String code;

      /**
     * 验证类型，1表示验证邮箱登录，2表示验证找回密码
     */
      private Integer type;

      /**
     * 验证码过期时间
     */
      @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
      private LocalDateTime expireTime;


}
