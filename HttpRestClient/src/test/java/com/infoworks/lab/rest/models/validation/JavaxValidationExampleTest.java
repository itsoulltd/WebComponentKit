package com.infoworks.lab.rest.models.validation;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.validation.Validator;

public class JavaxValidationExampleTest {

    private Validator validator;

    @Before
    public void setUp() {
        validator = ValidationConfig.createValidator();
    }

    @After
    public void tearDown() {
        validator = null;
    }

    @Test
    public void userValidationTest() {
        User user = new User();
        //Validation:
        String[] messages = ValidationConfig.validate(validator, user);
        Assert.assertTrue(messages.length > 0);
        System.out.println(String.join("; \n", messages));
    }

    @Test
    public void userValidationTestMinUpperCase() {
        User user = new User();
        user.setEmail("m.tow.g.com");
        //Rule: Min 2 Uppercase, Special, Digit char, Max 4 alpha/numeric char in seq.
        user.setPassword("01opMMM@#$_abc123");
        //Validation:
        String[] messages = ValidationConfig.validate(validator, user);
        Assert.assertTrue(messages.length > 0);
        System.out.println(String.join("; \n", messages));
    }

    @Test
    public void userValidationTestMinUpperCaseV2() {
        User user = new User();
        user.setEmail("m.tow.g.com");
        //Rule: Min 2 Uppercase, Special, Digit char, Max 4 alpha/numeric char in seq.
        user.setPassword("1opM@abcd1234");
        //Validation:
        String[] messages = ValidationConfig.validate(validator, user);
        Assert.assertTrue(messages.length > 0);
        System.out.println(String.join("; \n", messages));
    }

    @Test
    public void userValidationTestV3() {
        User user = null;
        //Validation:
        String[] messages = ValidationConfig.validate(validator, user);
        Assert.assertTrue(messages.length > 0);
        System.out.println(String.join("; \n", messages));
    }

    @Test
    public void userValidationTestV4() {
        User user = new User();
        user.setTenantID("mytenant");
        user.setEmail("sohana@gmail.com");
        user.setAge(20);
        user.setCurrency("BDT");
        //Validation:
        String[] messages = ValidationConfig.validate(validator, user);
        Assert.assertTrue(messages.length == 0);
        System.out.println(String.join("; \n", messages));
    }

}
