package com.company.internalmgmt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for Spring MVC.
 * Sets up global CORS configuration for all controllers.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;
    
    @Value("${app.cors.allow-credentials:true}")
    private boolean allowCredentials;
    
    @Value("${app.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS,PATCH}")
    private String allowedMethods;
    
    @Value("${app.cors.allowed-headers:authorization,content-type,x-auth-token}")
    private String allowedHeaders;
    
    @Value("${app.cors.exposed-headers:x-auth-token}")
    private String exposedHeaders;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins(allowedOrigins.split(","))
            .allowedMethods(allowedMethods.split(","))
            .allowedHeaders(allowedHeaders.split(","))
            .exposedHeaders(exposedHeaders.split(","))
            .allowCredentials(allowCredentials);
    }
} 