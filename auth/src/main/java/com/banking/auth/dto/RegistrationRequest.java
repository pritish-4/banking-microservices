package com.banking.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(
        @NotBlank
        @Size(min = 3)
        String username,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 3)
        String password
) {
}
