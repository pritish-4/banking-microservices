package com.banking.auth.dto;

import com.banking.auth.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AdminRegistrationRequest(
        @NotBlank
        @Size(min = 3)
        String username,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 3)
        String password,

        @NotNull
        Role role
) {
}
