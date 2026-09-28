package com.banking.auth.repo;

import com.banking.auth.entity.Role;
import com.banking.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
     Optional<User> findByUsernameOrEmail(String username, String email);
     boolean existsByUsername(String username);
     boolean existsByEmail(String email);
     boolean existsByRole(Role role);
}
