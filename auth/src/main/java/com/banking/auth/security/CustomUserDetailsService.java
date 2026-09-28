package com.banking.auth.security;

import com.banking.auth.entity.User;
import com.banking.auth.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepo repo;

    @Override
    public UserDetails loadUserByUsername(String usernameOrPassword) throws UsernameNotFoundException {
        User user = repo.findByUsernameOrEmail(usernameOrPassword, usernameOrPassword)
                .orElseThrow(() -> new UsernameNotFoundException("User does not exist"));
        return new CustomUserDetails(user);
    }
}
