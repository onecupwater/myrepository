package com.douyiyuan.first.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.douyiyuan.first.common.Result;
import com.douyiyuan.first.entity.Goods;
import com.douyiyuan.first.entity.User;
import com.douyiyuan.first.mapper.CartMapper;
import com.douyiyuan.first.service.IGoodsService;
import com.douyiyuan.first.util.TokenUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

import com.douyiyuan.first.service.ICartService;
import com.douyiyuan.first.entity.Cart;

import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2022-08-07
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Resource
    private ICartService cartService;

    @Resource
    private CartMapper cartMapper;

    @Resource
    private IGoodsService goodsService;

    /**
     * 查询所有
     * @return
     */
    @GetMapping
    public Result findAll() {
        return Result.success(cartService.list());
    }

    /**
     * 根据ids查询
     * @param ids
     * @return
     */
    @PostMapping("findListByIds")
    public Result findListByIds(@RequestBody List<Integer> ids) {
        List<Cart> carts = cartMapper.findListByIds(ids);
        return Result.success(carts);
    }

    /**
     * 分页查询
     * @param name 根据商品名模糊查询
     * @param user 根据当前登录user获取该user的所有购物车信息 ，管理员除外，管理员是获取所有人的所有购物车信息
     * @return
     */
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam String name) {
        User user = TokenUtil.getUser();
        Page<Cart> objectPage = new Page<>(pageNum, pageSize);
        Page<Cart> page = cartService.findPage(objectPage,name,user);
        return Result.success(page);
    }

    /**
     * 新增
     * @param cart
     * @return
     */
    @PostMapping("save")
    public Result save(@RequestBody Cart cart) {
        Integer userId = cart.getUserId();
        Integer goodsId = cart.getGoodsId();
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("goods_id",goodsId);
        Cart dbCart = cartService.getOne(queryWrapper);
        //如果用户购物车里已经有想要添加的商品，就改成给购物车的对应的商品数量加上想要购买的数量，
        //并修改该商品小计
        if(dbCart!=null){
            Integer num = dbCart.getNum() + cart.getNum();
            //根据goodsId获取商品单价
            Goods goods = goodsService.getById(goodsId);
            //相乘
            BigDecimal subtotal = new BigDecimal(num).multiply(goods.getPrice());
            //修改重数据库查出来的dbCart
            dbCart.setNum(num);
            dbCart.setSubtotal(subtotal);
            return Result.success(cartService.updateById(dbCart));
        }
        //没查到dbCart，就新增购物车信息
        return Result.success(cartService.save(cart));
        }

    /**
     * 修改
     * @param cart
     * @return
     */
    @PostMapping("updateById")
    public Result updateById(@RequestBody Cart cart) {
        return Result.success(cartService.updateById(cart));
    }

    /**
     * 根据id删除
     * @param id
     * @return
     */
    @DeleteMapping("delete/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(cartService.removeById(id));
        }

    /**
     * 批量删除，因为前端的axios传 "[1,2,3]" 这样的参数只能用post方式来传，不能用delete方式来传
     * @param ids
     * @return
     */
    @PostMapping("removeBatchByIds")
    public Result removeBatchByIds(@RequestBody List<Integer> ids){
        return Result.success(cartService.removeBatchByIds(ids));
        }
}

