package com.gym.crm.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {
    private static final String SERVICE_UNAVAILABLE_MESSAGE = "%s Service is currently unavailable. Please try again later.";

    @RequestMapping("/core")
    public ResponseEntity<String> gymCoreFallback() {
        return ResponseEntity.ok(String.format(SERVICE_UNAVAILABLE_MESSAGE, "Gym Core"));
    }

    @RequestMapping("/workload")
    public ResponseEntity<String> workloadFallback() {
        return ResponseEntity.ok(String.format(SERVICE_UNAVAILABLE_MESSAGE, "Workload"));
    }
}
