package com.enigmacamp.enigmaCademy.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequest {
    private Integer postId;
    private String name;
    private String email;
    private String body;
}
