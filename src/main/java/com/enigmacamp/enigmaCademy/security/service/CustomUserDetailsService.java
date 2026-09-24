package com.enigmacamp.enigmaCademy.security.service;

import com.enigmacamp.enigmaCademy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    // load User By Username
    @Override
    public UserDetails loadUserByUsername(String username) throws BadCredentialsException {

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Username atau password salah"));
    }
}
