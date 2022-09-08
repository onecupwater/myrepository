package com.douyiyuan.first.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class OrdersGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 订单id
     */
    private Integer ordersId;

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
     * 下面的字段不是OrdersGoods表的
     */
    @TableField(exist = false)
    private String code;

    @TableField(exist = false)
    private String goodsName;

    @TableField(exist = false)
    private String picture;

    @TableField(exist = false)
    private BigDecimal price;

    @TableField(exist = false)
    private Integer store;

    @TableField(exist = false)
    private BigDecimal grossWeight;

    @TableField(exist = false)
    private String place;
}
