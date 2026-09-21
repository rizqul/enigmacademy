package com.enigmacamp.enigmaCademy.utils.validators;

import com.enigmacamp.enigmaCademy.dto.request.UserRequest;
import com.enigmacamp.enigmaCademy.utils.enums.UserRole;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LmsEmailValidator implements ConstraintValidator<ValidLmsEmail, UserRequest> {
    @Override
    public boolean isValid(
            UserRequest request,
            ConstraintValidatorContext context
    ) {
        if(request == null) {
            return true;
        }

//        if(request.getEmail() == null || request.getUserRole() == null) {
//            return true;
//        }

        if( request.getUserRole() == null || request.getUserRole().isBlank()) {
            return request.getEmail().endsWith("@student.enigmacademy.com");
        }

        if (request.getUserRole().equals(UserRole.INSTRUCTOR.toString())) {
            return request.getEmail().endsWith("@enigmacademy.com");
        }

        return false;
    }
}
