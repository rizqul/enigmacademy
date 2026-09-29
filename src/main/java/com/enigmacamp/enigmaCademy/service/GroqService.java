package com.enigmacamp.enigmaCademy.service;

import com.enigmacamp.enigmaCademy.dto.request.GroqQuizRequest;

public interface GroqService {
    String generateQuiz(GroqQuizRequest request);
}
