package com.douyiyuan.first.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.douyiyuan.first.entity.Cart;
import com.douyiyuan.first.entity.User;
import com.douyiyuan.first.mapper.CartMapper;
import com.douyiyuan.first.service.ICartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2022-08-07
 */
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements ICartService {
    @Resource
    CartMapper cartMapper;

    @Override
    public Page<Cart> findPage(Page<Cart> objectPage, String name, User user) {
        String role = user.getRole();
        Integer userId = user.getId();
        Page<Cart> page = cartMapper.findPage(objectPage,name,role,userId);
        return page;
    }
}
