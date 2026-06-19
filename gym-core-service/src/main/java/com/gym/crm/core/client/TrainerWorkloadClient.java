package com.gym.crm.core.client;

import com.gia.openapi.model.TrainerWorkloadRequest;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/trainers/workload")
public interface TrainerWorkloadClient {

    @PostExchange
    void updateTrainerWorkload(@RequestBody TrainerWorkloadRequest request);
}
