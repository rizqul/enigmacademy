package com.enigmacamp.enigmaCademy.service;

import com.enigmacamp.enigmaCademy.dto.request.UserRequest;
import com.enigmacamp.enigmaCademy.dto.response.UserResponse;

public interface UserRegistrationService {
    UserResponse registerUser(UserRequest request);
}
