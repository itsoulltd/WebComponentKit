package com.infoworks.lab.rest.models.validation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class UserValidationTest {

    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator;

    @Before
    public void setUp() {
        validator = factory.getValidator();
    }

    @After
    public void tearDown() {
        validator = null;
    }

    @Test
    public void userValidationTest() {
        User user = new User();
        //Validation:
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        for (ConstraintViolation<User> violation : violations) {
            System.out.println(violation.getMessage());
        }
    }

}
