package com.douyiyuan.first.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class WebSocketConfig {

    /**
     * 注入一个ServerEndpointExporter,该Bean会自动注册使用@ServerEndpoint注解申明的websocket endpoint
     * 也就是说只要有一个类有@ServerEndpoint注解，就会被作为websocket的业务处理类，注册进springboot中
     * @return
     */
    @Bean
    public ServerEndpointExporter createServerEndpointExporter(){
        return new ServerEndpointExporter();
    }
}
