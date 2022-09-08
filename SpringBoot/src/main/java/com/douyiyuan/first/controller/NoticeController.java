package com.douyiyuan.first.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.douyiyuan.first.common.Result;
import com.douyiyuan.first.mapper.NoticeMapper;
import com.douyiyuan.first.util.TokenUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import com.douyiyuan.first.service.INoticeService;
import com.douyiyuan.first.entity.Notice;

import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2022-08-05
 */
@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Resource
    private INoticeService noticeService;

    @Resource
    private NoticeMapper noticeMapper;

    /**
     * 查询所有
     * @return
     */
    @GetMapping
    public Result findAll() {
        return Result.success(noticeMapper.findNoticeList());
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(noticeService.getById(id));
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

        Page<Notice> objectPage = new Page<>(pageNum, pageSize);
        Page<Notice> page = noticeService.findNoticePage(objectPage,name);
        return Result.success(page);
    }

    /**
     * 新增与修改
     * @param notice
     * @return
     */
    @PostMapping("save")
    public Result save(@RequestBody Notice notice) {
        notice.setUserId(TokenUtil.getUser().getId());
        notice.setTime(LocalDateTime.now());
        return Result.success(noticeService.saveOrUpdate(notice));
        }

    /**
     * 根据id删除
     * @param id
     * @return
     */
    @DeleteMapping("delete/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(noticeService.removeById(id));
        }

    /**
     * 批量删除，因为前端的axios传 "[1,2,3]" 这样的参数只能用post方式来传，不能用delete方式来传
     * @param ids
     * @return
     */
    @PostMapping("removeBatchByIds")
    public Result removeBatchByIds(@RequestBody List<Integer> ids){
        return Result.success(noticeService.removeBatchByIds(ids));
        }
}

