package com.company.internalmgmt.common.annotation;

import org.springframework.web.bind.annotation.CrossOrigin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to apply CrossOrigin with configuration from application.yml.
 * This is used to replace standard @CrossOrigin with a centralized configuration.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@CrossOrigin
public @interface CrossOriginConfig {
    // This annotation doesn't need any parameters as it will use values from application.yml
    // The actual configuration is done in WebSecurityConfig using CorsConfig
} 