package com.enigmacamp.enigmaCademy.controller;

import com.enigmacamp.enigmaCademy.dto.request.UserRequest;
import com.enigmacamp.enigmaCademy.dto.response.CommonResponse;
import com.enigmacamp.enigmaCademy.dto.response.UserResponse;
import com.enigmacamp.enigmaCademy.service.AuthService;
import com.enigmacamp.enigmaCademy.utils.ResponseUtil;
import com.enigmacamp.enigmaCademy.utils.constants.ResponseMessage;
import com.enigmacamp.enigmaCademy.utils.validators.ValidationGroups;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService userRegistrationService;

    // create user baru
    @PostMapping("/register")
    public ResponseEntity<CommonResponse<UserResponse>> registerUser(
            @Validated(ValidationGroups.OnCreate.class)
            @RequestBody UserRequest request) {
        UserResponse userResponse = userRegistrationService.registerUser(request);

        return ResponseUtil.buildResponse(HttpStatus.CREATED, ResponseMessage.SUCCES_CREATE_DATA, userResponse);
    }
}
