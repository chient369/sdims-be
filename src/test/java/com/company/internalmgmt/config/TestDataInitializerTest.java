package com.company.internalmgmt.config;

import com.company.internalmgmt.modules.admin.repository.UserRepository;
import com.company.internalmgmt.modules.opportunity.repository.OpportunityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test for the TestDataInitializer class
 */
@SpringBootTest
@ActiveProfiles("test")
public class TestDataInitializerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Test
    @Transactional
    public void testDataShouldBeInitialized() {
        // Test that data was initialized
        assertTrue(userRepository.count() > 0, "Users should be initialized");
        assertTrue(opportunityRepository.count() > 0, "Opportunities should be initialized");

        // Test that specific test data exists
        assertTrue(userRepository.findByUsername("admin").isPresent(), "Admin user should exist");
        assertTrue(userRepository.findByUsername("sales").isPresent(), "Sales user should exist");
        assertTrue(userRepository.findByUsername("leader").isPresent(), "Leader user should exist");
    }
} 