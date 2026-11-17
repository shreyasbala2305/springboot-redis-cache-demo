
package com.backendengineering.redis_cache_demo.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.backendengineering.redis_cache_demo.model.Doctor;
import com.backendengineering.redis_cache_demo.service.DoctorCacheService;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorCacheService doctorCacheService;

    public DoctorController(DoctorCacheService doctorCacheService) {
        this.doctorCacheService = doctorCacheService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctor(@PathVariable Long id) {

        Doctor doctor = doctorCacheService.getDoctor(id);

        if (doctor == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(doctor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Doctor> updateDoctor(
            @PathVariable Long id,
            @RequestBody Doctor doctor) {

        if (doctor.getId() == null || !id.equals(doctor.getId())) {
            return ResponseEntity.badRequest().build();
        }

        Doctor updatedDoctor = doctorCacheService.updateDoctor(doctor);

        return ResponseEntity.ok(updatedDoctor);
    }

    @DeleteMapping("/{id}/cache")
    public ResponseEntity<Map<String, Object>> evictCache(
            @PathVariable Long id) {

        boolean deleted = doctorCacheService.evictDoctor(id);

        return ResponseEntity.ok(
                Map.of(
                        "doctorId", id,
                        "cacheEvicted", deleted
                )
        );
    }

    @GetMapping("/cache/stats")
    public ResponseEntity<Map<String, Long>> getCacheStats() {

        return ResponseEntity.ok(
                Map.of(
                        "cacheHits", doctorCacheService.getCacheHits(),
                        "cacheMisses", doctorCacheService.getCacheMisses()
                )
        );
    }
}
