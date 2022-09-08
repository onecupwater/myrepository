package com.douyiyuan.first.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.douyiyuan.first.entity.Cart;
import com.baomidou.mybatisplus.extension.service.IService;
import com.douyiyuan.first.entity.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author baomidou
 * @since 2022-08-07
 */
public interface ICartService extends IService<Cart> {

    Page<Cart> findPage(Page<Cart> objectPage, String name, User user);
}
