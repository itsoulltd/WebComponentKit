package com.infoworks.lab.webapp.config;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface RequestFilter {
    String[] openAccess() default {};
    String appid() default "";
    String secret() default "";
}
