package com.banking.auth.controller;

import com.banking.auth.dto.*;
import com.banking.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/auth")
@Validated
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegistrationRequest req) {
        log.info("POST /auth/register - username: {}", req.username());
        return ResponseEntity.ok(authService.registerUser(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        log.info("POST /auth/login - identifier: {}", req.usernameOrEmail());
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/admin/create-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthResponse> createUser(@Valid @RequestBody AdminRegistrationRequest req) {
        log.info("POST /auth/admin/create-user - username: {}", req.username());
        return ResponseEntity.ok(authService.createAdmin(req));
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMe() {
        return ResponseEntity.ok(authService.getMe());
    }

    @PutMapping("/me/password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        return ResponseEntity.ok(authService.changePassword(req));
    }

    @PutMapping("/me/email")
    public ResponseEntity<String> changeEmail(@Valid @RequestBody ChangeEmailRequest req) {
        return ResponseEntity.ok(authService.changeEmail(req));
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }

    @DeleteMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        log.info("DELETE /auth/admin/users/{}", id);
        authService.deactivateUser(id);
        return ResponseEntity.ok().build();
    }
}
