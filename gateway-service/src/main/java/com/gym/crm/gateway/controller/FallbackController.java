package com.gym.crm.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping("/fallback")
public class FallbackController {
    private static final String SERVICE_UNAVAILABLE_MESSAGE = "%s Service is currently unavailable. Please try again later.";

    @RequestMapping(path = "/core", method = {GET, POST, PUT, PATCH, DELETE})
    public ResponseEntity<String> gymCoreFallback() {
        return ResponseEntity.ok(String.format(SERVICE_UNAVAILABLE_MESSAGE, "Gym Core"));
    }

    @RequestMapping(path = "/workload", method = {GET, POST, PUT, PATCH, DELETE})
    public ResponseEntity<String> workloadFallback() {
        return ResponseEntity.ok(String.format(SERVICE_UNAVAILABLE_MESSAGE, "Workload"));
    }
}
