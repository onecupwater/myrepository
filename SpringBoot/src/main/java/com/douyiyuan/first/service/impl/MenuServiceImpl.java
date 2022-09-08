package com.douyiyuan.first.service.impl;

import com.douyiyuan.first.entity.Menu;
import com.douyiyuan.first.mapper.MenuMapper;
import com.douyiyuan.first.service.IMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 菜单管理 服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2022-07-17
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

}
