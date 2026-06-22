package com.gym.crm.gateway.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class FallbackControllerTest {

    @Autowired
    private WebTestClient client;

    @Test
    void gymCoreFallback_shouldReturnOk_withFallbackMessage() {
        client.get()
                .uri("/fallback/core")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Gym Core Service is currently unavailable. Please try again later.");
    }

    @Test
    void workloadFallback_shouldReturnOk_withFallbackMessage() {
        client.get()
                .uri("/fallback/workload")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Workload Service is currently unavailable. Please try again later.");
    }
}
