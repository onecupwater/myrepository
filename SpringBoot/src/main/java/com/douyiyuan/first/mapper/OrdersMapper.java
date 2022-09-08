package com.douyiyuan.first.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.douyiyuan.first.entity.Orders;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.douyiyuan.first.entity.OrdersGoods;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2022-08-09
 */
public interface OrdersMapper extends BaseMapper<Orders> {

    Page<Orders> findPage(Page<Orders> objectPage,
                          @Param("no") String no,
                          @Param("role") String role,
                          @Param("userId") Integer userId);

    List<OrdersGoods> findGoodsByOrderId(@Param("orderId") Integer orderId);
}
