
package com.backendengineering.redis_cache_demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.backendengineering.redis_cache_demo.model.Doctor;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Doctor> doctorRedisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, Doctor> template = new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());

        template.setValueSerializer(
                new JacksonJsonRedisSerializer<>(Doctor.class));

        template.afterPropertiesSet();

        return template;
    }
}
