package com.enigmacamp.enigmaCademy.service.impl;

import com.enigmacamp.enigmaCademy.dto.request.CourseRequest;
import com.enigmacamp.enigmaCademy.dto.response.CourseResponse;
import com.enigmacamp.enigmaCademy.mapper.CourseMapper;
import com.enigmacamp.enigmaCademy.model.Category;
import com.enigmacamp.enigmaCademy.model.Course;
import com.enigmacamp.enigmaCademy.model.User;
import com.enigmacamp.enigmaCademy.repository.CategoryRepository;
import com.enigmacamp.enigmaCademy.repository.CourseRepository;
import com.enigmacamp.enigmaCademy.repository.UserRepository;
import com.enigmacamp.enigmaCademy.service.CourseService;
import com.enigmacamp.enigmaCademy.utils.enums.CourseStatus;
import com.enigmacamp.enigmaCademy.utils.enums.UserRole;
import com.enigmacamp.enigmaCademy.utils.exception.BadRequestException;
import com.enigmacamp.enigmaCademy.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public CourseResponse saveCourse(CourseRequest courseRequest) {
        User user = userRepository.findById(courseRequest.getInstructorId()).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", courseRequest.getInstructorId())
        );

        if (!user.getUserRole().equals(UserRole.INSTRUCTOR)) {
            throw new BadRequestException("User bukan instruktur");
        }

        Category category = categoryRepository.findById(courseRequest.getCategoryId()).orElseThrow(
                () -> new ResourceNotFoundException("Category", "id", courseRequest.getCategoryId())
        );

        CourseStatus courseStatus = (courseRequest.getStatus() != null && !courseRequest.getStatus().isBlank())
                ? CourseStatus.valueOf(courseRequest.getStatus())
                : CourseStatus.DRAFT;

        Course newCourse = new Course(
                courseRequest.getTitle(),
                courseRequest.getDescription(),
                courseStatus
        );

        newCourse.setInstructor(user);
        newCourse.setCategory(category);

        Course savedCourse = courseRepository.save(newCourse);
        return CourseMapper.convertToCourseResponse(savedCourse);
    }
}
