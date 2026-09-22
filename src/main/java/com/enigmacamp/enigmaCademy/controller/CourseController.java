package com.enigmacamp.enigmaCademy.controller;

import com.enigmacamp.enigmaCademy.dto.request.CourseRequest;
import com.enigmacamp.enigmaCademy.dto.response.CommonResponse;
import com.enigmacamp.enigmaCademy.dto.response.CourseResponse;
import com.enigmacamp.enigmaCademy.dto.response.UserResponse;
import com.enigmacamp.enigmaCademy.model.Course;
import com.enigmacamp.enigmaCademy.service.CourseService;
import com.enigmacamp.enigmaCademy.utils.ResponseUtil;
import com.enigmacamp.enigmaCademy.utils.constants.ResponseMessage;
import com.enigmacamp.enigmaCademy.utils.validators.ValidationGroups;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<CommonResponse<CourseResponse>> createCourse(
            @Validated(ValidationGroups.OnCreate.class)
            @RequestBody CourseRequest request
    ) {
        CourseResponse courseResponse = courseService.saveCourse(request);

        return ResponseUtil.buildResponse(
                HttpStatus.CREATED,
                ResponseMessage.SUCCES_CREATE_DATA,
                courseResponse
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<CourseResponse>> updateCourse(@Validated(ValidationGroups.OnUpdate.class) @PathVariable Long id, @RequestBody CourseRequest course){
        CourseResponse courseResponse = courseService.update(id, course);

        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCES_UPDATE_DATA, courseResponse);
    }

}
