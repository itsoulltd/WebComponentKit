package com.infoworks.lab.rest.validation.Password;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = PasswordRuleConstraint.class)
@Target({ TYPE, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface PasswordRule {

    int mixLengthRule() default 0;
    int maxLengthRule() default 0;
    int maxUpperCaseCharRule() default 0;
    int maxDigitCharRule() default 0;
    int maxSpecialCharRule() default 0;
    int maxNumericalSequenceRule() default 0;
    int maxAlphaSequenceRule() default 0;
    int maxQwertySequenceRule() default 0;
    boolean whitespaceRule() default false;
    String message() default "Invalid Password";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
