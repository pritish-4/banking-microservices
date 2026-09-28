package com.banking.auth.dto;

import com.banking.auth.entity.Role;

public record UserProfileResponse(
        Long id,
        String username,
        String email,
        Role role
) {}
