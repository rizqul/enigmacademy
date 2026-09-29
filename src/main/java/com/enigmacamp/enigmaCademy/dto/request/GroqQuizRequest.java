package com.enigmacamp.enigmaCademy.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GroqQuizRequest {
    @NotBlank
    private String courseName;

    @NotBlank
    private String courseDescription;

    @Min(1)
    @Max(20)
    private Integer numberOfQuestions = 5;

    private String difficulty = "MEDIUM";
}
