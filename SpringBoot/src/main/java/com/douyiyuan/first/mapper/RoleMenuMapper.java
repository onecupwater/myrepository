package com.douyiyuan.first.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.douyiyuan.first.entity.RoleMenu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    @Select("SELECT menu_id FROM role_menu where role_id = #{roleId}")
    List<Integer> selectMenuIdsByRoleId(@Param("roleId") Integer roleId);
}
