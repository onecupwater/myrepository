package com.douyiyuan.first.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.douyiyuan.first.common.Result;
import com.douyiyuan.first.common.StatusCode;
import com.douyiyuan.first.common.exception.ServiceException;
import com.douyiyuan.first.controller.dto.HandlePaperDTO;
import com.douyiyuan.first.controller.dto.PaperDTO;
import com.douyiyuan.first.entity.PaperQuestion;
import com.douyiyuan.first.entity.Question;
import com.douyiyuan.first.mapper.PaperQuestionMapper;
import com.douyiyuan.first.mapper.QuestionMapper;
import com.douyiyuan.first.service.IQuestionService;
import org.springframework.web.bind.annotation.RequestMapping;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.douyiyuan.first.service.IPaperService;
import com.douyiyuan.first.entity.Paper;

import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2022-08-11
 */
@RestController
@RequestMapping("/paper")
public class PaperController {

    @Resource
    private IPaperService paperService;

    @Resource
    private IQuestionService questionService;

    @Resource
    private QuestionMapper questionMapper;

    @Resource
    private PaperQuestionMapper paperQuestionMapper;

    /**
     * 根据id去查paper_question表，再用questionIds去查question表
     * @return
     */
    @GetMapping("findPaperByQuestionIds/{paperId}")
    public Result findPaperByQuestionIds(@PathVariable("paperId") Integer paperId) {
        QueryWrapper<PaperQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("paper_id",paperId);
        List<PaperQuestion> paperQuestions = paperQuestionMapper.selectList(queryWrapper);
        if(CollUtil.isEmpty(paperQuestions)){
            throw new ServiceException(StatusCode.CODE_300,"请先组卷，再查看");
        }
        List<Integer> questionIds = paperQuestions.stream()
                .map(paperQuestion -> paperQuestion.getQuestionId()).collect(Collectors.toList());
        List<Question> questions = questionMapper.selectBatchIds(questionIds);
        //按照选择题，判断题，填空题的顺序进行排序
        ArrayList<Question> sortQuestions = new ArrayList<>();
        List<Question> choiceQuestionList = questions.stream()
                .filter(question -> question.getQuestionType() == 1).collect(Collectors.toList());
        sortQuestions.addAll(choiceQuestionList);
        List<Question> judgeQuestions = questions.stream()
                .filter(question -> question.getQuestionType() == 2).collect(Collectors.toList());
        sortQuestions.addAll(judgeQuestions);
        List<Question> fillQuestions = questions.stream()
                .filter(question -> question.getQuestionType() == 3).collect(Collectors.toList());
        sortQuestions.addAll(fillQuestions);
        return Result.success(sortQuestions);
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(paperService.getById(id));
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
        QueryWrapper<Paper> queryWrapper = new QueryWrapper<>();
        //queryWrapper加条件
        if(StrUtil.isNotBlank(name)){
            queryWrapper.like("name",name);
        }
        queryWrapper.orderByDesc("id");
        Page<Paper> objectPage = new Page<>(pageNum, pageSize);
        Page<Paper> page = paperService.page(objectPage, queryWrapper);
        return Result.success(page);
    }

    @GetMapping("/findPaperByCourseId/{courseId}")
    public Result findPaperByCourseId(@PathVariable("courseId") Integer courseId) {
        BaseMapper<Paper> mapper = paperService.getBaseMapper();
        QueryWrapper<Paper> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id",courseId);
        List<Paper> papers = mapper.selectList(queryWrapper);
        return Result.success(papers);
    }

    //自动组卷
    @PostMapping("autoGenerate")
    public Result autoGenerate(@RequestBody PaperDTO paperDTO){

        //根据paperId先删后加,关联表
        QueryWrapper<PaperQuestion> wrapper = new QueryWrapper<>();
        wrapper.eq("paper_id",paperDTO.getPaperId());
        paperQuestionMapper.delete(wrapper);

        //根据courseId从question表中符合条件的所有数据
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id",paperDTO.getCourseId());
        List<Question> questionList = questionService.list(queryWrapper);

        //用流将数据进行分组(选择题，判断题，填空题)
        List<Question> choiceQuestions = questionList.stream()
                .filter(question -> question.getQuestionType() == 1).collect(Collectors.toList());
        List<Question> judgeQuestions = questionList.stream()
                .filter(question -> question.getQuestionType() == 2).collect(Collectors.toList());
        List<Question> fillQuestions = questionList.stream()
                .filter(question -> question.getQuestionType() == 3).collect(Collectors.toList());

        //判断分组后的个数与用户输入的个数，如果数据库数量少就报错
        if(choiceQuestions.size() < paperDTO.getChoiceNum()){
            throw  new ServiceException(StatusCode.CODE_300,"题库的 “选择题” 数量少于输入的选择题数量");
        }
        if(judgeQuestions.size() < paperDTO.getJudgeNum()){
            throw  new ServiceException(StatusCode.CODE_300,"题库的 “判断题” 数量少于输入的判断题数量");
        }
        if(fillQuestions.size() < paperDTO.getFillNum()){
            throw  new ServiceException(StatusCode.CODE_300,"题库的 “填空题” 数量少于输入的填空题数量");
        }

        //获取分组里集合的所有questionId,组成集合
        List<Integer> choiceQuestionIds = choiceQuestions.stream()
                .map(question -> question.getId()).collect(Collectors.toList());
        List<Integer> judgeQuestionIds = judgeQuestions.stream()
                .map(question -> question.getId()).collect(Collectors.toList());
        List<Integer> fillQuestionIds = fillQuestions.stream()
                .map(question -> question.getId()).collect(Collectors.toList());

        //根据paperDTO要求的各种类型题目的个数，随机获取对应的questionId个数
        Set<Integer> randomChoiceQuestionIds =
                RandomUtil.randomEleSet(choiceQuestionIds, paperDTO.getChoiceNum());
        Set<Integer> randomJudgeQuestionIds =
                RandomUtil.randomEleSet(judgeQuestionIds, paperDTO.getJudgeNum());
        Set<Integer> randomFillQuestionIds =
                RandomUtil.randomEleSet(fillQuestionIds, paperDTO.getFillNum());
        //3个随机id集合合成一个
        randomChoiceQuestionIds.addAll(randomJudgeQuestionIds);
        randomChoiceQuestionIds.addAll(randomFillQuestionIds);
        Set<Integer> allRandomQuestionIds = randomChoiceQuestionIds;

        //在paper_question表循环插入questionId,paperId
        for (Integer allRandomQuestionId : allRandomQuestionIds) {
            PaperQuestion paperQuestion = new PaperQuestion();
            paperQuestion.setPaperId(paperDTO.getPaperId());
            paperQuestion.setQuestionId(allRandomQuestionId);
            paperQuestionMapper.insert(paperQuestion);
        }
        //走到这里，自动生成试卷的代码就算完成了
        return Result.success(null);
    }

    //自动组卷
    @PostMapping("handleGenerate")
    public Result handleGenerate(@RequestBody HandlePaperDTO handlePaperDTO){
        if(CollUtil.isEmpty(handlePaperDTO.getQuestionIds())){
            throw new ServiceException(StatusCode.CODE_300,"请选择题目后再提交");
        }
        //先删
        Integer paperId = handlePaperDTO.getPaperId();
        QueryWrapper<PaperQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("paper_id",paperId);
        paperQuestionMapper.delete(queryWrapper);
        //后增
        for (Integer questionId: handlePaperDTO.getQuestionIds()) {
            PaperQuestion paperQuestion = new PaperQuestion();
            paperQuestion.setPaperId(paperId);
            paperQuestion.setQuestionId(questionId);
            paperQuestionMapper.insert(paperQuestion);
        }
        return Result.success(null);
    }

    /**
     * 新增与修改
     * @param paper
     * @return
     */
    @PostMapping("save")
    public Result save(@RequestBody Paper paper) {
        return Result.success(paperService.saveOrUpdate(paper));
        }

    /**
     * 根据id删除
     * @param id
     * @return
     */
    @DeleteMapping("delete/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(paperService.removeById(id));
        }

    /**
     * 批量删除，因为前端的axios传 "[1,2,3]" 这样的参数只能用post方式来传，不能用delete方式来传
     * @param ids
     * @return
     */
    @PostMapping("removeBatchByIds")
    public Result removeBatchByIds(@RequestBody List<Integer> ids){
        return Result.success(paperService.removeBatchByIds(ids));
        }
}

