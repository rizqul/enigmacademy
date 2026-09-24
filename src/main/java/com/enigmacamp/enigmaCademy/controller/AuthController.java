package com.enigmacamp.enigmaCademy.controller;

import com.enigmacamp.enigmaCademy.dto.request.LoginRequest;
import com.enigmacamp.enigmaCademy.dto.request.UserRequest;
import com.enigmacamp.enigmaCademy.dto.response.CommonResponse;
import com.enigmacamp.enigmaCademy.dto.response.LoginResponse;
import com.enigmacamp.enigmaCademy.dto.response.UserResponse;
import com.enigmacamp.enigmaCademy.service.AuthService;
import com.enigmacamp.enigmaCademy.utils.ResponseUtil;
import com.enigmacamp.enigmaCademy.utils.constants.ResponseMessage;
import com.enigmacamp.enigmaCademy.utils.validators.ValidationGroups;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name="Auth", description = "Auth endpoint")
public class AuthController {
    private final AuthService authService;
    // register user baru
    @Operation(
        summary = "Register new User",
            description = "ini adalah endpoint registreasi user baru",
            responses = {
                @ApiResponse(responseCode = "200",
                description = "berhasil melakukan registrasi")
    })
    @PostMapping("/register")
    public ResponseEntity<CommonResponse<UserResponse>> registerUser(
            @Validated(ValidationGroups.OnCreate.class)
            @RequestBody UserRequest request) {
        UserResponse userResponse = authService.registerUser(request);

        return ResponseUtil.buildResponse(HttpStatus.CREATED, ResponseMessage.SUCCES_CREATE_DATA, userResponse);
    }

    // login user
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<LoginResponse>> loginUser(
            @Valid
            @RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.loginUser(request);

        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCES_LOGIN, loginResponse);
    }
}
