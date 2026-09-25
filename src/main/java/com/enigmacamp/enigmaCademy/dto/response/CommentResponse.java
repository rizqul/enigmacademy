package com.enigmacamp.enigmaCademy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private Integer postId;
    private Integer id;
    private String name;
    private String email;
    private String body;
}
