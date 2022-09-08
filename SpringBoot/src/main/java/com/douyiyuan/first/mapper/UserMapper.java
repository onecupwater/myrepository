package com.douyiyuan.first.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.douyiyuan.first.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户信息表 Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2022-07-10
 */
public interface UserMapper extends BaseMapper<User> {

    Page<User> findPage(Page<User> objectPage, @Param("name") String name);
}
