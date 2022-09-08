package com.douyiyuan.first.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.douyiyuan.first.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;
import com.douyiyuan.first.entity.OrdersGoods;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author baomidou
 * @since 2022-08-09
 */
public interface IOrdersService extends IService<Orders> {

    Page<Orders> findPage(Page<Orders> objectPage, String no,String role,Integer userId);

    List<OrdersGoods> findGoodsByOrderId(Integer orderId);
}
