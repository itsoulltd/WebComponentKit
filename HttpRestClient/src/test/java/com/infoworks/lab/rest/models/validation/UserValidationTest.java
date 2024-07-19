package com.infoworks.lab.rest.models.validation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
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
        String[] messages = validate(validator, user);
        System.out.println(String.join("; \n", messages));
    }

    @Test
    public void userValidationTestV2() {
        User user = new User();
        user.setEmail("m.tow.g.com");
        user.setPassword("01op");
        //Validation:
        String[] messages = validate(validator, user);
        System.out.println(String.join("; \n", messages));
    }

    @Test
    public void userValidationTestV3() {
        User user = null;
        //Validation:
        String[] messages = validate(validator, user);
        System.out.println(String.join("; \n", messages));
    }

    public <T> String[] validate(Validator validator, T target) {
        if (target == null) return new String[]{"Validation target must not be null;"};
        List<String> messages = new ArrayList<>();
        Set<ConstraintViolation<T>> violations = validator.validate(target);
        for (ConstraintViolation<T> violation : violations) {
            messages.add(violation.getMessage());
        }
        return messages.toArray(new String[0]);
    }

}
