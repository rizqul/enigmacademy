package com.enigmacamp.enigmaCademy.dto.request;

import com.enigmacamp.enigmaCademy.utils.validators.ValidCourseStatus;
import com.enigmacamp.enigmaCademy.utils.validators.ValidationGroups;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequest {
    @NotBlank(message = "nama tidak boleh kosong", groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
    private String title;

    @NotBlank(message = "deskripsi tidak boleh kosong", groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
    private String description;

    @ValidCourseStatus
    private String status;

    private Long instructorId;

    private Long categoryId;
}
