package com.khokhlov.cloudstorage.validation.validator;

import com.khokhlov.cloudstorage.validation.PathValidationUtils;
import com.khokhlov.cloudstorage.validation.annotation.SafePathOrRoot;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PathOrRootValidator implements ConstraintValidator<SafePathOrRoot, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return PathValidationUtils.isValidPath(value, true);
    }
}
