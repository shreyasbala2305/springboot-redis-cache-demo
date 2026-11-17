
package com.backendengineering.redis_cache_demo.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backendengineering.redis_cache_demo.service.CachePerformanceService;

@RestController
@RequestMapping("/api/performance")
public class CachePerformanceController {

    private final CachePerformanceService performanceService;

    public CachePerformanceController(
            CachePerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @GetMapping("/cache")
    public Map<String, Object> analyze(
            @RequestParam(defaultValue = "101") Long doctorId,
            @RequestParam(defaultValue = "1000") int iterations) {

        return performanceService.analyzePerformance(
                doctorId, iterations);
    }
}
