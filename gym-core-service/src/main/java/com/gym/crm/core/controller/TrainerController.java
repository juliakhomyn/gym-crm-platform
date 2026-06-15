package com.gym.crm.core.controller;

import com.gia.openapi.model.ActivationStatusRequest;
import com.gia.openapi.model.TrainerCreateRequest;
import com.gia.openapi.model.TrainerCreateResponse;
import com.gia.openapi.model.TrainerGetResponse;
import com.gia.openapi.model.TrainerUpdateRequest;
import com.gia.openapi.model.TrainerUpdateResponse;
import com.gia.openapi.model.GetTrainerTrainingResponse;
import com.gym.crm.core.facade.GymFacade;
import com.gym.crm.core.search.filter.TrainerTrainingFilter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("${app.api.base-path}/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final GymFacade facade;

    @PostMapping("/register")
    public ResponseEntity<TrainerCreateResponse> register(@RequestBody @Valid TrainerCreateRequest request) {
        TrainerCreateResponse response = facade.createTrainer(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}")
    public ResponseEntity<TrainerGetResponse> getTrainerProfile(@PathVariable(name = "username") String username) {
        TrainerGetResponse response = facade.getTrainerByUsername(username);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{username}")
    public ResponseEntity<TrainerUpdateResponse> updateTrainerProfile(@PathVariable(name = "username") String username,
                                                                      @RequestBody @Valid TrainerUpdateRequest request) {
        TrainerUpdateResponse response = facade.updateTrainer(request, username);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{username}/activation")
    public ResponseEntity<Void> toggleActive(@PathVariable(name = "username") String username,
                                             @RequestBody @Valid ActivationStatusRequest request) {
        facade.toggleActiveStatus(request, username);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<GetTrainerTrainingResponse>> getTrainerTrainings(@PathVariable(name = "username") String username,
                                                                                @RequestParam(name = "fromDate", required = false)
                                                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                                                @RequestParam(name = "toDate", required = false)
                                                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                                                                @RequestParam(name = "traineeName", required = false) String traineeName) {
        TrainerTrainingFilter filter = TrainerTrainingFilter.builder()
                .username(username)
                .fromDate(fromDate)
                .toDate(toDate)
                .joinFullName(traineeName)
                .build();
        List<GetTrainerTrainingResponse> response = facade.getTrainerTrainingsByFilter(filter);

        return ResponseEntity.ok(response);
    }
}
