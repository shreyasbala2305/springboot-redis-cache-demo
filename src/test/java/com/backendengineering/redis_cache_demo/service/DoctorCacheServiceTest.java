
package com.backendengineering.redis_cache_demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.backendengineering.redis_cache_demo.model.Doctor;
import com.backendengineering.redis_cache_demo.repository.DoctorRepository;

@ExtendWith(MockitoExtension.class)
class DoctorCacheServiceTest {

    @Mock
    private RedisTemplate<String, Doctor> redisTemplate;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private ValueOperations<String, Doctor> valueOperations;

    @InjectMocks
    private DoctorCacheService doctorCacheService;

    private Doctor doctor;

    @BeforeEach
    void setUp() {
        doctor = new Doctor(
                101L,
                "Dr. Sharma",
                "Cardiology",
                800.0
        );
    }

    @Test
    void shouldReturnDoctorFromCacheWhenCacheHit() {

    	when(redisTemplate.opsForValue())
        .thenReturn(valueOperations);
    	
        when(valueOperations.get("cache:doctor:101"))
                .thenReturn(doctor);

        Doctor result = doctorCacheService.getDoctor(101L);

        assertSame(doctor, result);
        assertEquals(1, doctorCacheService.getCacheHits());
        assertEquals(0, doctorCacheService.getCacheMisses());

        verify(doctorRepository, never()).findById(101L);
    }

    @Test
    void shouldFetchDoctorFromRepositoryWhenCacheMiss() {

    	when(redisTemplate.opsForValue())
        .thenReturn(valueOperations);
    	
        when(valueOperations.get("cache:doctor:101"))
                .thenReturn(null);

        when(doctorRepository.findById(101L))
                .thenReturn(doctor);

        Doctor result = doctorCacheService.getDoctor(101L);

        assertSame(doctor, result);
        assertEquals(0, doctorCacheService.getCacheHits());
        assertEquals(1, doctorCacheService.getCacheMisses());

        verify(doctorRepository).findById(101L);

        verify(valueOperations).set(
                eq("cache:doctor:101"),
                eq(doctor),
                eq(Duration.ofSeconds(300))
        );
    }

    @Test
    void shouldReturnNullWhenDoctorDoesNotExist() {

    	when(redisTemplate.opsForValue())
        .thenReturn(valueOperations);
    	
        when(valueOperations.get("cache:doctor:999"))
                .thenReturn(null);

        when(doctorRepository.findById(999L))
                .thenReturn(null);

        Doctor result = doctorCacheService.getDoctor(999L);

        assertNull(result);
        assertEquals(1, doctorCacheService.getCacheMisses());

        verify(valueOperations, never())
                .set(
                        eq("cache:doctor:999"),
                        eq(doctor),
                        eq(Duration.ofSeconds(300))
                );
    }

    @Test
    void shouldUpdateDoctorAndInvalidateCache() {

        doctorCacheService.updateDoctor(doctor);

        verify(doctorRepository).update(doctor);

        verify(redisTemplate).delete("cache:doctor:101");
    }
}
