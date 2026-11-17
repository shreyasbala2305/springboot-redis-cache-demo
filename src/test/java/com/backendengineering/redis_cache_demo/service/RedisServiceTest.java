
package com.backendengineering.redis_cache_demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class RedisServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RedisService redisService;

    @Test
    void shouldStoreValueWithTtl() {

        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);

        redisService.setValue("test:key", "hello", 60);

        verify(valueOperations).set(
                "test:key",
                "hello",
                Duration.ofSeconds(60)
        );
    }

    @Test
    void shouldRetrieveValue() {

        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);

        when(valueOperations.get("test:key"))
                .thenReturn("hello");

        String result = redisService.getValue("test:key");

        assertEquals("hello", result);
        verify(valueOperations).get("test:key");
    }

    @Test
    void shouldReturnTrueWhenKeyIsDeleted() {

        when(redisTemplate.delete("test:key"))
                .thenReturn(true);

        boolean result = redisService.deleteValue("test:key");

        assertTrue(result);
        verify(redisTemplate).delete("test:key");
    }

    @Test
    void shouldReturnFalseWhenKeyDoesNotExist() {

        when(redisTemplate.delete("missing:key"))
                .thenReturn(false);

        boolean result = redisService.deleteValue("missing:key");

        assertFalse(result);
        verify(redisTemplate).delete("missing:key");
    }
}
