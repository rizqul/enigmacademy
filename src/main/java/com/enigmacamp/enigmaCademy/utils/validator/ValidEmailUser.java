package com.enigmacamp.enigmaCademy.utils.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented // To ensure that our custom annotations are shown in the documentation (this is not mandatory)
@Constraint(validatedBy = EmailUserValidator.class) // to list what constraint it belongs to. constraint reference to where the validation logic implemented to
@Target({ElementType.TYPE}) // to
@Retention(RetentionPolicy.RUNTIME) // to implement retention policies to tell where scope of the validation (ex: runtime)
public @interface ValidEmailUser {
    String message() default "Invalid format email address for given role";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
