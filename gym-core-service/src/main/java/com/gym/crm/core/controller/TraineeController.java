package com.gym.crm.core.controller;

import com.gia.openapi.model.ActivationStatusRequest;
import com.gia.openapi.model.AssignedTrainerResponse;
import com.gia.openapi.model.TraineeAssignedTrainersUpdateRequest;
import com.gia.openapi.model.TraineeAssignedTrainersUpdateResponse;
import com.gia.openapi.model.TraineeCreateRequest;
import com.gia.openapi.model.TraineeCreateResponse;
import com.gia.openapi.model.TraineeGetResponse;
import com.gia.openapi.model.TraineeUpdateRequest;
import com.gia.openapi.model.TraineeUpdateResponse;
import com.gia.openapi.model.GetTraineeTrainingResponse;
import com.gym.crm.core.facade.GymFacade;
import com.gym.crm.core.search.filter.TraineeTrainingFilter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("${app.api.base-path}/trainees")
@RequiredArgsConstructor
public class TraineeController {

    private final GymFacade facade;

    @PostMapping("/register")
    public ResponseEntity<TraineeCreateResponse> register(@RequestBody @Valid TraineeCreateRequest request) {
        TraineeCreateResponse response = facade.createTrainee(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}")
    public ResponseEntity<TraineeGetResponse> getTraineeProfile(@PathVariable(name = "username") String username) {
        TraineeGetResponse response = facade.getTraineeByUsername(username);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{username}")
    public ResponseEntity<TraineeUpdateResponse> updateTraineeProfile(@PathVariable(name = "username") String username,
                                                                      @RequestBody @Valid TraineeUpdateRequest request) {
        TraineeUpdateResponse response = facade.updateTrainee(request, username);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTrainee(@PathVariable(name = "username") String username) {
        facade.deleteTraineeByUsername(username);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{username}/activation")
    public ResponseEntity<Void> toggleActive(@PathVariable(name = "username") String username,
                                             @RequestBody @Valid ActivationStatusRequest request) {
        facade.toggleActiveStatus(request, username);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{username}/trainers")
    public ResponseEntity<TraineeAssignedTrainersUpdateResponse> updateTraineeTrainers(@PathVariable(name = "username") String username,
                                                                                       @RequestBody @Valid TraineeAssignedTrainersUpdateRequest request) {
        TraineeAssignedTrainersUpdateResponse response = facade.updateTraineeTrainersList(request, username);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}/available-trainers")
    public ResponseEntity<List<AssignedTrainerResponse>> getAvailableTrainers(@PathVariable(name = "username") String username) {
        List<AssignedTrainerResponse> response = facade.getTrainersNotAssignedToTrainee(username);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<GetTraineeTrainingResponse>> getTraineeTrainings(@PathVariable(name = "username") String username,
                                                                                @RequestParam(name = "fromDate", required = false)
                                                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                                                @RequestParam(name = "toDate", required = false)
                                                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                                                                @RequestParam(name = "trainerName", required = false) String trainerName,
                                                                                @RequestParam(name = "trainingType", required = false) String trainingType) {
        TraineeTrainingFilter filter = TraineeTrainingFilter.builder()
                .username(username)
                .fromDate(fromDate)
                .toDate(toDate)
                .joinFullName(trainerName)
                .trainingTypeName(trainingType)
                .build();
        List<GetTraineeTrainingResponse> response = facade.getTraineeTrainingsByFilter(filter);

        return ResponseEntity.ok(response);
    }
}
