
package com.backendengineering.redis_cache_demo.service;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.backendengineering.redis_cache_demo.model.Doctor;
import com.backendengineering.redis_cache_demo.repository.DoctorRepository;

@Service
public class DoctorCacheService {

    private static final String CACHE_PREFIX = "cache:doctor:";
    private static final long CACHE_TTL_SECONDS = 300;

    private final RedisTemplate<String, Doctor> redisTemplate;
    private final DoctorRepository doctorRepository;

    private long cacheHits;
    private long cacheMisses;

    public DoctorCacheService(
            RedisTemplate<String, Doctor> redisTemplate,
            DoctorRepository doctorRepository) {

        this.redisTemplate = redisTemplate;
        this.doctorRepository = doctorRepository;
    }

    public Doctor getDoctor(Long id) {

        String cacheKey = CACHE_PREFIX + id;

        // 1. Check Redis cache
        Doctor cachedDoctor = redisTemplate.opsForValue().get(cacheKey);

        if (cachedDoctor != null) {
            cacheHits++;
            System.out.println("CACHE HIT: " + cacheKey);
            return cachedDoctor;
        }

        // 2. Cache miss
        cacheMisses++;
        System.out.println("CACHE MISS: " + cacheKey);

        // 3. Fetch from repository
        Doctor doctor = doctorRepository.findById(id);

        if (doctor == null) {
            return null;
        }

        // 4. Store in Redis with TTL
        redisTemplate.opsForValue().set(
                cacheKey,
                doctor,
                Duration.ofSeconds(CACHE_TTL_SECONDS)
        );

        System.out.println("CACHE WRITE: " + cacheKey);

        return doctor;
    }

    public Doctor updateDoctor(Doctor doctor) {

        doctorRepository.update(doctor);

        String cacheKey = CACHE_PREFIX + doctor.getId();

        // Invalidate stale cache
        redisTemplate.delete(cacheKey);

        System.out.println("CACHE INVALIDATED: " + cacheKey);

        return doctor;
    }

    public boolean evictDoctor(Long id) {
        String cacheKey = CACHE_PREFIX + id;
        return Boolean.TRUE.equals(redisTemplate.delete(cacheKey));
    }

    public long getCacheHits() {
        return cacheHits;
    }

    public long getCacheMisses() {
        return cacheMisses;
    }
}
