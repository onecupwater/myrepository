package com.douyiyuan.first.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.douyiyuan.first.common.ReleaseAnnotation;
import com.douyiyuan.first.common.Result;
import com.douyiyuan.first.common.StatusCode;
import com.douyiyuan.first.common.exception.ServiceException;
import com.douyiyuan.first.mapper.CommentMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.douyiyuan.first.service.ICommentService;
import com.douyiyuan.first.entity.Comment;

import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 评论表 前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2022-08-01
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private ICommentService commentService;

    @Resource
    private CommentMapper commentMapper;

    /**
     * 根据articleId查询所有
     * @return
     */
    @ReleaseAnnotation
    @GetMapping("findCommentTreeListByArticleId/{articleId}")
    public Result findCommentTreeListByArticleId(@PathVariable("articleId") Integer articleId) {
        List<Comment> comments = commentService.findCommentTreeListByArticleId(articleId);
        //将comments改成tree形态
        //找出一级评论
        List<Comment> oneLevelComment = comments.stream()
                .filter(comment -> comment.getPid() == null).collect(Collectors.toList());
        //循环一级评论，对比整个comment的pid与一级评论的id
        //两者相同就把该comment作为一级评论的子级
        for(Comment comment : oneLevelComment){
            List<Comment> childrenComments = comments.stream()
                    .filter(commentInComments -> comment.getId()
                            .equals(commentInComments.getOriginId())).collect(Collectors.toList());
            comment.setChildren(childrenComments);
        }
        //返回所有的一级评论
        return Result.success(oneLevelComment);
    }

    /**
     * 根据id查询记录以及子级记录
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findCommentAndChildrenById(@PathVariable Integer id) {
        Comment comment = commentMapper.findCommentById(id);
        List<Comment> comments = commentMapper.findCommentListByOriginId(id);
        HashMap<String, Object> map = new HashMap<>();
        map.put("mainComment",comment);
        map.put("mainCommentDetails",comments);
        return Result.success(map);
    }

    /**
     * 分页查询
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                                    @RequestParam Integer pageSize) {
        //分页查询如果没有条件，就把new QueryWrapper<>()删了，有条件就在queryWrapper变量加条件
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        //queryWrapper加条件

        queryWrapper.orderByDesc("id");
        Page<Comment> objectPage = new Page<>(pageNum, pageSize);
        Page<Comment> page = commentService.page(objectPage, queryWrapper);
        return Result.success(page);
    }

    /**
     * 新增与修改
     * @param comment
     * @return
     */
    @PostMapping("save")
    public Result save(@RequestBody Comment comment) {
        comment.setCommentTime(LocalDateTime.now());
        //pid为空则是一级评论，不为空则是二级评论
        if(comment.getPid()!=null){
            Comment commentData = commentService.getById(comment.getPid());
            if(ObjectUtil.isEmpty(commentData)){
                throw new ServiceException(StatusCode.CODE_300,"找不到上级评论，上级评论已经被删除");
            }
            //将replyId赋值上一级评论的userid
            comment.setReplyId(commentData.getUserId());
            //如果从数据库查出来的记录的pid不是null，说明上级评论是二级评论，
            // 需要对前端传进来的comment的originid进行修改，否则，不需要修改
            if(commentData.getPid()!=null){
                comment.setOriginId(commentData.getOriginId());
            }
        }
        //给pid=null的comment的floorNum属性赋值
        if(comment.getPid()==null){
            QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("origin_id",0);
            int count =(int) commentService.count(queryWrapper);
            comment.setFloorNum(count+1);
        }
        return Result.success(commentService.saveOrUpdate(comment));
    }

    /**
     * 根据id删除
     * @param id
     * @return
     */
    @DeleteMapping("delete/{id}")
    public Result delete(@PathVariable Integer id) {
        //如果id是二级评论，那直接删二级
        //如果id是一级评论，删掉该一级评论后，再删originid = id的所有评论
        Comment comment = commentService.getById(id);
        if(comment.getPid()!=null){
            return Result.success(commentService.removeById(id));
        }
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("origin_id",id);
        List<Comment> comments = commentMapper.selectList(queryWrapper);
        List<Integer> idList = comments.stream()
                .map(commentInComments -> commentInComments.getId()).collect(Collectors.toList());
        //把id也加进idlist
        idList.add(id);
        return Result.success(commentService.removeBatchByIds(idList));
    }

    /**
     * 批量删除，因为前端的axios传 "[1,2,3]" 这样的参数只能用post方式来传，不能用delete方式来传
     * @param ids
     * @return
     */
    @PostMapping("removeBatchByIds")
    public Result removeBatchByIds(@RequestBody List<Integer> ids){
        return Result.success(commentService.removeBatchByIds(ids));
        }
}

