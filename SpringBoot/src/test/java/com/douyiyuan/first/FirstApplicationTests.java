package com.douyiyuan.first;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RunWith(SpringRunner.class)
@SpringBootTest
class FirstApplicationTests {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Test
    public void testSelect() {
        System.out.println(redisTemplate);
    }

    @Test
    void contextLoads() {
    }

}
