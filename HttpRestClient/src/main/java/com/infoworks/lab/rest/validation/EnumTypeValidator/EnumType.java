package com.infoworks.lab.rest.validation.EnumTypeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = EnumTypeImpl.class)
@ReportAsSingleViolation
public @interface EnumType {
    Class<? extends Enum<?>> enumType();
    String message() default "Value is not valid!";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
