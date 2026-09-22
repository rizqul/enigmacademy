package com.enigmacamp.enigmaCademy.controller;


import com.enigmacamp.enigmaCademy.dto.response.CommonResponse;
import com.enigmacamp.enigmaCademy.utils.ResponseUtil;
import com.enigmacamp.enigmaCademy.utils.constants.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public")
public class PublicController {
    @GetMapping("/hello")
    public ResponseEntity<CommonResponse<String>> helloWorld() {
        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCES_GET_DATA, "Hello world!");
    }
}
