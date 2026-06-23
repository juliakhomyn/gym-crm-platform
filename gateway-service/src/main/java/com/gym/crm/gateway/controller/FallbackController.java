package com.gym.crm.gateway.controller;

import com.gym.crm.gateway.dto.FallbackResponse;
import org.springframework.http.HttpStatus;
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
    private static final Integer ERROR_CODE = 3503;

    @RequestMapping(path = "/core", method = {GET, POST, PUT, PATCH, DELETE})
    public ResponseEntity<FallbackResponse> gymCoreFallback() {
        return buildFallbackResponse("Gym Core");
    }

    @RequestMapping(path = "/workload", method = {GET, POST, PUT, PATCH, DELETE})
    public ResponseEntity<FallbackResponse> workloadFallback() {
        return buildFallbackResponse("Workload");
    }

    private ResponseEntity<FallbackResponse> buildFallbackResponse(String serviceName) {
        FallbackResponse response = FallbackResponse.builder()
                .errorCode(ERROR_CODE)
                .message(String.format(SERVICE_UNAVAILABLE_MESSAGE, serviceName))
                .build();

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}
