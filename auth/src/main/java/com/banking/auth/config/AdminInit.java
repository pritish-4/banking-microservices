package com.banking.auth.config;

import com.banking.auth.entity.Role;
import com.banking.auth.entity.User;
import com.banking.auth.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInit implements CommandLineRunner {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if(!userRepo.existsByRole(Role.ADMIN)){
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@practice.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            userRepo.save(admin);
        }
    }
}
