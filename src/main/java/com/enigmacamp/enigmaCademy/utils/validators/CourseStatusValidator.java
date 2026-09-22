package com.enigmacamp.enigmaCademy.utils.validators;

import com.enigmacamp.enigmaCademy.utils.enums.CourseStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CourseStatusValidator implements ConstraintValidator<ValidCourseStatus, String> {

    @Override
    public boolean isValid(
            String value,
            ConstraintValidatorContext context
    ) {
        if (value == null || value.isBlank()) {
            return true;
        }

        return CourseStatus.isValid(value.toUpperCase());
    }
}
