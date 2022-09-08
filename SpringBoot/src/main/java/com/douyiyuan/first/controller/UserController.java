package com.douyiyuan.first.controller;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.douyiyuan.first.common.ReleaseAnnotation;
import com.douyiyuan.first.common.Result;
import com.douyiyuan.first.common.StatusCode;
import com.douyiyuan.first.common.exception.ServiceException;
import com.douyiyuan.first.controller.dto.UserDTO;
import com.douyiyuan.first.entity.Course;
import com.douyiyuan.first.entity.Menu;
import com.douyiyuan.first.mapper.MenuMapper;
import com.douyiyuan.first.mapper.RoleMapper;
import com.douyiyuan.first.mapper.RoleMenuMapper;
import com.douyiyuan.first.util.TokenUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.List;

import com.douyiyuan.first.service.IUserService;
import com.douyiyuan.first.entity.User;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


/**
 * <p>
 * 用户信息表 前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2022-07-10
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private MenuMapper menuMapper;

    @PostMapping("register")
    public Result register(@RequestBody UserDTO userDTO){
        String username = userDTO.getName();
        String password = userDTO.getPassword();
        if(StrUtil.isNotBlank(username)||StrUtil.isNotBlank(password)){
            User user = userService.register(userDTO);
            user.setPassword(null);
            return Result.success("注册成功,请点击'返回登录'，登录后进入系统",user);
        }
        return Result.error(StatusCode.CODE_300,"请完整输入用户名和密码");
    }

    //账号密码登录
    @PostMapping("login")
    public Result login(@RequestBody UserDTO userDTO){
        String username = userDTO.getName();
        String password = userDTO.getPassword();
        if(StrUtil.isNotBlank(username)||StrUtil.isNotBlank(password)){

            //验证数据库有没有该用户
            UserDTO userDTONew = userService.login(userDTO);

            //存储token
            String token = TokenUtil.createToken(userDTONew.getId().toString(), userDTONew.getPassword());
            userDTONew.setToken(token);
            userDTONew.setPassword(null);

            //存储该用户的所有菜单权限,数据不仅仅是数据库的记录，还要以一级二级菜单的方式展现当前用户menuList
            String roleFlag = userDTONew.getRole();
            Integer roleId = roleMapper.findRoleIdByRoleFlag(roleFlag);
            List<Integer> menuIds = roleMenuMapper.selectMenuIdsByRoleId(roleId);
            //menuIds为空，说明该用户不能访问后台
            if(menuIds == null || menuIds.size() == 0){
                //不能访问后台，也就不用设置menus属性，直接返回就好
                return Result.success("登录成功",userDTONew);
            }
            List<Menu> menus = menuMapper.selectBatchIds(menuIds);
            //对menus进行改造
            List<Menu> transformMenus = userService.transformMenus(menus);
            userDTONew.setMenus(transformMenus);
            return Result.success("登录成功",userDTONew);
        }
        return Result.error(StatusCode.CODE_300,"请输入用户名或密码！");
    }

    //邮箱验证登录
    @ReleaseAnnotation
    @PostMapping("loginByEmail")
    public Result loginByEmail(@RequestBody UserDTO userDTO){
        String email = userDTO.getEmail();
        String code = userDTO.getCode();
        if(StrUtil.isNotBlank(email)||StrUtil.isNotBlank(code)){

            //验证数据库有没有该用户
            UserDTO userDTONew = userService.loginByEmail(userDTO);

            //存储token
            String token = TokenUtil.createToken(userDTONew.getId().toString(), userDTONew.getPassword());
            userDTONew.setToken(token);
            userDTONew.setPassword(null);

            //存储该用户的所有菜单权限,数据不仅仅是数据库的记录，还要以一级二级菜单的方式展现当前用户menuList
            String roleFlag = userDTONew.getRole();
            Integer roleId = roleMapper.findRoleIdByRoleFlag(roleFlag);
            List<Integer> menuIds = roleMenuMapper.selectMenuIdsByRoleId(roleId);
            //menuIds为空，说明该用户不能访问后台
            if(menuIds == null || menuIds.size() == 0){
                //不能访问后台，也就不用设置menus属性，直接返回就好
                return Result.success("登录成功",userDTONew);
            }
            List<Menu> menus = menuMapper.selectBatchIds(menuIds);
            //对menus进行改造
            List<Menu> transformMenus = userService.transformMenus(menus);
            userDTONew.setMenus(transformMenus);
            return Result.success("登录成功",userDTONew);
        }
        return Result.error(StatusCode.CODE_300,"请输入邮箱或验证码！");
    }

    //重置密码
    @ReleaseAnnotation
    @PostMapping("resetPass")
    public Result resetPass(@RequestBody UserDTO userDTO){
        userService.resetPass(userDTO.getEmail(),userDTO.getCode());
        return Result.success("重置密码成功，重置后的密码为：123 ",null);
    }

    /**
     * 导入
     * @param file
     * @return
     */
    @PostMapping("/import")
    public Boolean imp(MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            ExcelReader reader = ExcelUtil.getReader(inputStream);
            List<User> users = reader.readAll(User.class);
            for (User user:users) {
                user.setCreateDate(LocalDateTime.now());
                user.setUpdateDate(LocalDateTime.now());
            }
            return userService.saveBatch(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 导出
     * @param response
     */
    @GetMapping("/export")
    public void exp(HttpServletResponse response){
        //查询所有数据
        List<User> users = userService.list();

        //使用hutool工具将数据写进xlsx文件到浏览器，让用户从浏览器下载
        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.write(users,true);

        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            String fileName = URLEncoder.encode("用户信息","utf-8");
            response.setHeader("Content-Disposition","attachment;filename="+fileName+".xlsx");

            ServletOutputStream out = response.getOutputStream();
            writer.flush(out,true);
            writer.close();
            IoUtil.close(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询所有
     * @return
     */
    @GetMapping
    public List<User> findAll() {
        return userService.list();
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public User findOne(@PathVariable Integer id) {
        return userService.getById(id);
    }

    /**
     * 用户分页查询
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public Page<User> findPage(@RequestParam Integer pageNum,
                               @RequestParam Integer pageSize,
                               @RequestParam String name) {
        //分页查询如果没有条件，就把new QueryWrapper<>()删了，有条件就在queryWrapper变量加条件
        /*QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //queryWrapper加条件
        if(name!=""){
            queryWrapper.like("name",name);
        }
        queryWrapper.orderByDesc("id");
        Page<User> objectPage = new Page<>(pageNum, pageSize);*/

        //这里的前端展示页面除了user表的数据，多了一个role表的name数据
        //使用了联合查询，1对1
        Page<User> objectPage = new Page<>(pageNum, pageSize);
        Page<User> page = userService.findPage(objectPage, name);
        return page;
    }

    /**
     * 查询所有老师
     * @return
     */
    @GetMapping("findAllTeachers")
    public Result findAllTeachers() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role","ROLE_TEACHER");
        return Result.success(userService.list(queryWrapper));
    }

    /**
     * 学生分页查询
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/findStudentPage")
    public Result findStudentPage(@RequestParam Integer pageNum,
                                  @RequestParam Integer pageSize,
                                  @RequestParam String nickname) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role","ROLE_STUDENT");
        if(StrUtil.isNotBlank(nickname)){
            queryWrapper.like("nickname", nickname);
        }
        Page<User> userPage = new Page<>(pageNum,pageSize);
        Page<User> page = userService.page(userPage, queryWrapper);
        return Result.success(page);
    }

    /**
     * 新增与修改
     * @param user
     * @return
     */
    @PostMapping("save")
    public Result save(@RequestBody User user) {
        if(user.getId()==null){
            user.setCreateDate(LocalDateTime.now());
            user.setUpdateDate(LocalDateTime.now());
            return Result.success(userService.save(user));
        }else {
            user.setUpdateDate(LocalDateTime.now());
            userService.updateById(user);
            User newUser = userService.getById(user.getId());

            return Result.success(newUser);
        }
    }

    /**
     * 根据id删除
     * @param id
     * @return
     */
    @DeleteMapping("delete/{id}")
    public Boolean delete(@PathVariable Integer id) {
        return userService.removeById(id);
        }

    /**
     * 批量删除，因为前端的axios传 "[1,2,3]" 这样的参数只能用post方式来传，不能用delete方式来传
     * @param ids
     * @return
     */
    @PostMapping("removeBatchByIds")
    public Boolean removeBatchByIds(@RequestBody List<Integer> ids){
        return userService.removeBatchByIds(ids);
    }

    /**
     * 查看学生选课信息
     * @param studentId
     * @return
     */
    @GetMapping("showStudentCourse/{studentId}")
    public Result showStudentCourse(@PathVariable("studentId") Integer studentId){
        List<Course> coursesByStudentId = userService.showStudentCourse(studentId);
        return Result.success(coursesByStudentId);
    }

    /**
     * 查看老师选课信息
     * @param teacherId
     * @return
     */
    @GetMapping("showTeacherCourse/{teacherId}")
    public Result showTeacherCourse(@PathVariable("teacherId") Integer teacherId){
        List<Course> coursesByTeacherId = userService.showTeacherCourse(teacherId);
        return Result.success(coursesByTeacherId);
    }
}

