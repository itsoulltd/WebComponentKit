package com.infoworks.lab.rest.validation.Password;

import org.passay.*;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class PasswordRuleConstraint implements ConstraintValidator<PasswordRule, String> {

    private PasswordValidator validator;
    private boolean nullable;
    private String defaultMessage;

    @Override
    public void initialize(final PasswordRule annotation) {
        nullable = annotation.nullable();
        defaultMessage = annotation.message();
        List<Rule> rules = new ArrayList<>();
        if (annotation.maxLengthRule() > 0 && annotation.minLengthRule() > 0)
            rules.add(new LengthRule(annotation.minLengthRule(), annotation.maxLengthRule()));
        if (annotation.minUpperCaseCharRule() > 0)
            rules.add(new UppercaseCharacterRule(annotation.minUpperCaseCharRule()));
        if (annotation.minDigitCharRule() > 0)
            rules.add(new DigitCharacterRule(annotation.minDigitCharRule()));
        if (annotation.minSpecialCharRule() > 0)
            rules.add(new SpecialCharacterRule(annotation.minSpecialCharRule()));
        if (annotation.maxNumericalSequenceRule() > 0)
            rules.add(new NumericalSequenceRule(annotation.maxNumericalSequenceRule(), false));
        if (annotation.maxAlphaSequenceRule() > 0)
            rules.add(new AlphabeticalSequenceRule(annotation.maxAlphaSequenceRule(), false));
        if (annotation.maxQwertySequenceRule() > 0)
            rules.add(new QwertySequenceRule(annotation.maxQwertySequenceRule(), false));
        if (annotation.whitespaceRule())
            rules.add(new WhitespaceRule());
        validator = new PasswordValidator(rules);
    }

    @Override
    public boolean isValid(final String password, final ConstraintValidatorContext context) {
        if (password == null && nullable) return true;
        if (password == null) {
            //because in this case nullable is false;
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(defaultMessage)
                    .addConstraintViolation();
            return false;
        }
        // @formatter:off
        final RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        } else {
            context.disableDefaultConstraintViolation();
            String compiledMessage = String.join(",", validator.getMessages(result));
            context.buildConstraintViolationWithTemplate(compiledMessage)
                    .addConstraintViolation();
            return false;
        }
    }

}
