package com.douyiyuan.first.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * 
 * </p>
 *
 * @author baomidou
 * @since 2022-08-07
 */
@Getter
@Setter
@ToString
  public class Cart implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * id
     */
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      /**
     * 用户id
     */
      private Integer userId;

      /**
     * 商品id
     */
      private Integer goodsId;

      /**
     * 商品数量
     */
      private Integer num;

      /**
     * 小计
     */
      private BigDecimal subtotal;

      /*
      * 下面的字段不是cart表的
      */
      @TableField(exist = false)
      private String username;

      @TableField(exist = false)
      private String nickname;

      @TableField(exist = false)
      private String code;

      @TableField(exist = false)
      private String goodsName;

      @TableField(exist = false)
      private String picture;

      @TableField(exist = false)
      private BigDecimal price;
}
