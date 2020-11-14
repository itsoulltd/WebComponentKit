package com.infoworks.lab.rest.validation.CurrencyCode;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Currency;

public class CurrencyCodeConstraint implements ConstraintValidator<IsValidCurrencyCode, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;
        if (!value.isEmpty()){
            try {
                Currency currency = Currency.getInstance(value);
                return currency != null;
            } catch (Exception e) {}
        }
        return false;
    }
}
