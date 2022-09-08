package com.douyiyuan.first.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.douyiyuan.first.entity.Orders;
import com.douyiyuan.first.entity.OrdersGoods;
import com.douyiyuan.first.mapper.OrdersMapper;
import com.douyiyuan.first.service.IOrdersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2022-08-09
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements IOrdersService {

    @Resource
    private OrdersMapper ordersMapper;

    @Override
    public Page<Orders> findPage(Page<Orders> objectPage, String no,String role,Integer userId) {

        return ordersMapper.findPage(objectPage,no,role,userId);
    }

    @Override
    public List<OrdersGoods> findGoodsByOrderId(Integer orderId) {
        return ordersMapper.findGoodsByOrderId(orderId);
    }
}
