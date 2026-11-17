
package com.backendengineering.redis_cache_demo.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.backendengineering.redis_cache_demo.service.RedisService;

@WebMvcTest(RedisController.class)
class RedisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RedisService redisService;

    @Test
    void shouldStoreValue() throws Exception {

        mockMvc.perform(post("/api/redis/test-key")
                .param("value", "hello")
                .param("ttlSeconds", "60"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("test-key"))
                .andExpect(jsonPath("$.message")
                        .value("Value stored successfully"));

        verify(redisService).setValue("test-key", "hello", 60);
    }

    @Test
    void shouldUseDefaultTtlWhenNotProvided() throws Exception {

        mockMvc.perform(post("/api/redis/test-key")
                .param("value", "hello"))
                .andExpect(status().isOk());

        verify(redisService).setValue("test-key", "hello", 300);
    }

    @Test
    void shouldRejectInvalidTtl() throws Exception {

        mockMvc.perform(post("/api/redis/test-key")
                .param("value", "hello")
                .param("ttlSeconds", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("TTL must be greater than zero"));

        verify(redisService, never())
                .setValue(anyString(), anyString(), anyLong());
    }

    @Test
    void shouldRetrieveValue() throws Exception {

        when(redisService.getValue("test-key"))
                .thenReturn("hello");

        mockMvc.perform(get("/api/redis/test-key"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("test-key"))
                .andExpect(jsonPath("$.value").value("hello"));
    }

    @Test
    void shouldReturn404WhenValueDoesNotExist() throws Exception {

        when(redisService.getValue("missing-key"))
                .thenReturn(null);

        mockMvc.perform(get("/api/redis/missing-key"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteValue() throws Exception {

        when(redisService.deleteValue("test-key"))
                .thenReturn(true);

        mockMvc.perform(delete("/api/redis/test-key"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("test-key"))
                .andExpect(jsonPath("$.message")
                        .value("Key deleted successfully"));
    }

    @Test
    void shouldReturn404WhenDeletingMissingKey() throws Exception {

        when(redisService.deleteValue("missing-key"))
                .thenReturn(false);

        mockMvc.perform(delete("/api/redis/missing-key"))
                .andExpect(status().isNotFound());
    }
}
