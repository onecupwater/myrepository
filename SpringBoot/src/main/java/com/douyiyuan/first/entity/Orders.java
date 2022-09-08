package com.douyiyuan.first.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * 
 * </p>
 *
 * @author baomidou
 * @since 2022-08-09
 */
@Getter
@Setter
@ToString
  public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * id
     */
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      /**
     * 名称
     */
      private String name;

      /**
     * 订单号
     */
      private String no;

      /**
     * 用户id
     */
      private Integer userId;

      /**
     * 支付金额
     */
      private BigDecimal amount;

      /**
     * 支付状态
     */
      private String state;

      /**
     * 下单时间
     */
      @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
      private LocalDateTime orderTime;

      /**
     * 支付时间
     */
      @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
      private LocalDateTime payTime;

    //下面的字段不是orders表的
      @TableField(exist = false)
      private String username;

      @TableField(exist = false)
      private String nickname;
}
