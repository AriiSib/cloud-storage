package com.khokhlov.cloudstorage.validation.annotation;

import com.khokhlov.cloudstorage.validation.validator.PathOrRootValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PathOrRootValidator.class)
public @interface SafePathOrRoot {
    String message() default "Value is longer than 255 characters or contains invalid characters";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
