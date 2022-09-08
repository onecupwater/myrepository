package com.douyiyuan.first.service;

import com.douyiyuan.first.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author baomidou
 * @since 2022-07-17
 */
public interface IRoleService extends IService<Role> {

    void saveMenuIdsByRoleId(Integer roleId, List<Integer> menuIds);

    List<Integer> selectMenuIdsByRoleId(Integer roleId);
}
