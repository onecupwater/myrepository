package com.douyiyuan.first.mapper;

import com.douyiyuan.first.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2022-07-17
 */
public interface RoleMapper extends BaseMapper<Role> {

    @Select("SELECT id FROM role where flag = #{roleFlag}")
    Integer findRoleIdByRoleFlag(@Param("roleFlag") String roleFlag);
}
