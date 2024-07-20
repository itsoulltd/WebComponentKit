package com.infoworks.lab.rest.models.validation;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ValidationConfig {

    public static Validator createValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        return validator;
    }

    public static <T> String[] validate(Validator validator, T target) {
        if (target == null) return new String[]{"target param must not be null!"};
        List<String> messages = new ArrayList<>();
        Set<ConstraintViolation<T>> violations = validator.validate(target);
        for (ConstraintViolation<T> violation : violations) {
            messages.add(violation.getMessage());
        }
        return messages.toArray(new String[0]);
    }
}
