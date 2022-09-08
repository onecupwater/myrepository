package com.douyiyuan.first.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.douyiyuan.first.common.ReleaseAnnotation;
import com.douyiyuan.first.common.Result;
import com.douyiyuan.first.entity.Paper;
import com.douyiyuan.first.entity.Sign;
import com.douyiyuan.first.service.IPaperService;
import com.douyiyuan.first.service.ISignService;
import com.douyiyuan.first.util.TokenUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import com.douyiyuan.first.service.IExamService;
import com.douyiyuan.first.entity.Exam;

import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2022-08-18
 */
@RestController
@RequestMapping("/exam")
public class ExamController {

    @Resource
    private IExamService examService;

    @Resource
    private ISignService signService;

    @Resource
    private IPaperService paperService;

    /**
     * 查询所有
     * @return
     */
    @GetMapping
    public Result findAll() {
        return Result.success(examService.list());
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(examService.getById(id));
    }

        /**
         * 后台分页查询
         * @param pageNum
         * @param pageSize
         * @return
         */
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam String name,
                           @RequestParam(defaultValue = "") Integer courseId) {
        Page<Exam> objectPage = new Page<>(pageNum, pageSize);
        Page<Exam> page = examService.findPage(objectPage, name,courseId);
        List<Exam> exams = page.getRecords();
        for (Exam exam: exams) {
            //如果考试还没有绑定试卷，跳出循环，执行下一次循环
            Integer paperId = exam.getPaperId();
            if(paperId==null){
                continue;
            }
            //对考试状态的设置，根据paperId获取paper里的duration，考试状态分3种：未开始，进行中，已结束
            // exam里的startTime + duration = endTime；
            // 当startTime 大于 now，状态设置：未开始
            // 当endTime 小于 now，状态设置：已结束
            // 当 now 在 startTime 与 endTime 之间，状态设置：进行中
            Paper paper = paperService.getById(paperId);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startTime = exam.getStartTime();
            LocalDateTime endTime = startTime.plusMinutes(paper.getDuration());
            if(startTime.compareTo(now)>0){
                exam.setState("未开始");
            }
            if(endTime.compareTo(now)<0){
                exam.setState("已结束");
            }
            if(now.compareTo(startTime)>=0 && now.compareTo(endTime)<=0){
                exam.setState("进行中");
            }
        }
        return Result.success(page);
    }

    /**
     * 前台分页查询，用exam实体类中的enable来操控考试页面的‘开始考试’按钮的显示与隐藏,enable不会存储到数据库
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/findPageFront")
    public Result findPageFront(@RequestParam Integer pageNum,
                                @RequestParam Integer pageSize,
                                @RequestParam String name,
                                @RequestParam(defaultValue = "") Integer courseId) {
        Page<Exam> objectPage = new Page<>(pageNum, pageSize);
        Page<Exam> page = examService.findPage(objectPage, name,courseId);
        List<Exam> exams = page.getRecords();
        //对查出来的exams再进行加工，主要是对考试状态的显示和控制开始考试按钮的显示与隐藏
        //获取当前学生的考试审核情况
        Integer userId = TokenUtil.getUser().getId();
        QueryWrapper<Sign> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        List<Sign> signs = signService.getBaseMapper().selectList(queryWrapper);
        //审核通过的examId，page里的Exam对应的Id的实体类，设置enable为true，其他都为false
        for (Exam exam: exams) {

            //对enable的设置
            exam.setEnable(false); //对所有的exam先设置false，发现审核通过的再改成true
            //下面这句代码的解释：在sign集合里的每一个sign与每一个exam做比较，
            // （.filter，返回的是集合）：找到sign对象的examId与exam的id相同的sign对象集合，
            // （.findFirst，返回集合的第一个对象）：对这个‘sign对象集合’进行获取第一个sign对象，
            // （.ifPresent，对对象进行判断是否存在）：如果能获取第一个的话，就对当前的exam对象的enable属性进行设置
            signs.stream().filter(sign -> sign.getExamId().equals(exam.getId()))
                    .findFirst().ifPresent(sign -> {
                        exam.setEnable("审核通过".equals(sign.getState()));
            });
            //如果考试还没有绑定试卷，跳出循环，执行下一次循环
            Integer paperId = exam.getPaperId();
            if(paperId==null){
                continue;
            }
            //对考试状态的设置，根据paperId获取paper里的duration，考试状态分3种：未开始，进行中，已结束
            // exam里的startTime + duration = endTime；
            // 当startTime 大于 now，状态设置：未开始
            // 当endTime 小于 now，状态设置：已结束
            // 当 now 在 startTime 与 endTime 之间，状态设置：进行中
            Paper paper = paperService.getById(paperId);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startTime = exam.getStartTime();
            LocalDateTime endTime = startTime.plusMinutes(paper.getDuration());
            if(startTime.compareTo(now)>0){
                exam.setState("未开始");
            }
            if(endTime.compareTo(now)<0){
                exam.setState("已结束");
            }
            if(now.compareTo(startTime)>=0 && now.compareTo(endTime)<=0){
                exam.setState("进行中");
            }
        }

        return Result.success(page);
    }

    /**
     * 新增与修改
     * @param exam
     * @return
     */
    @PostMapping("save")
    public Result save(@RequestBody Exam exam) {
        System.out.println(exam);
        return Result.success(examService.saveOrUpdate(exam));
        }

    /**
     * 根据id删除
     * @param id
     * @return
     */
    @DeleteMapping("delete/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(examService.removeById(id));
        }

    /**
     * 批量删除，因为前端的axios传 "[1,2,3]" 这样的参数只能用post方式来传，不能用delete方式来传
     * @param ids
     * @return
     */
    @PostMapping("removeBatchByIds")
    public Result removeBatchByIds(@RequestBody List<Integer> ids){
        return Result.success(examService.removeBatchByIds(ids));
        }
}

