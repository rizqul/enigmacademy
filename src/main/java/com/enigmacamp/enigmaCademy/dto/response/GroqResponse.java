package com.enigmacamp.enigmaCademy.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class GroqResponse {
    private String id;
    private String object;
    private Long created;
    private String model;
    private List<Choices> choices;

    public static class Choices {
        private Integer index;
        private Message message;
        private String finish_reason;
    }

    public static class Message {
        private String role;
        private String message;
    }
}
