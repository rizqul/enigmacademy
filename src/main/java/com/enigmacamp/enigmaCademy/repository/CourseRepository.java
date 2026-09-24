package com.enigmacamp.enigmaCademy.repository;

import com.enigmacamp.enigmaCademy.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CourseRepository
        extends
        JpaRepository<Course, Long>,
        JpaSpecificationExecutor<Course> {
}
