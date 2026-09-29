package com.enigmacamp.enigmaCademy.service.impl;

import com.enigmacamp.enigmaCademy.dto.request.GroqQuizRequest;
import com.enigmacamp.enigmaCademy.dto.response.GroqResponse;
import com.enigmacamp.enigmaCademy.service.GroqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GroqServiceImpl implements GroqService {

    private final RestClient groqClient;

    @Value("${groq.api.model}")
    private String model;

    @Value("${groq.api.url}")
    private String apiUrl;

    @Value("${groq.ai.key}")
    private String apiKey;

    public GroqServiceImpl(
            @Qualifier("groqClient") RestClient groqClient) {
        this.groqClient = groqClient;
    }

    @Override
    public String generateQuiz(GroqQuizRequest request) {
        try {
            // 1. bikin promt untuk system -> high persona dan non negotiable rule
            String systemPrompt = """
                    Anggap kamu adalah sebuah sistem pembuat soal untuk suatu Learning Management System bernama enigmaCademy.
                    
                    Kembalikan output hanya dalam format JSON valid tanpa teks atau markdown tambahan.
                    
                    Format JSON harus mengikuti struktur berikut ini :
                    
                    {
                        "questions": [
                            {
                                "question" : "Isi pertanyaannya",
                                "options" : ["A. opsi 1", "B. opsi 2", "C. opsi 3", "D. opsi 4"],
                                "correctAnswer": "A",
                                "explaination": "Alasan jawaban dikatakan benar"
                            }
                        ] 
                    }
                    """;

            // 2. bikin prompt untuk user -> data or user request
            String userPrompt = """
                    Buatkan %d soal pilihan ganda dengan kriteria sebagai berikut ini :
                    Course Name : %s
                    Course Description : %s
                    Difficulty Level : %s
                    """.formatted(
                    request.getNumberOfQuestions(),
                    request.getCourseName(),
                    request.getCourseDescription(),
                    request.getDifficulty()
            );

            // 3. Gabungkan prompt berdasarkan role
            List<Map<String, String>> messages = List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", userPrompt)
            );

            // 4. Membuat request body dengan "request format" JSON
            Map<String, Object> requestBody = Map.of(
                    "model", model,
                    "messages", messages,
                    "response_format", Map.of("type", "json_object"),
                    "temperature", 0.4
            );

            // 5. Eksekusi request ke Groq
            GroqResponse response = groqClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .body(requestBody)
                    .retrieve()
                    .body(GroqResponse.class);

            // 6. Validasi response
            if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                throw new RuntimeException("Groq API mengembalikan kembalian kosong");
            }

            // 7. Return
            return response
                    .getChoices()
                    .getFirst()
                    .getMessage()
                    .getContent();

        } catch (Exception e) {
            log.error("Failed to generate quiz form groq");
            throw new RuntimeException("Failed to generate using groq api" + e.getMessage());
        }
    };
}
