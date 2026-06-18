package com.gym.crm.workload.controller;

import com.gym.crm.workload.mapper.TrainerWorkloadMapper;
import com.gym.crm.workload.openapi.model.TrainerWorkloadRequest;
import com.gym.crm.workload.openapi.model.TrainerWorkloadResponse;
import com.gym.crm.workload.service.TrainerWorkloadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${app.api.base-path}/trainers/workload")
@RequiredArgsConstructor
public class TrainerWorkloadRestController {

    private final TrainerWorkloadService service;
    private final TrainerWorkloadMapper mapper;

    @GetMapping("/{username}")
    public ResponseEntity<TrainerWorkloadResponse> findTrainerWorkload(@PathVariable String username, @RequestParam Integer year, @RequestParam Integer month) {
        TrainerWorkloadResponse response = new TrainerWorkloadResponse();
        response.setTrainerUsername(username);
        response.month(month);
        response.setYear(year);
        response.setTrainingDuration(service.getWorkingHours(username, year, month));

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<TrainerWorkloadResponse> updateTrainerWorkload(@RequestBody @Valid TrainerWorkloadRequest request) {
        service.update(mapper.toUpdateDTO(request));

        return ResponseEntity.ok().build();
    }
}
