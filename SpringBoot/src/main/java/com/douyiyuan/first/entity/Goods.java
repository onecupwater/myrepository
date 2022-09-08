package com.douyiyuan.first.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.math.BigDecimal;

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
 * @since 2022-08-06
 */
@Getter
@Setter
@ToString
  public class Goods implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * id
     */
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      /**
     * 商品名称
     */
      private String name;

      /**
     * 商品编号
     */
      private String code;

      /**
     * 价格
     */
      //这个注解已经把数字类型转成string类型，前端计算时要转化
      @JsonFormat(shape = JsonFormat.Shape.STRING)
      private BigDecimal price;

      /**
     * 库存
     */
      private Integer store;

      /**
     * 毛重
     */
      private BigDecimal grossWeight;

      /**
     * 产地
     */
      private String place;

      /**
      * 图片
      */
      private String picture;

      @TableField(exist = false)
      private Integer num;
      @TableField(exist = false)
      private BigDecimal subtotal;
}
