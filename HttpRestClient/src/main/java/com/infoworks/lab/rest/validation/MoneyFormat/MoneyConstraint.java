package com.infoworks.lab.rest.validation.MoneyFormat;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MoneyConstraint implements ConstraintValidator<Money, String> {

    private String REGEX = "^[0-9]+\\.[0-9]{2}?$";

    @Override
    public void initialize(Money annotation) {
        if(annotation.regx() != null && !annotation.regx().isEmpty())
            REGEX = annotation.regx();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (!value.isEmpty()){
            return value.matches(REGEX);
        }
        return false;
    }
}
