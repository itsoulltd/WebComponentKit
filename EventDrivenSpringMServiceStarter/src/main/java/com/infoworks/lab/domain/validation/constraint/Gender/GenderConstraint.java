package com.infoworks.lab.domain.validation.constraint.Gender;

import com.infoworks.lab.domain.entities.Gender;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class GenderConstraint implements ConstraintValidator<IsValidGender, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        if (!value.isEmpty()){
            try {
                return Gender.valueOf(value) != null;
            } catch (IllegalArgumentException e) {}
        }
        return false;
    }
}
