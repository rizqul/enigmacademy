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
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.Optional;

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

        String message = "Data tidak dapat disimpan karena melanggar aturan integritas data";
        String databaseMessage = Optional.ofNullable(ex.getMostSpecificCause())
                .map(Throwable::getMessage)
                .orElse("");

        if (databaseMessage.contains("users_email_key")) {
            message = "Alamat email sudah terdaftar!";
        }

        CommonResponse<Object> response = CommonResponse.builder()
                .statusCode(HttpStatus.CONFLICT.value())
                .message(message)
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<CommonResponse<Object>> handleDuplicateResourceException(
            DuplicateResourceException ex, WebRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
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

        String message = error instanceof FieldError fieldError
                ? fieldError.getField() + ": " + fieldError.getDefaultMessage()
                : error.getDefaultMessage();

        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonResponse<Object>> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {
        String message = ex.getConstraintViolations().stream()
                .findFirst()
                .map(ConstraintViolation::getMessage)
                .orElse("Parameter request tidak valid");

        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<CommonResponse<Object>> handleMalformedRequestException(
            Exception ex, WebRequest request) {
        String message = ex instanceof MethodArgumentTypeMismatchException mismatch
                ? "Parameter '" + mismatch.getName() + "' tidak valid"
                : "Format request tidak valid";

        return buildResponse(HttpStatus.BAD_REQUEST, message);
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

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<CommonResponse<Object>> handleResponseStatusException(
            ResponseStatusException ex, WebRequest request) {
        String message = Optional.ofNullable(ex.getReason()).orElse("Request tidak dapat diproses");
        return buildResponse(HttpStatus.valueOf(ex.getStatusCode().value()), message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Object>> handleUnexpectedException(Exception ex, WebRequest request) {
        log.error("Unexpected error while handling request", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Terjadi kesalahan pada server");
    }

    private ResponseEntity<CommonResponse<Object>> buildResponse(HttpStatus status, String message) {
        CommonResponse<Object> response = CommonResponse.builder()
                .statusCode(status.value())
                .message(message)
                .build();

        return ResponseEntity.status(status).body(response);
    }
}
