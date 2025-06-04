package com.company.internalmgmt.modules.admin.service;

import com.company.internalmgmt.modules.admin.dto.UserDto;
import com.company.internalmgmt.modules.admin.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * Service interface for managing users.
 */
public interface UserService extends UserDetailsService {

    /**
     * Get all users with pagination
     *
     * @param pageable pagination information
     * @return paginated list of users
     */
    Page<UserDto> findAll(Pageable pageable);

    /**
     * Get all users
     *
     * @return list of all users
     */
    List<UserDto> findAll();

    /**
     * Get user by ID
     *
     * @param id the user ID
     * @return the user
     */
    UserDto findById(Long id);

    /**
     * Get user entity by ID (used by other services)
     *
     * @param id the user ID
     * @return the user entity
     */
    User findUserById(Long id);

    /**
     * Get user by username
     *
     * @param username the username
     * @return the user
     */
    UserDto findByUsername(String username);

    /**
     * Get user by email
     *
     * @param email the email
     * @return the user
     */
    UserDto findByEmail(String email);

    /**
     * Create a new user
     *
     * @param userDto the user to create
     * @return the created user
     */
    UserDto create(UserDto userDto);

    /**
     * Update an existing user
     *
     * @param id the user ID
     * @param userDto the user data to update
     * @return the updated user
     */
    UserDto update(Long id, UserDto userDto);

    /**
     * Delete a user
     *
     * @param id the user ID
     */
    void delete(Long id);

    /**
     * Activate a user
     *
     * @param id the user ID
     * @return the activated user
     */
    UserDto activate(Long id);

    /**
     * Deactivate a user
     *
     * @param id the user ID
     * @return the deactivated user
     */
    UserDto deactivate(Long id);

    /**
     * Change user password
     *
     * @param id the user ID
     * @param currentPassword the current password
     * @param newPassword the new password
     * @return the updated user
     */
    UserDto changePassword(Long id, String currentPassword, String newPassword);

    /**
     * Reset user password
     *
     * @param id the user ID
     * @return the temporary password
     */
    String resetPassword(Long id);

    /**
     * Check if username exists
     *
     * @param username the username
     * @return true if exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     *
     * @param email the email
     * @return true if exists
     */
    boolean existsByEmail(String email);
} 