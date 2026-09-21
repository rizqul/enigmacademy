package com.enigmacamp.enigmaCademy.utils.validator;

import com.enigmacamp.enigmaCademy.dto.request.UserRequest;
import com.enigmacamp.enigmaCademy.utils.enums.UserRole;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Locale;

public class EmailUserValidator implements ConstraintValidator<ValidEmailUser, UserRequest> { // UserRequest is the kind of field that we want to validate, cause this is an email validator for email per user role so the type is gonna be user request
    @Override
    public void initialize(ValidEmailUser constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(UserRequest value, ConstraintValidatorContext context) {
        if (value == null){
            return true;
        }

        if (value.getEmail() == null || value.getEmail().isBlank()){
            // why it return true so that the annotations @NotBlank will handle the validation. so this will only focus on the email domain for user role.
            return true;
        }

        // this is to tell which role can use wich email domain
        if(value.getUserRole() == null || value.getUserRole().isBlank()){
            return value.getEmail().endsWith("@student.enigmacademy.com");
        }

        if(value.getUserRole().toUpperCase().equals(UserRole.INSTRUCTOR.toString())){
            return value.getEmail().endsWith("@enigmacademy.com");
        }

        return false;
    }

}
