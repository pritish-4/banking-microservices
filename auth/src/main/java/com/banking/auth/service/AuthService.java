package com.banking.auth.service;

import com.banking.auth.dto.*;
import com.banking.auth.entity.Role;
import com.banking.auth.entity.User;
import com.banking.auth.event.AuthEvent;
import com.banking.auth.event.AuthEventPublisher;
import com.banking.auth.exception.UserAlreadyExistsException;
import com.banking.auth.repo.UserRepo;
import com.banking.auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final AuthEventPublisher eventPublisher;

    public AuthResponse registerUser(RegistrationRequest request) {
        log.info("Registering new user: {}", request.username());
        validateNewUser(request.username(), request.email());

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.CUSTOMER);
        userRepo.save(user);

        log.info("User registered successfully: {}", request.username());
        return new AuthResponse(jwtUtil.generateToken(user), Role.CUSTOMER, "Bearer");
    }

    @PreAuthorize("hasRole('ADMIN')")
    public AuthResponse createAdmin(AdminRegistrationRequest request) {
        log.info("Admin creating user: {} with role: {}", request.username(), request.role());
        validateNewUser(request.username(), request.email());

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        userRepo.save(user);

        log.info("User created by admin: {} with role: {}", request.username(), request.role());
        return new AuthResponse(jwtUtil.generateToken(user), request.role(), "Bearer");
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for: {}", request.usernameOrEmail());
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.usernameOrEmail(), request.password())
        );

        User user = userRepo.findByUsernameOrEmail(request.usernameOrEmail(), request.usernameOrEmail()).orElseThrow();
        log.info("Login successful for: {}", request.usernameOrEmail());
        return new AuthResponse(jwtUtil.generateToken(user), user.getRole(), "Bearer");
    }

    public UserProfileResponse getMe() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new UserProfileResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }

    public String changePassword(ChangePasswordRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepo.save(user);
        log.info("Password changed for user: {}", username);
        eventPublisher.publish(new AuthEvent(user.getId(), user.getUsername(), "PASSWORD_CHANGED", LocalDateTime.now()));
        return "Password changed successfully. Please log in again with your new password.";
    }

    public String changeEmail(ChangeEmailRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (userRepo.existsByEmail(request.newEmail())) {
            throw new UserAlreadyExistsException("Email already in use");
        }

        user.setEmail(request.newEmail());
        userRepo.save(user);
        log.info("Email changed for user: {}", username);
        eventPublisher.publish(new AuthEvent(user.getId(), user.getUsername(), "EMAIL_CHANGED", LocalDateTime.now()));
        return "Email updated successfully to " + request.newEmail();
    }

    public List<UserProfileResponse> getAllUsers() {
        log.info("Admin fetching all users");
        return userRepo.findAll().stream()
                .map(u -> new UserProfileResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole()))
                .toList();
    }

    public void deactivateUser(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + id));
        user.setEnabled(false);
        userRepo.save(user);
        log.info("User deactivated: {}", id);
    }

    private void validateNewUser(String username, String email) {
        if (userRepo.existsByUsername(username)) {
            log.warn("Registration failed - username already exists: {}", username);
            throw new UserAlreadyExistsException("Username already exists");
        }
        if (userRepo.existsByEmail(email)) {
            log.warn("Registration failed - email already exists: {}", email);
            throw new UserAlreadyExistsException("Email already exists");
        }
    }
}
