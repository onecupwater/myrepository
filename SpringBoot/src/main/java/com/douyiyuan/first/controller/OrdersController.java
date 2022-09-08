package com.douyiyuan.first.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.douyiyuan.first.common.Result;
import com.douyiyuan.first.common.StatusCode;
import com.douyiyuan.first.common.exception.ServiceException;
import com.douyiyuan.first.entity.*;
import com.douyiyuan.first.mapper.CartMapper;
import com.douyiyuan.first.mapper.OrdersGoodsMapper;
import com.douyiyuan.first.mapper.OrdersMapper;
import com.douyiyuan.first.service.ICartService;
import com.douyiyuan.first.service.IGoodsService;
import com.douyiyuan.first.util.TokenUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.douyiyuan.first.service.IOrdersService;

import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2022-08-09
 */
@RestController
@RequestMapping("/orders")
public class OrdersController {

    @Resource
    private IOrdersService ordersService;

    @Resource
    private CartMapper cartMapper;

    @Resource
    private OrdersGoodsMapper ordersGoodsMapper;

    @Resource
    private IGoodsService goodsService;

    /**
     * 查询所有
     * @return
     */
    @GetMapping
    public Result findAll() {
        return Result.success(ordersService.list());
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(ordersService.getById(id));
    }

    /**
     * 根据ordersId查询orders_goods表，从orders_goods表关联goods表获取商品信息
     * @param orderId
     * @return
     */
    @GetMapping("findOrdersAndGoodsByOrderId/{orderId}")
    public Result findOrdersAndGoodsByOrderId(@PathVariable Integer orderId) {
        Orders orders = ordersService.getById(orderId);
        List<OrdersGoods> ordersGoodsList =  ordersService.findGoodsByOrderId(orderId);
        HashMap<String, Object> map = new HashMap<>();
        map.put("orders",orders);
        map.put("ordersGoods",ordersGoodsList);
        return Result.success(map);
    }

    /**
     * 根据ordersId查询orders_goods表，从orders_goods表关联goods表获取商品信息
     * @param orderId
     * @return
     */
    @GetMapping("findGoodsByOrderId/{orderId}")
    public Result findGoodsByOrderId(@PathVariable Integer orderId) {
        List<OrdersGoods> OrdersGoodsList =  ordersService.findGoodsByOrderId(orderId);
        return Result.success(OrdersGoodsList);
    }

    /**
     * 分页查询
     * @param pageNum
     * @param pageSize
     * @param no          订单号
     * @return
     */
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam String no) {
        Page<Orders> objectPage = new Page<>(pageNum, pageSize);
        User user = TokenUtil.getUser();
        String role = user.getRole();
        Integer userId = user.getId();
        Page<Orders> page = ordersService.findPage(objectPage, no,role,userId);
        return Result.success(page);
    }

    /**
     * 通过购物车新增订单
     * @param ids
     * @return
     */
    @PostMapping("saveByCart")
    public Result saveByCart(@RequestBody List<Integer> ids) {
        if(ObjectUtil.isEmpty(ids)){
            throw  new ServiceException(StatusCode.CODE_300,"没有选择购物车里的商品");
        }
        //获取被选中的购物车商品信息
        List<Cart> carts = cartMapper.findListByIds(ids);
        if(ObjectUtil.isEmpty(carts)){
            throw  new ServiceException(StatusCode.CODE_300,"没有找到购物车里的商品");
        }
        //造一个order实体，并填充数据
        Orders orders = new Orders();

        String orderName = "";
        Integer userId = 0;
        BigDecimal amount = new BigDecimal(0);

        HashMap<Integer, Integer> reduceStoreMap = new HashMap<>(); //省一个循环，用于减库存（K,V）->(商品id，购买数量)
        HashSet<Integer> cartIds = new HashSet<>();  //省一个循环,用于后面生成订单后，删除选中的购物车商品记录

        for (Cart cart: carts) {
            //拼接订单名称
            String substring = cart.getGoodsName().substring(0, 9);
            substring = substring + "...,";
            orderName = orderName + substring;
            //获取用户id
            userId = cart.getUserId();
            //获取小计总和
            amount = amount.add(cart.getSubtotal());

            //下面两步是为了扣库存和删购物车做准备
            reduceStoreMap.put(cart.getGoodsId(),cart.getNum());
            cartIds.add(cart.getId());
        }

        //生成订单号（年月日+用户id）
        LocalDateTime date = LocalDateTime.now();
        String no = DateTimeFormatter
                .ofPattern("yyyyMMddHHmmss").format(date)+ userId;

        orders.setName(orderName);
        orders.setNo(no);
        orders.setUserId(userId);
        orders.setAmount(amount);
        orders.setOrderTime(date);

        //把orders保存到orders表
         ordersService.save(orders);

//-------------------------------------------------------------------------------------------

        //保存后获取刚刚保存的orders数据，通过 userId,订单号 去查
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("no",no);
        queryWrapper.eq("user_id",userId);
        Orders dbOrders = ordersService.getOne(queryWrapper);
        if(ObjectUtil.isEmpty(dbOrders)){
            throw  new ServiceException(StatusCode.CODE_300,"通过购物车产生的订单未生成");
        }

        //将选中的购物车商品信息和订单id关联起来，保存到orders_goods表
        Integer ordersId = dbOrders.getId();
        //先删，根据ordersId获取orders_goods表中的数据，删除，删除后再关联商品
        QueryWrapper<OrdersGoods> wrapper = new QueryWrapper<>();
        wrapper.eq("orders_id",ordersId);
        ordersGoodsMapper.delete(wrapper);
        //后增
        for (Cart cart: carts) {
            OrdersGoods ordersGoods = new OrdersGoods();
            ordersGoods.setOrdersId(ordersId);
            ordersGoods.setGoodsId(cart.getGoodsId());
            ordersGoods.setNum(cart.getNum());
            ordersGoods.setSubtotal(cart.getSubtotal());
            ordersGoodsMapper.insert(ordersGoods);
        }
//--------------------------------------------------------------------------------------
        //扣减库存
        Set<Map.Entry<Integer, Integer>> entries = reduceStoreMap.entrySet();
        Iterator<Map.Entry<Integer, Integer>> iterator = entries.iterator();
        while (iterator.hasNext()){
            Map.Entry<Integer, Integer> entry = iterator.next();
            Goods goods = goodsService.getById(entry.getKey());
            Integer store = goods.getStore();
            store = store - entry.getValue();
            goods.setStore(store);
            goodsService.updateById(goods);
        }

//--------------------------------------------------------------------------------------
        //删除对应的购物车记录
        cartMapper.deleteBatchIds(cartIds);

        //最后返回 ordersId，以便前端拿到 ordersId直接跳转订单详情页
        return Result.success("提交订单成功",ordersId);
    }

    /**
     * 通过直接新增订单
     * @param goods
     * @return
     */
    @PostMapping("saveByPurchase")
    public Result saveByPurchase(@RequestBody Goods goods) {
        //造一个order实体，并填充数据
        Integer userId = TokenUtil.getUser().getId();
        Orders orders = new Orders();

        //生成订单号（年月日+用户id）
        LocalDateTime date = LocalDateTime.now();
        String no = DateTimeFormatter
                .ofPattern("yyyyMMddHHmmss").format(date)+ userId;

        orders.setName(goods.getName().substring(0,9)+"...");
        orders.setNo(no);
        orders.setUserId(userId);
        orders.setAmount(goods.getSubtotal());
        orders.setOrderTime(date);
        ordersService.save(orders);

//-------------------------------------------------------------------------------------------

        //保存后获取刚刚保存的orders数据，通过 userId,订单号 去查
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("no",no);
        queryWrapper.eq("user_id",userId);
        Orders dbOrders = ordersService.getOne(queryWrapper);
        if(ObjectUtil.isEmpty(dbOrders)){
            throw  new ServiceException(StatusCode.CODE_300,"通过直接购买产生的订单未生成");
        }

        //将选中的商品信息和订单id关联起来，保存到orders_goods表
        Integer ordersId = dbOrders.getId();
        //先删，根据ordersId获取orders_goods表中的数据，删除，删除后再关联商品
        QueryWrapper<OrdersGoods> wrapper = new QueryWrapper<>();
        wrapper.eq("orders_id",ordersId);
        ordersGoodsMapper.delete(wrapper);
        //后增
        OrdersGoods ordersGoods = new OrdersGoods();
        ordersGoods.setOrdersId(ordersId);
        ordersGoods.setGoodsId(goods.getId());
        ordersGoods.setNum(goods.getNum());
        ordersGoods.setSubtotal(goods.getSubtotal());
        ordersGoodsMapper.insert(ordersGoods);
//--------------------------------------------------------------------------------------
        //扣减库存
        Goods dbGoods = goodsService.getById(goods.getId());
        dbGoods.setStore(dbGoods.getStore()-goods.getNum());
        goodsService.updateById(dbGoods);
//--------------------------------------------------------------------------------------
        //最后返回 ordersId，以便前端拿到 ordersId直接跳转订单详情页
        return Result.success("提交订单成功",ordersId);
    }


    /**
     * 根据id删除，先删orders_goods表的关联数据，再删orders表
     * @param id
     * @return
     */
    @DeleteMapping("delete/{id}")
    public Result delete(@PathVariable Integer id) {
        QueryWrapper<OrdersGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("orders_id",id);
        List<OrdersGoods> ordersGoodsList = ordersGoodsMapper.selectList(queryWrapper);
        List<Integer> ordersGoodsIdList = ordersGoodsList.stream()
                .map(ordersGoods -> ordersGoods.getId()).collect(Collectors.toList());
        ordersGoodsMapper.deleteBatchIds(ordersGoodsIdList);
        ordersService.removeById(id);
        return Result.success(null);
        }

    /**
     * 批量删除，因为前端的axios传 "[1,2,3]" 这样的参数只能用post方式来传，不能用delete方式来传
     * 根据ids删除，先删orders_goods表的关联数据，再删orders表
     * @param ids
     * @return
     */
    @PostMapping("removeBatchByIds")
    public Result removeBatchByIds(@RequestBody List<Integer> ids){
        ArrayList<Integer> ordersGoodsIds = new ArrayList<>();
        for(Integer id : ids){
            QueryWrapper<OrdersGoods> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("orders_id",id);
            List<OrdersGoods> ordersGoodsList = ordersGoodsMapper.selectList(queryWrapper);
            for (OrdersGoods ordersGoods : ordersGoodsList){
                ordersGoodsIds.add(ordersGoods.getId());
            }
        }
        ordersGoodsMapper.deleteBatchIds(ordersGoodsIds);
        ordersService.removeBatchByIds(ids);
        return Result.success(null);
    }
}

