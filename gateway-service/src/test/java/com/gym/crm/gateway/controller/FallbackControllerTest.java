package com.gym.crm.gateway.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class FallbackControllerTest {

    @Autowired
    private WebTestClient client;

    @Test
    void gymCoreFallback_shouldReturnFallbackResponse() {
        client.get()
                .uri("/fallback/core")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo(3503)
                .jsonPath("$.message")
                .isEqualTo("Gym Core Service is currently unavailable. Please try again later.");
    }

    @Test
    void workloadFallback_shouldReturnFallbackResponse() {
        client.get()
                .uri("/fallback/workload")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo(3503)
                .jsonPath("$.message")
                .isEqualTo("Workload Service is currently unavailable. Please try again later.");
    }
}
