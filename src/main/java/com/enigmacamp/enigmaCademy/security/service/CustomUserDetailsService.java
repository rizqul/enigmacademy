package com.enigmacamp.enigmaCademy.security.service;

import com.enigmacamp.enigmaCademy.model.User;
import com.enigmacamp.enigmaCademy.repository.UserRepository;
import com.enigmacamp.enigmaCademy.utils.exception.BadRequestException;
import com.enigmacamp.enigmaCademy.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
