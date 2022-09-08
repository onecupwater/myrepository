package com.douyiyuan.first.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.douyiyuan.first.entity.Notice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2022-08-05
 */
public interface NoticeMapper extends BaseMapper<Notice> {

    Page<Notice> findNoticePage(Page<Notice> objectPage, @Param("name") String name);

    List<Notice> findNoticeList();
}
