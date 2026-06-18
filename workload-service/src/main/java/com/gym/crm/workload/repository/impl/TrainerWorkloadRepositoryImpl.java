package com.gym.crm.workload.repository.impl;

import com.gym.crm.workload.model.TrainerWorkload;
import com.gym.crm.workload.repository.TrainerWorkloadRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TrainerWorkloadRepositoryImpl implements TrainerWorkloadRepository {

    private final Map<String, TrainerWorkload> workloads = new ConcurrentHashMap<>();

    @Override
    public Optional<TrainerWorkload> findByTrainerUsername(String username) {
        return Optional.ofNullable(workloads.get(username));
    }

    @Override
    public void save(TrainerWorkload workload) {
        workloads.put(workload.getTrainerUsername(), workload);
    }
}
