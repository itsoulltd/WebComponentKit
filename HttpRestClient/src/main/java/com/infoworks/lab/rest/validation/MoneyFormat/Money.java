package com.infoworks.lab.rest.validation.MoneyFormat;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = MoneyConstraint.class)
public @interface Money {

    /**
     * Since regular expressions are horrible to read, much less understand, here is the verbose equivalent:
     *
     * ^                         # Start of string
     *  [0-9]+                   # Require one or more numbers
     *        (                  # Begin optional group
     *         \.                # Point must be escaped or it is treated as "any character"
     *           [0-9]{1,2}      # One or two numbers
     *                     )?    # End group--signify that it's optional with "?"
     *                       $   # End of string
     *
     */

    /**
     * regx-sample-1: ^[0-9]+(\.[0-9]{1,2})?$
     * valid = ["123.12", "2", "56754", "92929292929292.12", "0.21", "3.1"]
     * nvalid = ["12.1232", "2.23332", "e666.76"]
     *
     * regx-sample-2: ^[0-9]+\.[0-9]{2}?$
     * valid = ["123.12", "92929292929292.12", "0.21"]
     * invalid = ["12.1232", "2.23332", "e666.76", "2", "3.1", "56754"]
     */
    //
    String regx() default "^[0-9]+\\.[0-9]{2}?$";
    String message() default "Money must be well formatted. " +
            "e.g. 0.00 or Any number digits before precision and least 2 digit after precision.";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
