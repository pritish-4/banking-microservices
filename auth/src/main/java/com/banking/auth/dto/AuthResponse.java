package com.banking.auth.dto;

import com.banking.auth.entity.Role;

public record AuthResponse(
        String accessToken,
        Role role,
        String tokenType
) {
}
