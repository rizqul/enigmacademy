package com.enigmacamp.enigmaCademy.utils.validators;

import com.enigmacamp.enigmaCademy.utils.enums.UserRole;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserRoleValidator implements ConstraintValidator<ValidUserRole, String> {

    @Override
    public boolean isValid(
            String value,
            ConstraintValidatorContext context
    ){
      if (value == null  || value.isBlank()) {
          return true;
      }

      if(UserRole.isValid(value.toUpperCase())){
          return true;
      } else {
          return  false;
      }
    }
}
