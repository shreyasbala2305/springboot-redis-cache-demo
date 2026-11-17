
package com.backendengineering.redis_cache_demo.service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.backendengineering.redis_cache_demo.model.Doctor;
import com.backendengineering.redis_cache_demo.repository.DoctorRepository;

@Service
public class CachePerformanceService {

    private final DoctorRepository doctorRepository;
    private final RedisTemplate<String, Doctor> doctorRedisTemplate;
    private static final long BENCHMARK_CACHE_TTL_SECONDS = 300;
    private static final String CACHE_PREFIX = "cache:doctor:";

    public CachePerformanceService(
            DoctorRepository doctorRepository,
            RedisTemplate<String, Doctor> doctorRedisTemplate) {
        this.doctorRepository = doctorRepository;
        this.doctorRedisTemplate = doctorRedisTemplate;
    }

    public Map<String, Object> analyzePerformance(
            Long doctorId, int iterations) {

        if (iterations <= 0 || iterations > 10000) {
            throw new IllegalArgumentException(
                    "Iterations must be between 1 and 10000");
        }

        String cacheKey = CACHE_PREFIX + doctorId;

        // Ensure the Redis entry exists before benchmarking.
        Doctor doctor = doctorRepository.findById(doctorId);

        if (doctor == null) {
            throw new IllegalArgumentException("Doctor not found");
        }

        doctorRedisTemplate.opsForValue().set(
                cacheKey,
                doctor,
                BENCHMARK_CACHE_TTL_SECONDS,
                TimeUnit.SECONDS
        );

        // Warm-up
        for (int i = 0; i < 100; i++) {
            doctorRepository.findById(doctorId);
            doctorRedisTemplate.opsForValue().get(cacheKey);
        }

        // Repository benchmark
        long repositoryStart = System.nanoTime();

        for (int i = 0; i < iterations; i++) {
            doctorRepository.findById(doctorId);
        }

        long repositoryEnd = System.nanoTime();

        // Redis benchmark
        long redisStart = System.nanoTime();

        for (int i = 0; i < iterations; i++) {
            doctorRedisTemplate.opsForValue().get(cacheKey);
        }

        long redisEnd = System.nanoTime();

        double repositoryAverageMs =
                (repositoryEnd - repositoryStart)
                        / (double) iterations / 1_000_000;

        double redisAverageMs =
                (redisEnd - redisStart)
                        / (double) iterations / 1_000_000;

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("doctorId", doctorId);
        result.put("iterations", iterations);
        result.put("repositoryAverageMs", repositoryAverageMs);
        result.put("redisAverageMs", redisAverageMs);
        result.put("repositoryAccessType", "In-memory ConcurrentHashMap");
        result.put("redisAccessType", "Redis via RedisTemplate");

        return result;
    }
}
