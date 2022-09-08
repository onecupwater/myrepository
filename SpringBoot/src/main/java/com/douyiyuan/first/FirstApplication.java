package com.douyiyuan.first;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;

@SpringBootApplication
public class FirstApplication {

    public static void main(String[] args) {
        SpringApplication.run(FirstApplication.class, args);
    }

    @Bean
    public MultipartConfigElement multipartConfigElement(
            @Value("${multipart.maxFileSize}")Long maxFileSize,
            @Value("${multipart.maxRequestSize}") Long maxRequestSize) {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(maxFileSize));
        factory.setMaxRequestSize(DataSize.ofMegabytes(maxRequestSize));
        return factory.createMultipartConfig();
    }
}
