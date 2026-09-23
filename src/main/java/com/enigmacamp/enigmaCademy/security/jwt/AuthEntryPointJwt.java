package com.enigmacamp.enigmaCademy.security.jwt;

import com.enigmacamp.enigmaCademy.dto.response.CommonResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.awt.*;
import java.io.IOException;

@Component
@Slf4j
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        // print error jika ada unauthorized error
        log.error("Unauthorized error : " + authException.getMessage());

        // set response
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        CommonResponse<Object> errorResponse = CommonResponse.builder()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .message("Unauthorized + " + authException.getMessage())
                .build();

        final ObjectMapper mapper = new ObjectMapper();

        mapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
