package com.douyiyuan.first.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.douyiyuan.first.common.StatusCode;
import com.douyiyuan.first.common.exception.ServiceException;
import com.douyiyuan.first.entity.Menu;
import com.douyiyuan.first.entity.Role;
import com.douyiyuan.first.entity.RoleMenu;
import com.douyiyuan.first.mapper.MenuMapper;
import com.douyiyuan.first.mapper.RoleMapper;
import com.douyiyuan.first.mapper.RoleMenuMapper;
import com.douyiyuan.first.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2022-07-17
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private MenuMapper menuMapper;

    @Transactional
    @Override
    public void saveMenuIdsByRoleId(Integer roleId, List<Integer> menuIds) {
        try {
            //先删除roleId对应的角色菜单表的所有记录
            QueryWrapper<RoleMenu> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("role_id",roleId);
            roleMenuMapper.delete(queryWrapper);
            /*
             * 再for循环插入前端传进来的menuIds，但这里会出现一个问题，
             * 前端传进来的menuIds是role页面的tree标签里选中menuId的一个集合，也就是有父子级关系，
             * 那么就存在一个bug，当子级有一个没被选中，父级也不会被自动选中，那就出问题了，当父级没被选中，
             * 其他子级的menuId就会失去父级的的牵引，在这个系统的意思就是有被选中二级菜单却没有一级菜单来作为parent，
             * 这样会导致动态路由不会去渲染这些没有一级菜单的二级菜单。
             * 所以当menuIds里有二级菜单时，必须把对应的一级菜单也加上，这个问题后端可以解决。
             * 但这里加上一级菜单又会出现另一个问题：
             * tree标签之所以不传二级菜单对应的一级菜单，那是element的规则，或者前端的规则，只要父级被选中，
             * 那么父级所有的子级都会默认被选中。这个问题前端可以解决。
             */
            HashSet<Integer> processedMenuIds = new HashSet<>(); //保证相同的pid只有一个
            Iterator<Integer> iterator = menuIds.iterator();
            while (iterator.hasNext()){
                //遍历查看每个menuId有没有pid，多个menuId都有相同的pid时，只插入一条roleId-pid记录
                Integer menuId = iterator.next();
                Menu menu = menuMapper.selectById(menuId);
                if(menu.getPid()!=null){
                    processedMenuIds.add(menu.getPid());
                }
                processedMenuIds.add(menuId);
            }
            //插入处理后的menusIds
            RoleMenu roleMenu = new RoleMenu();
            for (Integer processedMenuId:processedMenuIds) {
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(processedMenuId);
                roleMenuMapper.insert(roleMenu);
            }
        }catch (Exception e){
            throw new ServiceException(StatusCode.CODE_500,"系统错误，无法进行角色菜单分配");
        }
    }

    @Override
    public List<Integer> selectMenuIdsByRoleId(Integer roleId) {
        List<Integer> menuIds = roleMenuMapper.selectMenuIdsByRoleId(roleId);
        return menuIds;
    }
}
