package com.douyiyuan.first.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.douyiyuan.first.common.Constants;
import com.douyiyuan.first.common.Result;
import com.douyiyuan.first.entity.Dict;
import com.douyiyuan.first.mapper.DictMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.douyiyuan.first.service.IMenuService;
import com.douyiyuan.first.entity.Menu;

import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 菜单管理 前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2022-07-17
 */
@RestController
@RequestMapping("/menu")
public class MenuController {

    @Resource
    private IMenuService menuService;

    @Resource
    private DictMapper dictMapper;

    /**
     * 查询所有菜单的id
     *
     * @return
     */
    @GetMapping("/findAllIds")
    public Result findAllIds() {
        List<Integer> ids = menuService.list().stream()
                .map(menu -> menu.getId()).collect(Collectors.toList());
        return Result.success(ids);
    }

    //递归查找所有菜单的子菜单
    private List<Menu> findChildren(Menu root, List<Menu> all) {
        List<Menu> children = all.stream().filter((menu) -> {
            return menu.getPid() == root.getId();
        }).map((menu) -> {
            //1.找到子菜单
            menu.setChildren(findChildren(menu, all));
            return menu;
        }).sorted((menu1, menu2) -> {
            //2.菜单的排序
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());
        return children;
    }

    /**
     * 查询所有
     *
     * @return
     */
    @GetMapping("tree")
    public Result findAll(@RequestParam(defaultValue = "") String name) {
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotBlank(name)) {
            queryWrapper.like("name", name);
        }

        List<Menu> menus = menuService.list(queryWrapper);
        //2.1)、找到所有的一级分类
        List<Menu> level1Menus = menus.stream().filter((menu) -> {
            return menu.getPid() == null;
        }).map((menu) -> {
            menu.setChildren(findChildren(menu, menus));
            return menu;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());



        /*List<Menu> menus = menuService.list();
        //找出一级菜单
        List<Menu> parentNode = menus.stream()
                .filter(menu -> menu.getPid() == null).collect(Collectors.toList());
        //循环一级菜单，对比整个menu的pid与一级菜单的id
        // 两者相同就把当前menu作为一级菜单的子菜单，
        // 代码的实现：m -> menu.getId().equals(m.getPid())
        for (Menu menu: parentNode) {
            menu.setChildren(menus.stream()
                    .filter(m -> menu.getId().equals(m.getPid())).collect(Collectors.toList()));
        }*/


        /*List<Menu> menus = menuService.list(); //从数据查出所有数据
        ArrayList<Menu> menuHasPidList = new ArrayList<>();
        for (Menu menu:menus) {
            Integer pid = menu.getPid();
            if(pid != null){
                menuHasPidList.add(menu);
            }
        }

        for (Menu menu: menuHasPidList) {
            Integer pid = menu.getPid();
            for (Menu m:menus) {
                if(pid==m.getId()){
                    m.getChildren().add(menu);
                }
            }
            menus.remove(menu);
        }*/

        return Result.success(level1Menus); //return的是一级菜单list
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(menuService.getById(id));
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
                           @RequestParam Integer pageSize) {
        //分页查询如果没有条件，就把new QueryWrapper<>()删了，有条件就在queryWrapper变量加条件
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        //queryWrapper加条件

        queryWrapper.orderByDesc("id");
        Page<Menu> objectPage = new Page<>(pageNum, pageSize);
        Page<Menu> page = menuService.page(objectPage, queryWrapper);
        return Result.success(page);
    }

    /**
     * 新增与修改
     *
     * @param menu
     * @return
     */
    @PostMapping("save")
    public Result save(@RequestBody Menu menu) {
        return Result.success(menuService.saveOrUpdate(menu));
    }

    /**
     * 根据id删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("delete/{id}")
    public Result delete(@PathVariable Integer id) {
        //判断被删的记录是不是一级菜单
        //不是一级菜单就可以直接删，因为只有1级2级菜单
        Menu menu = menuService.getById(id);
        if (menu.getPid() != null) {
            return Result.success(menuService.removeById(id));
        }
        HashSet<Integer> idList = new HashSet<>();  //存放需要被删的记录的所有id,set集合能保证元素不重复

        //是一级菜单，就需要把它的二级菜单都获取出来，一并删除
        findChildrenMenus(id,idList);

        //同时也将一级菜单放进idList
        idList.add(id);

        return Result.success(menuService.removeBatchByIds(idList));
    }

    /**
     * 批量删除，因为前端的axios传 "[1,2,3]" 这样的参数只能用post方式来传，不能用delete方式来传
     *
     * @param ids
     * @return
     */
    @PostMapping("removeBatchByIds")
    public Result removeBatchByIds(@RequestBody List<Integer> ids) {

        HashSet<Integer> idList = new HashSet<>();  //存放需要被删的记录的所有id

        //判断ids有没有一级菜单
        Iterator<Integer> iterator = ids.iterator();

        while (iterator.hasNext()){
            Integer id = iterator.next();
            Menu menu = menuService.getById(id);
            //不是一级菜单，直接放idList，因为只有1级2级菜单
            if(menu.getPid() != null){
                idList.add(id);
                continue;  //结束本次循环，下面的代码在本次循环不再执行
            }
            //是一级菜单，就需要把它的二级菜单都获取出来，一并放idList
            findChildrenMenus(id,idList);
            //同时也将一级菜单放进idList
            idList.add(id);
        }

        //对idList里所有id执行删除
        return Result.success(menuService.removeBatchByIds(idList));
    }

    private void findChildrenMenus(Integer id,HashSet<Integer> idList){
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pid",id);
        List<Menu> menus = menuService.list(queryWrapper);
        Iterator<Menu> childrenMenuIterator = menus.iterator();
        while (childrenMenuIterator.hasNext()){
            Menu childrenMenu = childrenMenuIterator.next();
            idList.add(childrenMenu.getId());
        }
    }

    /**
     * 获取dict表的图标
     */
    @GetMapping("icons")
    public Result getIcons() {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", Constants.ICON);
        List<Dict> dictList = dictMapper.selectList(queryWrapper);
        return Result.success(dictList);
    }
}

