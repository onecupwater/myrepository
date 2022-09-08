package com.douyiyuan.first.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 这个类是演示单独使用redis所必须的配置类，但与现在的整个系统无关，所以要注释掉
 */
//@Configuration
public class YourRedisConfig {
    /**
     * 自定义RedisTemplate对redis中的各种value的序列化规则
     * Redis默认使用JdkSerializationRedisSerializer序列化方式
     */
    //@Bean
    public RedisTemplate redisTemplate (RedisConnectionFactory factory){
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    /**
     * 对 String 数据类型的操作
     *
     * @param redisTemplate
     * @return
     */
    //@Bean
    public ValueOperations<String, Object> valueOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    /**
     * 对 hash 数据类型的操作
     *
     * @param redisTemplate
     * @return
     */
    //@Bean
    public HashOperations<String, String, Object> hashOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForHash();
    }

    /**
     * 对 list链表 数据类型的操作
     *
     * @param redisTemplate
     * @return
     */
    //@Bean
    public ListOperations<String, Object> listOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForList();
    }

    /**
     * 对 Set(无序集合) 数据类型的操作
     *
     * @param redisTemplate
     * @return
     */
    //@Bean
    public SetOperations<String, Object> setOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForSet();
    }

    /**
     * 对 ZSet（有序集合） 数据类型的操作
     *
     * @param redisTemplate
     * @return
     */
    //@Bean
    public ZSetOperations<String, Object> zSetOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForZSet();
    }
}
