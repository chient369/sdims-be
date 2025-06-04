package com.company.internalmgmt.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration class for CORS settings.
 * Reads values from application.yml.
 */
@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${app.cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${app.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS,PATCH}")
    private String allowedMethods;

    @Value("${app.cors.allowed-headers:authorization,content-type,x-auth-token}")
    private String allowedHeaders;

    @Value("${app.cors.exposed-headers:x-auth-token}")
    private String exposedHeaders;

    /**
     * Get allowed origins as an array
     */
    public String[] getAllowedOriginsArray() {
        return allowedOrigins.split(",");
    }

    /**
     * Get allowed methods as an array
     */
    public String[] getAllowedMethodsArray() {
        return allowedMethods.split(",");
    }

    /**
     * Get allowed headers as an array
     */
    public String[] getAllowedHeadersArray() {
        return allowedHeaders.split(",");
    }

    /**
     * Get exposed headers as an array
     */
    public String[] getExposedHeadersArray() {
        return exposedHeaders.split(",");
    }

    /**
     * Get allowed origins as a single string
     */
    public String getAllowedOrigins() {
        return allowedOrigins;
    }

    /**
     * Creates a CorsConfigurationSource bean for Spring Security
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(getAllowedOriginsArray()));
        configuration.setAllowedMethods(Arrays.asList(getAllowedMethodsArray()));
        configuration.setAllowedHeaders(Arrays.asList(getAllowedHeadersArray()));
        configuration.setExposedHeaders(Arrays.asList(getExposedHeadersArray()));
        configuration.setAllowCredentials(allowCredentials);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
} 