package com.enigmacamp.enigmaCademy.service.impl;

import com.enigmacamp.enigmaCademy.dto.request.CourseRequest;
import com.enigmacamp.enigmaCademy.dto.response.CourseResponse;
import com.enigmacamp.enigmaCademy.mapper.CourseMapper;
import com.enigmacamp.enigmaCademy.entity.Category;
import com.enigmacamp.enigmaCademy.entity.Course;
import com.enigmacamp.enigmaCademy.entity.User;
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

import java.util.Objects;

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
        User user = isInstructorAvailable(courseRequest.getInstructorId());
        isInstructor(courseRequest.getInstructorId(), user);

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

    @Transactional
    @Override
    public CourseResponse update(Long id, CourseRequest courseRequest){
        if(id == null || id<=0){
            throw new BadRequestException("Id tidak boleh kosong atau lebih kecil/sama dengan nol");
        }
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("course", "id", id));
        if (!Objects.equals(course.getTitle(), courseRequest.getTitle())) {
            course.setTitle(courseRequest.getTitle());
        }
        User user = isInstructorAvailable(courseRequest.getInstructorId());
        isInstructor(id, user);
        Category category = isCategoryAvailable(courseRequest.getCategoryId());

        course.setTitle(courseRequest.getTitle());
        course.setCategory(category);
        course.setDescription(courseRequest.getDescription());
        course.setStatus(CourseStatus.valueOf(courseRequest.getStatus()));
        course.setInstructor(user);

        return CourseMapper.convertToCourseResponse(courseRepository.save(course));
    }

    public void isInstructor(Long id, User user){
        if (!user.getUserRole().equals(UserRole.INSTRUCTOR)) {
            throw new BadRequestException("User bukan instruktur");
        }
    }
    public User isInstructorAvailable(Long id){

        return userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id)
        );
    }
    public Category isCategoryAvailable(Long id){
        return categoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Category", "id", id)
        );
    }
}
