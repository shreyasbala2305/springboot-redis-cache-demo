package com.backendengineering.redis_cache_demo.service;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setValue(String key, String value, long ttlSeconds) {
        redisTemplate.opsForValue()
                .set(key, value, Duration.ofSeconds(ttlSeconds));
    }

    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean deleteValue(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }
}
