package com.enigmacamp.enigmaCademy.utils.exception;

import com.enigmacamp.enigmaCademy.dto.response.CommonResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CommonResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex,
            WebRequest request) {
        CommonResponse<Object> response = CommonResponse.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<CommonResponse<Object>> handleBadRequestException(BadRequestException ex,
            WebRequest request) {
        CommonResponse<Object> response = CommonResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<CommonResponse<Object>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, WebRequest request) {

        String message = "Data Sudah Terdaftar!";

        if (ex.getMessage().contains("users_email_key")) {
            message = "Alamat email sudah terdaftar!";
        }

        CommonResponse<Object> response = CommonResponse.builder()
                .statusCode(HttpStatus.CONFLICT.value())
                .message(message)
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<CommonResponse<Object>> handleBadCredentialException(
            BadCredentialsException ex,
            WebRequest request) {

        CommonResponse<Object> response = CommonResponse.builder()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .message(ex.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Object>> handleValidationException(MethodArgumentNotValidException ex,
            WebRequest request) {
        ObjectError error = ex.getBindingResult()
                .getAllErrors()
                .getFirst();

        String message = error.getDefaultMessage();

        CommonResponse<Object> response = CommonResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Security-specific exception handlers

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CommonResponse<Object>> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {

        log.warn("Access denied for request to {}: {}", request.getRequestURI(), ex.getMessage());

        CommonResponse<Object> response = CommonResponse.builder()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .message("Anda tidak memiliki akses ke resource ini")
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<CommonResponse<Object>> handleDisabledException(
            DisabledException ex, HttpServletRequest request) {

        CommonResponse<Object> response = CommonResponse.builder()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .message("Akun dinonaktifkan, silakan hubungi administrator")
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<CommonResponse<Object>> handleLockedException(
            LockedException ex, HttpServletRequest request) {

        CommonResponse<Object> response = CommonResponse.builder()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .message("Akun terkunci, silakan coba lagi nanti atau hubungi administrator")
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<CommonResponse<Object>> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {

        log.warn("Authentication exception for request to {}: {}",
                request.getRequestURI(), ex.getMessage());

        CommonResponse<Object> response = CommonResponse.builder()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .message("Autentikasi gagal: " + ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}
