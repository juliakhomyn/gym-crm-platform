package com.gym.crm.workload.model;

import com.gym.crm.workload.dto.validation.Username;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@CompoundIndex(name = "trainer_name_index", def = "{'first_name' : 1, 'last_name': 1}")
@Document(collection = "trainer_workloads")
public class TrainerWorkload {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field(name = "username")
    @Username
    private String trainerUsername;

    @Field(name = "first_name")
    @NotBlank
    private String trainerFirstName;

    @Field(name = "last_name")
    @NotBlank
    private String trainerLastName;

    @Field(name = "is_active")
    @NotNull
    private Boolean isActive;

    @Field(name = "years")
    @Builder.Default
    @NotNull
    private final List<YearWorkload> years = new ArrayList<>();
}
