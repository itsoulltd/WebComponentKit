package com.infoworks.lab.rest.validation.EnumTypeValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class EnumTypeImpl implements ConstraintValidator<EnumType, String> {

    private List<String> values;

    @Override
    public void initialize(EnumType constraintAnnotation) {
        values = new ArrayList<>();
        Class<? extends Enum<?>> enumClz = constraintAnnotation.enumType();
        Enum[] enumConstants = enumClz.getEnumConstants();
        for (Enum anEnum : enumConstants) {
            values.add(anEnum.name());
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (values == null || values.isEmpty()) return false;
        if (value == null) return false;
        if (!value.isEmpty()){
            try {
                return values.contains(value.trim());
            } catch (IllegalArgumentException e) {}
        }
        return false;
    }
}
