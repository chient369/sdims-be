package com.company.internalmgmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@ComponentScan(basePackages = {
    "com.company.internalmgmt"
})
public class InternalMgmtApplication {
    public static void main(String[] args) {
        SpringApplication.run(InternalMgmtApplication.class, args);
    }
} 
