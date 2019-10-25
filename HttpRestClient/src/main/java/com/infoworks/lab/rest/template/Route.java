package com.infoworks.lab.rest.template;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Route {
    String value() default "";
}
