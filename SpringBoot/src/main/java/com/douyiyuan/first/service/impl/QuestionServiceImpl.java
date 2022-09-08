package com.douyiyuan.first.service.impl;

import com.douyiyuan.first.entity.Question;
import com.douyiyuan.first.mapper.QuestionMapper;
import com.douyiyuan.first.service.IQuestionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2022-08-10
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements IQuestionService {

}
