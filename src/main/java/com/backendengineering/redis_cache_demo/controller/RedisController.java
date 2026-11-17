
package com.backendengineering.redis_cache_demo.controller;

import java.util.Map;

import com.backendengineering.redis_cache_demo.service.RedisService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/redis")
public class RedisController {

    private final RedisService redisService;

    public RedisController(RedisService redisService) {
        this.redisService = redisService;
    }

    @PostMapping("/{key}")
    public ResponseEntity<Map<String, String>> setValue(
            @PathVariable String key,
            @RequestParam String value,
            @RequestParam(defaultValue = "300") long ttlSeconds) {

        if (ttlSeconds <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "TTL must be greater than zero"));
        }

        redisService.setValue(key, value, ttlSeconds);

        return ResponseEntity.ok(
                Map.of("message", "Value stored successfully", "key", key)
        );
    }

    @GetMapping("/{key}")
    public ResponseEntity<Map<String, String>> getValue(
            @PathVariable String key) {

        String value = redisService.getValue(key);

        if (value == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                Map.of("key", key, "value", value)
        );
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Map<String, String>> deleteValue(
            @PathVariable String key) {

        boolean deleted = redisService.deleteValue(key);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                Map.of("message", "Key deleted successfully", "key", key)
        );
    }
}
