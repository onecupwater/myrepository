package com.douyiyuan.first.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.douyiyuan.first.entity.User;
import com.douyiyuan.first.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 使用jwt生成token
 *  生成的token由3部分组成： 标头 载荷  签证
 *  标头可以告诉服务器这个传回来token是使用什么算法加密的，token存活时间
 *  载荷，存储业务数据的
 *  签证，以标头的算法，结合载荷加key作为密钥，进行签名产生的第三部分string串
 */

@Component
public class TokenUtil {

    private static IUserService iUserService;

    @Autowired
    private IUserService userService;

    @PostConstruct
    public void setIUserService(){
        iUserService = userService;
    }

    public static String createToken (String userId,String password){

        String token = JWT.create()
                .withAudience(userId)  //往载荷塞数据
                .withExpiresAt(DateUtil.offsetHour(new Date(),6))  //设置token存活时间
                .sign(Algorithm.HMAC256(password));  //往签证塞密钥
        return token;
    }

    public static User getUser(){
        HttpServletRequest request =((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        if(StrUtil.isNotBlank(token)){
            try {
                String userId = JWT.decode(token).getAudience().get(0);
                return iUserService.getById(Integer.valueOf(userId));
            }catch (Exception e){}
             return null;
        }
        return null;
    }
}
