
package com.backendengineering.redis_cache_demo.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.backendengineering.redis_cache_demo.model.Doctor;
import com.backendengineering.redis_cache_demo.service.DoctorCacheService;

@WebMvcTest(DoctorController.class)
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorCacheService doctorCacheService;

    @Test
    void shouldReturnDoctor() throws Exception {

        Doctor doctor = new Doctor(
                101L, "Dr. Sharma", "Cardiology", 800.0);

        when(doctorCacheService.getDoctor(101L))
                .thenReturn(doctor);

        mockMvc.perform(get("/api/doctors/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(101))
                .andExpect(jsonPath("$.name").value("Dr. Sharma"))
                .andExpect(jsonPath("$.consultationFee").value(800.0));
    }

    @Test
    void shouldReturn404WhenDoctorNotFound() throws Exception {

        when(doctorCacheService.getDoctor(999L))
                .thenReturn(null);

        mockMvc.perform(get("/api/doctors/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateDoctor() throws Exception {

        Doctor doctor = new Doctor(
                101L, "Dr. Sharma", "Cardiology", 1500.0);

        when(doctorCacheService.updateDoctor(any(Doctor.class)))
                .thenReturn(doctor);

        mockMvc.perform(put("/api/doctors/101")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "id": 101,
                            "name": "Dr. Sharma",
                            "specialization": "Cardiology",
                            "consultationFee": 1500.0
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.consultationFee").value(1500.0));
    }

    @Test
    void shouldReturn400WhenDoctorIdDoesNotMatch() throws Exception {

        mockMvc.perform(put("/api/doctors/101")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "id": 102,
                            "name": "Dr. Mehta",
                            "specialization": "Dermatology",
                            "consultationFee": 600.0
                        }
                        """))
                .andExpect(status().isBadRequest());

        verify(doctorCacheService, never())
                .updateDoctor(any(Doctor.class));
    }

    @Test
    void shouldEvictDoctorCache() throws Exception {

        when(doctorCacheService.evictDoctor(101L))
                .thenReturn(true);

        mockMvc.perform(delete("/api/doctors/101/cache"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctorId").value(101))
                .andExpect(jsonPath("$.cacheEvicted").value(true));
    }

    @Test
    void shouldReturnCacheStatistics() throws Exception {

        when(doctorCacheService.getCacheHits()).thenReturn(10L);
        when(doctorCacheService.getCacheMisses()).thenReturn(5L);

        mockMvc.perform(get("/api/doctors/cache/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cacheHits").value(10))
                .andExpect(jsonPath("$.cacheMisses").value(5));
    }
}
