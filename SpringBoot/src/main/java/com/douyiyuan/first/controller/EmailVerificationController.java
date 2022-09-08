package com.douyiyuan.first.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.douyiyuan.first.common.Constants;
import com.douyiyuan.first.common.ReleaseAnnotation;
import com.douyiyuan.first.common.Result;
import com.douyiyuan.first.common.StatusCode;
import com.douyiyuan.first.common.exception.ServiceException;
import com.douyiyuan.first.entity.User;
import com.douyiyuan.first.service.IUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.List;

import com.douyiyuan.first.service.IEmailVerificationService;
import com.douyiyuan.first.entity.EmailVerification;

import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2022-08-25
 */
@RestController
@RequestMapping("/emailVerification")
public class EmailVerificationController {

    @Resource
    private IEmailVerificationService emailVerificationService;

    @Resource
    private IUserService userService;

    @Resource
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    private static final Log LOGGER = Log.get();

    //发送邮箱验证码给用户,并将验证码存进数据库，设置验证过期时间
    @ReleaseAnnotation
    @Transactional
    @GetMapping("sendEmailCode/{email}/{type}")
    public Result sendEmailCode(@PathVariable("email") String email, @PathVariable("type")Integer type) throws MessagingException {
        if(StrUtil.isBlank(email)){
            throw new ServiceException(StatusCode.CODE_300,"请输入邮箱！");
        }

        //只有在user表找得到得email，才能往下走
//-------------------------------------------------------------------------------
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("email",email);
        User one;
        //找到多条的情况
        try{
            one = userService.getOne(userQueryWrapper);
        }catch (Exception e){
            LOGGER.error(e);
            throw new ServiceException(StatusCode.CODE_500,"系统错误");
        }
        //没有找到的情况
        if(one==null){
            throw new ServiceException(StatusCode.CODE_300,"未找到用户");
        }
//-------------------------------------------------------------------------------------------

        //为了避免用户反复点击获取验证码按钮，导致程序一直发送短信，导致资源得浪费，
        // 当查到当前邮箱在数据库的验证码还没过期的时候，抛业务异常，不让程序往下执行
        QueryWrapper<EmailVerification> emailVerificationQueryWrapper = new QueryWrapper<>();
        emailVerificationQueryWrapper.eq("email",email);
        emailVerificationQueryWrapper.eq("type",type);
        //下面这句代码比较难理解，ge是大于等于，sql语句的主体是数据库的记录的过期时间，而不是你给时间，
        // 当记录的过期时间大于等于now时，说明验证码还没过期，而不是拿now去比较记录的过期时间，要明白sql的主体时谁
        // 如果条件不满足，那查出来就是null
        emailVerificationQueryWrapper.ge("expire_time",LocalDateTime.now());
        EmailVerification dbEmailVerification = emailVerificationService.getOne(emailVerificationQueryWrapper);
        if(dbEmailVerification != null){
            throw new ServiceException(StatusCode.CODE_300,"最新的邮箱验证码尚未过期，请不要反复点击！");
        }

        //生成验证码
        String code = RandomUtil.randomNumbers(4);
        //将验证码存入数据库,先删后增，保证数据库用于登录的当前验证码只有一个
        //先删
        UpdateWrapper<EmailVerification> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("email",email);
        updateWrapper.eq("type",type);
        emailVerificationService.remove(updateWrapper);
        //后增
        EmailVerification emailVerification = new EmailVerification();
        emailVerification.setEmail(email);
        emailVerification.setCode(code);
        emailVerification.setType(type);
        LocalDateTime expireTime = LocalDateTime.now().plusMinutes(5);  //设置验证过期时间
        emailVerification.setExpireTime(expireTime);
        emailVerificationService.save(emailVerification);
        //发送邮箱
        if(type == Constants.LOGIN_BY_EMAIL){
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(from);  //是谁发送邮件，必要属性
            simpleMailMessage.setTo(email);   //邮件发送给谁，必要属性
            simpleMailMessage.setSubject("【XX公司】登录邮箱验证");   //邮件主题，必要属性
            simpleMailMessage.setText("您本次邮箱登录的验证码是："+ code + " ,有效期5分钟，请妥善保管，切勿泄露");      //邮件内容，必要属性
            //simpleMailMessage.setBcc();       //邮件密送给谁，非必要属性
            //simpleMailMessage.setCc();        //邮件抄送给谁，非必要属性
            javaMailSender.send(simpleMailMessage);
        }else if(type == Constants.FORGET_PASS){
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setFrom(from);
            helper.setTo(email);
            helper.setSubject("【XX公司】忘记密码邮箱验证");
            String message = "<b>尊敬的用户：</b><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                    "你好，您本次邮箱登录的验证码是：" +
                    "<b>"+ code + "</b><br>" +
                    ",有效期5分钟，请妥善保管，切勿泄露";
            helper.setText(message,true);
            javaMailSender.send(mimeMessage);
        }

        return Result.success(null);
    }


        /**
         * 查询所有
         * @return
         */
    @GetMapping
    public Result findAll() {
        return Result.success(emailVerificationService.list());
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(emailVerificationService.getById(id));
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
        QueryWrapper<EmailVerification> queryWrapper = new QueryWrapper<>();
        //queryWrapper加条件

        queryWrapper.orderByDesc("id");
        Page<EmailVerification> objectPage = new Page<>(pageNum, pageSize);
        Page<EmailVerification> page = emailVerificationService.page(objectPage, queryWrapper);
        return Result.success(page);
    }

    /**
     * 新增与修改
     * @param emailVerification
     * @return
     */
    @PostMapping("save")
    public Result save(@RequestBody EmailVerification emailVerification) {
        return Result.success(emailVerificationService.saveOrUpdate(emailVerification));
        }

    /**
     * 根据id删除
     * @param id
     * @return
     */
    @DeleteMapping("delete/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(emailVerificationService.removeById(id));
        }

    /**
     * 批量删除，因为前端的axios传 "[1,2,3]" 这样的参数只能用post方式来传，不能用delete方式来传
     * @param ids
     * @return
     */
    @PostMapping("removeBatchByIds")
    public Result removeBatchByIds(@RequestBody List<Integer> ids){
        return Result.success(emailVerificationService.removeBatchByIds(ids));
        }
}

