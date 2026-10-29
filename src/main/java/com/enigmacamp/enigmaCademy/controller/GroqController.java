package com.enigmacamp.enigmaCademy.controller;

import com.enigmacamp.enigmaCademy.dto.request.GroqQuizRequest;
import com.enigmacamp.enigmaCademy.service.GroqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
public class GroqController {
    private final GroqService groqService;

    @Autowired
    public GroqController(GroqService groqService) {
        this.groqService = groqService;
    }

    @PostMapping("/quiz")
    public ResponseEntity<String> generateQuiz(
            @RequestBody GroqQuizRequest request
            ) {


        return ResponseEntity.ok(groqService.generateQuiz(request));
    }
}
