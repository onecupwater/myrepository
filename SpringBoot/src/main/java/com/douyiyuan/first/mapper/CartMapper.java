package com.douyiyuan.first.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.douyiyuan.first.entity.Cart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.douyiyuan.first.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2022-08-07
 */
public interface CartMapper extends BaseMapper<Cart> {

    Page<Cart> findPage(Page<Cart> objectPage,
                        @Param("name") String name,
                        @Param("role")String role,
                        @Param("userId") Integer userId);

    List<Cart> findListByIds(List<Integer> ids);
}
