package com.douyiyuan.first.service.impl;

import com.douyiyuan.first.entity.EmailVerification;
import com.douyiyuan.first.mapper.EmailVerificationMapper;
import com.douyiyuan.first.service.IEmailVerificationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2022-08-25
 */
@Service
public class EmailVerificationServiceImpl extends ServiceImpl<EmailVerificationMapper, EmailVerification> implements IEmailVerificationService {

}
