package com.enigmacamp.enigmaCademy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
       private Integer postId;
       private Integer id;
       private String name;
       private String email;
       private String body;
}
