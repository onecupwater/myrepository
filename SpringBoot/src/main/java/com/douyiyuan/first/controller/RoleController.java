package com.douyiyuan.first.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.douyiyuan.first.common.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import com.douyiyuan.first.service.IRoleService;
import com.douyiyuan.first.entity.Role;

import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2022-07-17
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Resource
    private IRoleService roleService;


    /**
     * 查询所有
     * @return
     */
    @GetMapping("findAll")
    public List<Role> findAll() {
        return roleService.list();
    }


    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(roleService.getById(id));
    }

    /**
     * 分页查询
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam String name) {
        //分页查询如果没有条件，就把new QueryWrapper<>()删了，有条件就在queryWrapper变量加条件
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        //queryWrapper加条件
        if (StrUtil.isNotBlank(name)) {
            queryWrapper.like("name", name);
        }
        queryWrapper.orderByDesc("id");
        Page<Role> objectPage = new Page<>(pageNum, pageSize);
        Page<Role> page = roleService.page(objectPage, queryWrapper);
        return Result.success(page);
    }

    /**
     * 新增与修改
     *
     * @param role
     * @return
     */
    @PostMapping("save")
    public Result save(@RequestBody Role role) {
        return Result.success(roleService.saveOrUpdate(role));
    }

    /**
     * 根据id删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("delete/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(roleService.removeById(id));
    }

    /**
     * 批量删除，因为前端的axios传 "[1,2,3]" 这样的参数只能用post方式来传，不能用delete方式来传
     *
     * @param ids
     * @return
     */
    @PostMapping("removeBatchByIds")
    public Result removeBatchByIds(@RequestBody List<Integer> ids) {
        return Result.success(roleService.removeBatchByIds(ids));
    }

    /**
     * 存储角色id对应的所有菜单id
     * 操作：先删后加
     *
     * @param roleId
     * @param menuIds
     * @return
     */
    @PostMapping("saveMenuIdsByRoleId/{roleId}")
    public Result saveMenuIdsByRoleId(@PathVariable("roleId") Integer roleId,
                                      @RequestBody List<Integer> menuIds) {
        roleService.saveMenuIdsByRoleId(roleId, menuIds);
        return Result.success("角色菜单分配成功", null);
    }

    /**
     * 根据roleId查对应的所有menuIds
     *
     * @param roleId
     * @return
     */
    @GetMapping("selectMenuIdsByRoleId/{roleId}")
    public Result selectMenuIdsByRoleId(@PathVariable("roleId") Integer roleId) {
        List<Integer> menuIds = roleService.selectMenuIdsByRoleId(roleId);
        return Result.success(menuIds);
    }
}

