package com.enigmacamp.enigmaCademy.service.impl;

import com.enigmacamp.enigmaCademy.dto.request.UserRequest;
import com.enigmacamp.enigmaCademy.dto.response.UserResponse;
import com.enigmacamp.enigmaCademy.mapper.UserMapper;
import com.enigmacamp.enigmaCademy.model.User;
import com.enigmacamp.enigmaCademy.repository.UserRepository;
import com.enigmacamp.enigmaCademy.service.UserRegistrationService;
import com.enigmacamp.enigmaCademy.utils.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegisterServiceImpl implements UserRegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;



    @Override
    public UserResponse registerUser(UserRequest userRequest) {
        UserRole userRole = (userRequest.getUserRole() != null && !userRequest.getUserRole().isBlank())
                ? UserRole.valueOf(userRequest.getUserRole())
                : UserRole.STUDENT;

        User newUser = new User(
                userRequest.getName(),
                userRequest.getUsername(),
                userRequest.getEmail(),
                userRole,
                passwordEncoder.encode(userRequest.getPassword()));

        User savedUser = userRepository.save(newUser);
        return UserMapper.convertToUserResponse(savedUser);
    }
}
