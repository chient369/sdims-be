-- Add roles based on permissions_definition.md
-- Script created on: 2023-11-23

-- Create basic roles
INSERT INTO roles (name, description, created_at, updated_at) VALUES
('Admin', 'System administrator with full permissions', NOW(), NOW()),
('Division Manager', 'Department manager with access to all department data', NOW(), NOW()),
('Leader', 'Team leader with team-scoped permissions', NOW(), NOW()),
('Sales', 'Sales staff with own-scoped and sales-specific permissions', NOW(), NOW()),
('Employee', 'Regular employee with basic permissions', NOW(), NOW()); 