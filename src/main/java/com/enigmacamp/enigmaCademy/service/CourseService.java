package com.enigmacamp.enigmaCademy.service;

import com.enigmacamp.enigmaCademy.dto.request.CourseRequest;
import com.enigmacamp.enigmaCademy.dto.response.CourseResponse;

public interface CourseService {
    CourseResponse saveCourse(CourseRequest courseRequest);
}
