package com.infoworks.lab.rest.validation.Email;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailPatternConstraint implements ConstraintValidator<EmailPattern, String> {
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final Pattern PATTERN = Pattern.compile(EMAIL_PATTERN);
    private boolean nullable;
    private String defaultMessage;

    @Override
    public void initialize(EmailPattern annotation) {
        nullable = annotation.nullable();
        defaultMessage = annotation.message();
    }

    @Override
    public boolean isValid(final String username, final ConstraintValidatorContext context) {
        if (username == null && nullable) return true;
        if (username == null) {
            //because in this case nullable is false;
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(defaultMessage)
                    .addConstraintViolation();
            return false;
        }
        Matcher matcher = PATTERN.matcher(username);
        return matcher.matches();
    }
}
