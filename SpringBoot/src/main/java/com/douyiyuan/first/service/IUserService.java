package com.douyiyuan.first.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.douyiyuan.first.controller.dto.UserDTO;
import com.douyiyuan.first.entity.Course;
import com.douyiyuan.first.entity.Menu;
import com.douyiyuan.first.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author baomidou
 * @since 2022-07-10
 */
public interface IUserService extends IService<User> {

    UserDTO login(UserDTO userDTO);

    User register(UserDTO userDTO);

    List<Menu> transformMenus(List<Menu> menus);

    Page<User> findPage(Page<User> objectPage, String name);

    List<Course> showStudentCourse(Integer studentId);

    List<Course> showTeacherCourse(Integer teacherId);

    UserDTO loginByEmail(UserDTO userDTO);

    void resetPass(String email, String code);
}
