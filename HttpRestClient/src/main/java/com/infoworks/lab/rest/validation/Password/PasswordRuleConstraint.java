package com.infoworks.lab.rest.validation.Password;

import org.passay.*;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class PasswordRuleConstraint implements ConstraintValidator<PasswordRule, String> {

    private PasswordValidator validator;

    @Override
    public void initialize(final PasswordRule annotation) {
        List<Rule> rules = new ArrayList<>();
        if (annotation.maxLengthRule() > 0 && annotation.mixLengthRule() > 0)
            rules.add(new LengthRule(annotation.mixLengthRule(), annotation.maxLengthRule()));
        if (annotation.maxUpperCaseCharRule() > 0)
            rules.add(new UppercaseCharacterRule(annotation.maxUpperCaseCharRule()));
        if (annotation.maxDigitCharRule() > 0)
            rules.add(new DigitCharacterRule(annotation.maxDigitCharRule()));
        if (annotation.maxSpecialCharRule() > 0)
            rules.add(new SpecialCharacterRule(annotation.maxSpecialCharRule()));
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
        // @formatter:off
        final RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(String.join(",", validator.getMessages(result))).addConstraintViolation();
        return false;
    }

}
