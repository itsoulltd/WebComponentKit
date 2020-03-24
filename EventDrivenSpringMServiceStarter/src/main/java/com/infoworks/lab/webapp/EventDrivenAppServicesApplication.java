package com.infoworks.lab.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.infoworks.lab.controllers"
        , "com.infoworks.lab.webapp.config"
        , "com.infoworks.lab.domain"})
public class EventDrivenAppServicesApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(EventDrivenAppServicesApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(EventDrivenAppServicesApplication.class);
    }

}

