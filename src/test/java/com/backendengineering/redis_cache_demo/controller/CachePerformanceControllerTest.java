
package com.backendengineering.redis_cache_demo.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.backendengineering.redis_cache_demo.service.CachePerformanceService;

@WebMvcTest(CachePerformanceController.class)
class CachePerformanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CachePerformanceService cachePerformanceService;

    @Test
    void shouldReturnPerformanceAnalysis() throws Exception {

        when(cachePerformanceService.analyzePerformance(101L, 1000))
                .thenReturn(Map.of(
                        "doctorId", 101L,
                        "iterations", 1000,
                        "repositoryAverageMs", 0.01,
                        "redisAverageMs", 0.5,
                        "repositoryAccessType", "In-memory ConcurrentHashMap",
                        "redisAccessType", "Redis via RedisTemplate"
                ));

        mockMvc.perform(get("/api/performance/cache")
                        .param("doctorId", "101")
                        .param("iterations", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctorId").value(101))
                .andExpect(jsonPath("$.iterations").value(1000))
                .andExpect(jsonPath("$.repositoryAverageMs").value(0.01))
                .andExpect(jsonPath("$.redisAverageMs").value(0.5))
                .andExpect(jsonPath("$.repositoryAccessType")
                        .value("In-memory ConcurrentHashMap"))
                .andExpect(jsonPath("$.redisAccessType")
                        .value("Redis via RedisTemplate"));

        verify(cachePerformanceService).analyzePerformance(101L, 1000);
    }

    @Test
    void shouldUseDefaultParametersWhenNotProvided() throws Exception {

        when(cachePerformanceService.analyzePerformance(101L, 1000))
                .thenReturn(Map.of(
                        "doctorId", 101L,
                        "iterations", 1000,
                        "repositoryAverageMs", 0.01,
                        "redisAverageMs", 0.5
                ));

        mockMvc.perform(get("/api/performance/cache"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctorId").value(101))
                .andExpect(jsonPath("$.iterations").value(1000));

        verify(cachePerformanceService).analyzePerformance(101L, 1000);
    }
}
