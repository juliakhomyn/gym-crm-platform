package com.gym.crm.testing.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisteredUser {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
}
