package com.enigmacamp.enigmaCademy.mapper;

import com.enigmacamp.enigmaCademy.dto.response.CourseResponse;
import com.enigmacamp.enigmaCademy.entity.Course;

import java.util.List;

public class CourseMapper {
    // mapper untuk create
    public static CourseResponse convertToCourseResponse(Course course) {
        // 1. instansiate
        CourseResponse courseResponse = new CourseResponse();

        // 2. set value
        courseResponse.setId(course.getId());
        courseResponse.setTitle(course.getTitle());
        courseResponse.setDescription(course.getDescription());
        courseResponse.setStatus(course.getStatus());
        courseResponse.setInstructor(course.getInstructor());
        courseResponse.setCategory(course.getCategory());

        return courseResponse;
    }

    // mapper untuk list
    public static List<CourseResponse> convertToUserListResponse(List<Course> courses) {
        return courses.stream()
                .map(CourseMapper::convertToCourseResponse)
                .toList();
    }
}
