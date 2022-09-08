package com.douyiyuan.first.common.interceptor;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.douyiyuan.first.common.ReleaseAnnotation;
import com.douyiyuan.first.common.StatusCode;
import com.douyiyuan.first.common.exception.ServiceException;
import com.douyiyuan.first.entity.User;
import com.douyiyuan.first.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 前端请求必须带有token才能访问服务器，这个类是对request进行解析
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    IUserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");

        //被拦截的请求如果不是访问各种controller的方法，直接通过
        //虽然这样说，但前端的css或者js样式也不会放到后端项目里呀
        //不过老项目可能会，毕竟没用vue的时候，前端后端都在同一个项目里写代码,
        //像这种老项目就会把css放到resource包下，那么这个if判断就很有必要了
        if(!(handler instanceof HandlerMethod)){
            return true;
        }

        // 如果方法上有@ReleaseAnnotation注解，放行
        HandlerMethod method = (HandlerMethod) handler;
        if(method.hasMethodAnnotation(ReleaseAnnotation.class)){
            return true;
        }

        if(StrUtil.isBlank(token)){
            throw new ServiceException(StatusCode.CODE_300,"token不存在，请重新登录");
        }

        //从token获取之前在创建token时往Audience塞的数据，之前塞的是userid，也只塞了一个
        String userId = JWT.decode(token).getAudience().get(0);
        User user = userService.getById(userId.toString());

        if(user==null){
            throw new ServiceException(StatusCode.CODE_300,"用户不存在，请重新登录");
        }

        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();

        try {
            verifier.verify(token);
        }catch (Exception e){
            throw new ServiceException(StatusCode.CODE_300,"token验证不通过，请重新登录");
        }
        request.setAttribute("user",user);
        //当请求通过上面的代码验证走到这里时，放行请求
        return true;
    }
}
