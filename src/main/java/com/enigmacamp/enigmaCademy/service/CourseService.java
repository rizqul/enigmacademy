package com.enigmacamp.enigmaCademy.service;

import com.enigmacamp.enigmaCademy.dto.request.CourseRequest;
import com.enigmacamp.enigmaCademy.dto.response.CourseResponse;
import org.springframework.transaction.annotation.Transactional;

public interface CourseService {
    CourseResponse saveCourse(CourseRequest courseRequest);

    @Transactional
    CourseResponse update(Long id, CourseRequest courseRequest);
}
