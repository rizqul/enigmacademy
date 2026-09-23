package com.enigmacamp.enigmaCademy.service;

import com.enigmacamp.enigmaCademy.dto.request.LoginRequest;
import com.enigmacamp.enigmaCademy.dto.request.UserRequest;
import com.enigmacamp.enigmaCademy.dto.response.LoginResponse;
import com.enigmacamp.enigmaCademy.dto.response.UserResponse;

public interface AuthService {
//    LoginResponse loginUser(LoginRequest request);
    void loginUser(LoginRequest request);

    UserResponse registerUser(UserRequest request);
}
