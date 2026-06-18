package com.gym.crm.workload.repository;

import com.gym.crm.workload.model.TrainerWorkload;

import java.util.Optional;

public interface TrainerWorkloadRepository {

    Optional<TrainerWorkload> findByTrainerUsername(String username);

    void save(TrainerWorkload trainerWorkload);
}
