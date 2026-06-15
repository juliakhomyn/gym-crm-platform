package com.gym.crm.core.service;

import com.gym.crm.core.facade.dto.common.PasswordChangeRequest;
import com.gym.crm.core.facade.dto.common.ToggleActiveRequestDTO;
import com.gym.crm.core.facade.dto.validation.ValidId;
import com.gym.crm.core.facade.dto.validation.ValidUsername;
import com.gym.crm.core.model.User;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface UserService {
    User getByUsername(@ValidUsername String username);

    User getById(@ValidId Long id);

    List<User> getAll();

    void changePassword(@Valid PasswordChangeRequest request);

    void toggleActive(@Valid ToggleActiveRequestDTO request);
}
