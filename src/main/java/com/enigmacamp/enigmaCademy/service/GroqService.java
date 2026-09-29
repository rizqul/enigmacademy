package com.enigmacamp.enigmaCademy.service;

import com.enigmacamp.enigmaCademy.dto.request.GroqQuizRequest;
import com.enigmacamp.enigmaCademy.dto.response.GroqResponse;

public interface GroqService {
    String generateQuiz(GroqQuizRequest request);
}
