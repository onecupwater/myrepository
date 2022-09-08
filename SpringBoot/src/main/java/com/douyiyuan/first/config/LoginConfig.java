package com.douyiyuan.first.config;

import com.douyiyuan.first.common.interceptor.TokenInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 让spring服务器使用TokenInterceptor
 */

@Configuration
public class LoginConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getTokenInterceptor())
                .addPathPatterns("/**")   //让服务器拦截所有请求
                .excludePathPatterns("/user/login","/user/register","/**/import","/**/export","/file/**");    //让服务器放过哪些请求
    }

    @Bean
    public TokenInterceptor getTokenInterceptor(){
        return new TokenInterceptor();
    }
}
