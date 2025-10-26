package com.khokhlov.cloudstorage.validation.validator;

import com.khokhlov.cloudstorage.validation.PathValidationUtils;
import com.khokhlov.cloudstorage.validation.annotation.SafePath;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PathSafeValidator implements ConstraintValidator<SafePath, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return PathValidationUtils.isValidPath(value, false);
    }
}
