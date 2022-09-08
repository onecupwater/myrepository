package com.douyiyuan.first.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.douyiyuan.first.common.Constants;
import com.douyiyuan.first.common.exception.ServiceException;
import com.douyiyuan.first.common.StatusCode;
import com.douyiyuan.first.controller.dto.UserDTO;
import com.douyiyuan.first.entity.*;
import com.douyiyuan.first.mapper.*;
import com.douyiyuan.first.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2022-07-10
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private static final Log LOGGER = Log.get();

    @Resource
    private UserMapper userMapper;

    @Resource
    private EmailVerificationMapper emailVerificationMapper;

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private StudentCourseMapper studentCourseMapper;

    @Resource
    private TeacherCourseMapper teacherCourseMapper;

    //账号密码登录
    @Override
    public UserDTO login(UserDTO userDTO) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("name",userDTO.getName());
        userQueryWrapper.eq("password",userDTO.getPassword());

        User one;
        try{
            one = getOne(userQueryWrapper);
        }catch (Exception e){
            LOGGER.error(e);
            throw new ServiceException(StatusCode.CODE_500,"系统错误");
        }

        if(one!=null){
            BeanUtil.copyProperties(one,userDTO);
            return userDTO;
        }else {
            throw new ServiceException(StatusCode.CODE_300,"用户名或者密码错误");
        }
    }

    //邮箱登录
    @Override
    public UserDTO loginByEmail(UserDTO userDTO) {
        //先查user表看有没有当前email
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("email",userDTO.getEmail());
        User one;
        //找到多条的情况
        try{
            one = getOne(userQueryWrapper);
        }catch (Exception e){
            LOGGER.error(e);
            throw new ServiceException(StatusCode.CODE_500,"系统错误");
        }
        //没有找到的情况
        if(one==null){
            throw new ServiceException(StatusCode.CODE_300,"未找到用户");
        }
        //去user表找到一条后，再去email_verification表用email+code，看能不能找到
        QueryWrapper<EmailVerification> emailVerificationQueryWrapper = new QueryWrapper<>();
        emailVerificationQueryWrapper.eq("email",userDTO.getEmail());
        emailVerificationQueryWrapper.eq("code", userDTO.getCode());
        //下面这句代码比较难理解，ge是大于等于，sql语句的主体是数据库的记录的过期时间，而不是你给时间，
        // 当记录的过期时间大于等于now时，说明验证码还没过期，而不是拿now去比较记录的过期时间，要明白sql的主体时谁
        // 如果条件不满足，那查出来就是null
        emailVerificationQueryWrapper.ge("expire_time",LocalDateTime.now());
        EmailVerification emailVerification = emailVerificationMapper
                .selectOne(emailVerificationQueryWrapper);
        if(emailVerification==null){
            throw new ServiceException(StatusCode.CODE_300,"验证码无效");
        }
        //上面的验证都通过之后，才能返回数据库查到的user
        BeanUtil.copyProperties(one,userDTO);
        return userDTO;
    }

    //重置密码
    @Override
    public void resetPass(String email, String code) {
        //先查user表看有没有当前email
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("email",email);
        User one;
        //找到多条的情况
        try{
            one = getOne(userQueryWrapper);
        }catch (Exception e){
            LOGGER.error(e);
            throw new ServiceException(StatusCode.CODE_500,"系统错误");
        }
        //没有找到的情况
        if(one==null){
            throw new ServiceException(StatusCode.CODE_300,"未找到用户");
        }
        //去user表找到一条后，再去email_verification表用email+code，看能不能找到
        QueryWrapper<EmailVerification> emailVerificationQueryWrapper = new QueryWrapper<>();
        emailVerificationQueryWrapper.eq("email",email);
        emailVerificationQueryWrapper.eq("code", code);
        //下面这句代码比较难理解，ge是大于等于，sql语句的主体是数据库的记录的过期时间，而不是你给时间，
        // 当记录的过期时间大于等于now时，说明验证码还没过期，而不是拿now去比较记录的过期时间，要明白sql的主体时谁
        // 如果条件不满足，那查出来就是null
        emailVerificationQueryWrapper.ge("expire_time",LocalDateTime.now());
        EmailVerification emailVerification = emailVerificationMapper
                .selectOne(emailVerificationQueryWrapper);
        if(emailVerification==null){
            throw new ServiceException(StatusCode.CODE_300,"验证码无效");
        }
        //上面的验证都通过之后，才能将密码重置为123
        one.setPassword("123");
        updateById(one);
    }

    @Override
    public User register(UserDTO userDTO) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",userDTO.getName());
        Map<String, Object> map = getMap(queryWrapper);
        if(map==null){
            User user = new User();
            user.setName(userDTO.getName());
            user.setPassword(userDTO.getPassword());
            user.setCreateDate(LocalDateTime.now());
            user.setUpdateDate(LocalDateTime.now());
            try{
                save(user);
                return user;
            }catch (Exception e){
                LOGGER.error(e);
                throw new ServiceException(StatusCode.CODE_300,"系统异常");
            }
        }
        throw new ServiceException(StatusCode.CODE_300,"用户名已存在");
    }

//-------------------------------------------------------------------------------------------------

    @Override
    public List<Menu> transformMenus(List<Menu> menus) {
        //2.1)、找到所有的一级分类
        List<Menu> level1Menus = menus.stream().filter((menu) -> {
            return menu.getPid() == null;
        }).map((menu) -> {
            menu.setChildren(findChildren(menu, menus));
            return menu;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());
        return level1Menus;
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

//-----------------------------------------------------------------------------------------

    @Override
    public Page<User> findPage(Page<User> objectPage, String name) {
        return userMapper.findPage(objectPage,name);
    }

    @Override
    public List<Course> showStudentCourse(Integer studentId) {
        QueryWrapper<StudentCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id",studentId);
        List<StudentCourse> studentCourses = studentCourseMapper.selectList(queryWrapper);
        if(ObjectUtil.isEmpty(studentCourses)){
            throw new ServiceException(StatusCode.CODE_300,"当前学生未选课");
        }
        List<Integer> courseIds = studentCourses.stream()
                .map(studentCourse -> studentCourse.getCourseId()).collect(Collectors.toList());
        List<Course> courses = courseMapper.selectBatchIds(courseIds);
        return courses;
    }

    @Override
    public List<Course> showTeacherCourse(Integer teacherId) {
        QueryWrapper<TeacherCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teacher_id",teacherId);
        List<TeacherCourse> teacherCourses = teacherCourseMapper.selectList(queryWrapper);
        if(ObjectUtil.isEmpty(teacherCourses)){
            throw new ServiceException(StatusCode.CODE_300,"当前老师未任课");
        }
        List<Integer> courseIds = teacherCourses.stream()
                .map(teacherCourse -> teacherCourse.getCourseId()).collect(Collectors.toList());
        List<Course> courses = courseMapper.selectBatchIds(courseIds);
        return courses;
    }

}
