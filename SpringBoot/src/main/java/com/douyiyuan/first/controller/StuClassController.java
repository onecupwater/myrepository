package com.douyiyuan.first.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.douyiyuan.first.common.Result;
import com.douyiyuan.first.controller.dto.coursetable.CoursesTable;
import com.douyiyuan.first.controller.dto.coursetable.SectionTime;
import com.douyiyuan.first.entity.Course;
import com.douyiyuan.first.entity.StuClassCourse;
import com.douyiyuan.first.entity.User;
import com.douyiyuan.first.mapper.StuClassCourseMapper;
import com.douyiyuan.first.service.ICourseService;
import com.douyiyuan.first.service.IUserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import com.douyiyuan.first.service.IStuClassService;
import com.douyiyuan.first.entity.StuClass;

import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2022-08-22
 */
@RestController
@RequestMapping("/stuClass")
public class StuClassController {

    @Resource
    private IStuClassService stuClassService;

    @Resource
    private IUserService userService;

    @Resource
    private ICourseService courseService;

    @Resource
    private StuClassCourseMapper stuClassCourseMapper;

    /**
     * 查询所有
     * @return
     */
    @GetMapping
    public Result findAll() {
        return Result.success(stuClassService.list());
    }

    /**
     * 专业id查询所有班级
     * @return
     */
    @GetMapping("findStuClassesByMajorId/{majorId}")
    public Result findStuClassesByMajorId(@PathVariable("majorId") Integer majorId) {
        QueryWrapper<StuClass> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("major_id",majorId);
        return Result.success(stuClassService.list(queryWrapper));
    }

    /**
     * 根据班级id查询stu_class_course表
     * @return
     */
    @GetMapping("findStuClassCourseByStuClassId/{stuClassId}")
    public Result findStuClassCourseByStuClassId(@PathVariable("stuClassId") Integer stuClassId) {
        QueryWrapper<StuClassCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("stu_class_id",stuClassId);
        List<StuClassCourse> stuClassCourseList = stuClassCourseMapper.selectList(queryWrapper);
        return Result.success(stuClassCourseList);
    }

    /**
     * 根据班级id查询stu_class_course表,然后把数据整理成课程表
     * @return
     */
    @GetMapping("getCourseTableByStuClassId/{stuClassId}")
    public Result getCourseTableByStuClassId(@PathVariable("stuClassId") Integer stuClassId) {
        QueryWrapper<StuClassCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("stu_class_id",stuClassId);
        List<StuClassCourse> stuClassCourseList = stuClassCourseMapper.selectList(queryWrapper);

        /**
         * 前端要的数据格式：
         *  {
         *     "sectionTime":{"section": "第一大节", "time": "08:00-09:35"},
         *     "Monday":{"courseName": "大学物理", "room": "A-101","teacher": "张之洞"},
         *     "Tuesday":{"courseName": "大学化学", "room": "A-103","teacher": "邓小平"},
         *     "Thursday":{"courseName": "高等数学", "room": "A-104","teacher": "刘一桃"},
         *  },
         *  返回结果只能有5条数据：因为一天只有5个大节
         *  按照上面的格式，如果要用实体类new对象的方式来做的话，需要创建1个总的实体类，8个小的实体类，
         *  用JSONUtil可以不用写实体类
         */
        ArrayList<JSONObject> coursesTable = new ArrayList<>();

        //五个jsonObject对象代表着一天中的五大节课
        JSONObject jsonObject1 = new JSONObject();
        String sectionTime1 = "{\"section\": \"第一大节\", \"time\": \"08:00-09:35\"}";
        jsonObject1.set("sectionTime",JSONUtil.parseObj(sectionTime1));
        coursesTable.add(jsonObject1);

        JSONObject jsonObject2 = new JSONObject();
        String sectionTime2 = "{\"section\": \"第二大节\", \"time\": \"10:05-11:40\"}";
        jsonObject2.set("sectionTime",JSONUtil.parseObj(sectionTime2));
        coursesTable.add(jsonObject2);

        JSONObject jsonObject3 = new JSONObject();
        String sectionTime3 = "{\"section\": \"第三大节\", \"time\": \"14:00-15:35\"}";
        jsonObject3.set("sectionTime",JSONUtil.parseObj(sectionTime3));
        coursesTable.add(jsonObject3);

        JSONObject jsonObject4 = new JSONObject();
        String sectionTime4 = "{\"section\": \"第四大节\", \"time\": \"16:05-17:40\"}";
        jsonObject4.set("sectionTime",JSONUtil.parseObj(sectionTime4));
        coursesTable.add(jsonObject4);

        JSONObject jsonObject5 = new JSONObject();
        String sectionTime5 = "{\"section\": \"第五大节\", \"time\": \"19:00-20:35\"}";
        jsonObject5.set("sectionTime",JSONUtil.parseObj(sectionTime5));
        coursesTable.add(jsonObject5);

        //循环班级的每一个课程，把每个课程塞进指定的坑位，坑位的条件是先根据第几大节，再根据星期几
        for (StuClassCourse stuClassCourse: stuClassCourseList) {
            String section = stuClassCourse.getSection();
            String weekDay = stuClassCourse.getWeekDay();
            Course course = courseService.getById(stuClassCourse.getCourseId());
            stuClassCourse.setCourseName(course.getName());
            User teacher = userService.getById(stuClassCourse.getTeacherId());
            stuClassCourse.setTeacher(teacher.getNickname());
            switch (section){
                case "第一大节":
                    setJson(weekDay,jsonObject1,stuClassCourse);
                    break;
                case "第二大节":
                    setJson(weekDay,jsonObject2,stuClassCourse);
                    break;
                case "第三大节":
                    setJson(weekDay,jsonObject3,stuClassCourse);
                    break;
                case "第四大节":
                    setJson(weekDay,jsonObject4,stuClassCourse);
                    break;
                case "第五大节":
                    setJson(weekDay,jsonObject5,stuClassCourse);
                    break;
            }
        }
        return Result.success(coursesTable);
    }

    private void setJson(String weekDay,JSONObject jsonObject,StuClassCourse stuClassCourse){
        if(weekDay.equals("星期一")){
            jsonObject.set("Monday",stuClassCourse);
        }else if(weekDay.equals("星期二")){
            jsonObject.set("Tuesday",stuClassCourse);
        }else if(weekDay.equals("星期三")){
            jsonObject.set("Wednesday",stuClassCourse);
        }else if(weekDay.equals("星期四")){
            jsonObject.set("Thursday",stuClassCourse);
        }else if(weekDay.equals("星期五")){
            jsonObject.set("Friday",stuClassCourse);
        }else if(weekDay.equals("星期六")){
            jsonObject.set("Saturday",stuClassCourse);
        }else if(weekDay.equals("星期日")){
            jsonObject.set("Sunday",stuClassCourse);
        }
    }


    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(stuClassService.getById(id));
    }

    /**
     * 分页查询
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam String name) {
        //分页查询如果没有条件，就把new QueryWrapper<>()删了，有条件就在queryWrapper变量加条件
        QueryWrapper<StuClass> queryWrapper = new QueryWrapper<>();
        //queryWrapper加条件
        if(StrUtil.isNotBlank(name)){
            queryWrapper.like("name",name);
        }
        queryWrapper.orderByDesc("id");
        Page<StuClass> objectPage = new Page<>(pageNum, pageSize);
        Page<StuClass> page = stuClassService.page(objectPage, queryWrapper);
        //每次查询班级，都会从user表获取每个班级的人数，然后设置人数
        //找出所有学生
        List<User> students = userService
                .list(new QueryWrapper<User>().eq("role", "ROLE_STUDENT"));
        List<StuClass> classes = page.getRecords();
        //classes循环设置学生人数
        for (StuClass stuClass: classes) {
            long count = students.stream()
                    .filter(student -> student.getStuClassId() == stuClass.getId()).count();
            stuClass.setStuNum((int)count);
            //再保存进数据库，其实这一步做不做都可以，毕竟保存进数据会有额外的线程开销
            stuClassService.updateById(stuClass);
        }
        return Result.success(page);
    }

    /**
     * 新增与修改
     * @param stuClass
     * @return
     */
    @PostMapping("save")
    public Result save(@RequestBody StuClass stuClass) {
        return Result.success(stuClassService.saveOrUpdate(stuClass));
        }

    /**
     * 往stuClassCourse表写入数据
     * @param stuClassCourses
     * @return
     */
    @Transactional
    @PostMapping("saveFromStuClassCourse/{stuClassId}")
    public Result saveFromStuClassCourse(@PathVariable("stuClassId") Integer stuClassId,
                                         @RequestBody List<StuClassCourse> stuClassCourses) {
        //根据stu_class_id，先删后增
        UpdateWrapper<StuClassCourse> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("stu_class_id",stuClassId);
        stuClassCourseMapper.delete(updateWrapper);
        for (StuClassCourse stuClassCourse: stuClassCourses){
            stuClassCourseMapper.insert(stuClassCourse);
        }
        return Result.success(null);
    }

    /**
     * 根据id删除
     * @param id
     * @return
     */
    @DeleteMapping("delete/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(stuClassService.removeById(id));
        }

    /**
     * 批量删除，因为前端的axios传 "[1,2,3]" 这样的参数只能用post方式来传，不能用delete方式来传
     * @param ids
     * @return
     */
    @PostMapping("removeBatchByIds")
    public Result removeBatchByIds(@RequestBody List<Integer> ids){
        return Result.success(stuClassService.removeBatchByIds(ids));
        }
}

