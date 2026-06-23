package com.gym.crm.workload.service.impl;

import com.gym.crm.workload.dto.ActionType;
import com.gym.crm.workload.dto.TrainerWorkloadUpdateDTO;
import com.gym.crm.workload.exception.EntityNotFoundException;
import com.gym.crm.workload.model.MonthWorkload;
import com.gym.crm.workload.model.TrainerWorkload;
import com.gym.crm.workload.model.YearWorkload;
import com.gym.crm.workload.repository.TrainerWorkloadRepository;
import com.gym.crm.workload.service.TrainerWorkloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.gym.crm.workload.dto.ActionType.DELETE;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerWorkloadServiceImpl implements TrainerWorkloadService {

    private final TrainerWorkloadRepository repository;

    @Override
    public Integer getWorkingHours(String username, int year, int month) {
        log.info("Getting working hours for trainer: username={}, year={}, month={}", username, year, month);

        return findYearWorkload(findByUsername(username), year)
                .flatMap(yw -> findMonthWorkload(yw, month))
                .map(MonthWorkload::getTrainingDuration)
                .orElse(0);
    }

    @Override
    public void update(TrainerWorkloadUpdateDTO dto) {
        log.info("Updating workload for trainer: username={}, action={}, date={}, duration={}",
                dto.getTrainerUsername(), dto.getActionType(), dto.getTrainingDate(), dto.getTrainingDuration());

        Optional<TrainerWorkload> existing = repository.findByTrainerUsername(dto.getTrainerUsername());
        if (existing.isEmpty() && dto.getActionType() == DELETE) {
            log.warn("Trying to delete non-existent workload for trainer: username={}", dto.getTrainerUsername());
            return;
        }

        TrainerWorkload workload = existing.map(w -> updateWorkload(w, dto)).orElseGet(() -> buildWorkload(dto));
        updateMonthWorkload(workload, dto);

        repository.save(workload);
        log.info("Workload updated successfully for trainer: username={}", dto.getTrainerUsername());
    }

    private TrainerWorkload findByUsername(String username) {
        return repository.findByTrainerUsername(username).orElseThrow(
                () -> new EntityNotFoundException("Trainer workload not found by username: " + username));
    }

    private Optional<YearWorkload> findYearWorkload(TrainerWorkload workload, int year) {
        return workload.getYears().stream()
                .filter(w -> w.getYear() == year)
                .findFirst();
    }

    private Optional<MonthWorkload> findMonthWorkload(YearWorkload workload, int month) {
        return workload.getMonths().stream()
                .filter(w -> w.getMonth() == month)
                .findFirst();
    }

    private TrainerWorkload buildWorkload(TrainerWorkloadUpdateDTO dto) {
        return TrainerWorkload.builder().
                trainerUsername(dto.getTrainerUsername())
                .trainerFirstName(dto.getTrainerFirstName())
                .trainerLastName(dto.getTrainerLastName())
                .isActive(dto.getIsActive())
                .build();
    }

    private TrainerWorkload updateWorkload(TrainerWorkload existing, TrainerWorkloadUpdateDTO dto) {
        return existing.toBuilder()
                .trainerFirstName(dto.getTrainerFirstName())
                .trainerLastName(dto.getTrainerLastName())
                .isActive(dto.getIsActive())
                .build();
    }

    private YearWorkload buildYearWorkload(int year) {
        return YearWorkload.builder()
                .year(year)
                .build();
    }

    private MonthWorkload buildMonthWorkload(int month) {
        return MonthWorkload.builder()
                .month(month)
                .trainingDuration(0)
                .build();
    }

    private YearWorkload getOrCreateYearWorkload(TrainerWorkload workload, int year) {
        return workload.getYears().stream()
                .filter(yw -> yw.getYear() == year)
                .findFirst()
                .orElseGet(() -> buildAndAddYear(workload, year));
    }

    private MonthWorkload getOrCreateMonthWorkload(YearWorkload yearWorkload, int month) {
        return yearWorkload.getMonths().stream()
                .filter(mw -> mw.getMonth() == month)
                .findFirst()
                .orElseGet(() -> buildAndAddMonthWorkload(yearWorkload, month));
    }

    private YearWorkload buildAndAddYear(TrainerWorkload workload, int year) {
        YearWorkload yw = buildYearWorkload(year);
        workload.getYears().add(yw);

        return yw;
    }

    private MonthWorkload buildAndAddMonthWorkload(YearWorkload yearWorkload, int month) {
        MonthWorkload mw = buildMonthWorkload(month);
        yearWorkload.getMonths().add(mw);

        return mw;
    }

    private void updateMonthWorkload(TrainerWorkload workload, TrainerWorkloadUpdateDTO dto) {
        int year = dto.getTrainingDate().getYear();
        int month = dto.getTrainingDate().getMonthValue();
        ActionType action = dto.getActionType();

        YearWorkload yearWorkload = getOrCreateYearWorkload(workload, year);
        MonthWorkload monthWorkload = getOrCreateMonthWorkload(yearWorkload, month);

        int currentDuration = monthWorkload.getTrainingDuration();

        if (action == DELETE) {
            if (currentDuration == 0) {
                log.warn("Attempt to delete workload from empty month: username={}, year={}, month={}, requestedDuration={}",
                        dto.getTrainerUsername(), year, month, dto.getTrainingDuration());
            } else if (dto.getTrainingDuration() > currentDuration) {
                log.warn("Attempt to delete more workload than exists: username={}, year={}, month={}, currentDuration={}, requestedDuration={}",
                        dto.getTrainerUsername(), year, month, currentDuration, dto.getTrainingDuration());
            }
        }

        int newDuration = switch (action) {
            case ADD -> currentDuration + dto.getTrainingDuration();
            case DELETE -> Math.max(0, currentDuration - dto.getTrainingDuration());
        };

        yearWorkload.getMonths().removeIf(mw -> mw.getMonth() == month);
        if (newDuration > 0) {
            yearWorkload.getMonths().add(MonthWorkload.builder()
                    .month(month)
                    .trainingDuration(newDuration)
                    .build());
        }
        removeEmptyYearWorkload(workload, yearWorkload);
    }

    private void removeEmptyYearWorkload(TrainerWorkload workload, YearWorkload yearWorkload) {
        if (yearWorkload.getMonths().isEmpty()) {
            workload.getYears().removeIf(yw -> yw.getYear() == yearWorkload.getYear());
        }
    }
}
