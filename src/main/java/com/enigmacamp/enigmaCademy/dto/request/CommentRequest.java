package com.enigmacamp.enigmaCademy.dto.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CommentRequest {
    @Min(message = "Post Id tidak boleh kurang dari nol", value = 0)
    private Integer postId;
    private String name;
    private String email;
    private String body;
}
