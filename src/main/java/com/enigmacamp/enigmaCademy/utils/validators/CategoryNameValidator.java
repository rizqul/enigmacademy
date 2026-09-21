package com.enigmacamp.enigmaCademy.utils.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class CategoryNameValidator implements ConstraintValidator<ValidCategoryName, String> {

    List<String> categoryName = List.of("BackEnd", "FrontEnd", "Mobile");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }

        for (String cat : categoryName){
            if (!cat.toLowerCase().equals(value.toLowerCase())){
                return false;
            }
        }

        return true;
    }
}
