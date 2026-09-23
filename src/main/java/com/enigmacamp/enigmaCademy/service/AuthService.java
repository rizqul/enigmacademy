package com.enigmacamp.enigmaCademy.service;

import com.enigmacamp.enigmaCademy.dto.request.LoginRequest;
import com.enigmacamp.enigmaCademy.dto.request.UserRequest;
import com.enigmacamp.enigmaCademy.dto.response.LoginResponse;
import com.enigmacamp.enigmaCademy.dto.response.UserResponse;
import org.springframework.security.authentication.BadCredentialsException;

public interface AuthService {
    LoginResponse loginUser(LoginRequest request) throws BadCredentialsException;

    UserResponse registerUser(UserRequest request);
}
