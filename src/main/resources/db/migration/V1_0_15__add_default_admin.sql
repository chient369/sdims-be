-- Add default admin user
-- Script created on: 2023-11-23

-- Add admin user with encrypted password
-- Password: admin123 (encrypted with BCrypt)
INSERT INTO users (
    username, 
    email, 
    password, 
    full_name, 
    enabled, 
    account_non_expired, 
    account_non_locked, 
    credentials_non_expired, 
    created_at, 
    updated_at
) VALUES (
    'admin', 
    'admin@company.com', 
    '$2a$10$T/AZjLCPf4oxQOX0bHNO8u/pBAxwu.eC1KKmCnHHbIwLi9Dzd.XOe', -- BCrypt hash for 'admin123'
    'System Administrator', 
    TRUE, 
    TRUE, 
    TRUE, 
    TRUE, 
    NOW(), 
    NOW()
);

-- Get the admin user ID
SET @admin_user_id = (SELECT id FROM users WHERE username = 'admin');

-- Get the admin role ID
SET @admin_role_id = (SELECT id FROM roles WHERE name = 'Admin');

-- Assign admin role to admin user
INSERT INTO user_roles (user_id, role_id, created_at, updated_at) 
VALUES (@admin_user_id, @admin_role_id, NOW(), NOW()); 