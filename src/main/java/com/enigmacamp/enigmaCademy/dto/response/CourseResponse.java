package com.enigmacamp.enigmaCademy.dto.response;

import com.enigmacamp.enigmaCademy.model.Category;
import com.enigmacamp.enigmaCademy.model.User;
import com.enigmacamp.enigmaCademy.utils.enums.CourseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private Long id;

    private String title;

    private String description;

    private CourseStatus status;

    private User instructor;

    private Category category;
}
