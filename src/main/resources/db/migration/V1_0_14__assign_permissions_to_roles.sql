-- Assign permissions to roles based on permissions_definition.md
-- Script created on: 2023-11-23

-- Procedure to assign permissions to a role
DELIMITER //
CREATE PROCEDURE assign_permissions_to_role(IN role_name VARCHAR(100), IN permission_pattern VARCHAR(100))
BEGIN
    DECLARE role_id BIGINT;
    
    -- Get role id
    SELECT id INTO role_id FROM roles WHERE name = role_name;
    
    -- Insert permissions that match the pattern
    INSERT INTO role_permissions (role_id, permission_id, created_at, updated_at)
    SELECT role_id, p.id, NOW(), NOW()
    FROM permissions p
    WHERE p.name LIKE permission_pattern
    AND NOT EXISTS (
        SELECT 1 FROM role_permissions rp 
        WHERE rp.role_id = role_id AND rp.permission_id = p.id
    );
END //
DELIMITER ;

-- Assign all permissions to Admin role
SET @admin_role_id = (SELECT id FROM roles WHERE name = 'Admin');
INSERT INTO role_permissions (role_id, permission_id, created_at, updated_at)
SELECT @admin_role_id, p.id, NOW(), NOW()
FROM permissions p
WHERE NOT EXISTS (
    SELECT 1 FROM role_permissions rp 
    WHERE rp.role_id = @admin_role_id AND rp.permission_id = p.id
);

-- Assign Division Manager permissions
-- First get the role ID
SET @manager_role_id = (SELECT id FROM roles WHERE name = 'Division Manager');

-- HRM Module - Division Manager permissions
CALL assign_permissions_to_role('Division Manager', 'employee:read:all');
CALL assign_permissions_to_role('Division Manager', 'employee:create');
CALL assign_permissions_to_role('Division Manager', 'employee:update:all');
CALL assign_permissions_to_role('Division Manager', 'employee:delete');
CALL assign_permissions_to_role('Division Manager', 'employee:import');
CALL assign_permissions_to_role('Division Manager', 'employee:export');
CALL assign_permissions_to_role('Division Manager', 'skill-category:%');
CALL assign_permissions_to_role('Division Manager', 'skill:%');
CALL assign_permissions_to_role('Division Manager', 'employee-skill:read:all');
CALL assign_permissions_to_role('Division Manager', 'employee-skill:create:all');
CALL assign_permissions_to_role('Division Manager', 'employee-skill:update:all');
CALL assign_permissions_to_role('Division Manager', 'employee-skill:delete:all');
CALL assign_permissions_to_role('Division Manager', 'employee-skill:evaluate');
CALL assign_permissions_to_role('Division Manager', 'employee-suggest:read');
CALL assign_permissions_to_role('Division Manager', 'employee-status:read:all');
CALL assign_permissions_to_role('Division Manager', 'employee-status:update:all');
CALL assign_permissions_to_role('Division Manager', 'project-history:read:all');
CALL assign_permissions_to_role('Division Manager', 'utilization:read:all');
CALL assign_permissions_to_role('Division Manager', 'employee-alert:read:all');

-- Margin Module - Division Manager permissions
CALL assign_permissions_to_role('Division Manager', 'employee-cost:read:all');
CALL assign_permissions_to_role('Division Manager', 'employee-cost:create');
CALL assign_permissions_to_role('Division Manager', 'employee-cost:update:all');
CALL assign_permissions_to_role('Division Manager', 'employee-cost:delete');
CALL assign_permissions_to_role('Division Manager', 'employee-cost:import');
CALL assign_permissions_to_role('Division Manager', 'revenue:read:all');
CALL assign_permissions_to_role('Division Manager', 'margin:read:all');
CALL assign_permissions_to_role('Division Manager', 'margin-summary:read:all');
CALL assign_permissions_to_role('Division Manager', 'margin-alert:read:all');
CALL assign_permissions_to_role('Division Manager', 'margin-alert:config');

-- Opportunity Module - Division Manager permissions
CALL assign_permissions_to_role('Division Manager', 'opportunity:read:all');
CALL assign_permissions_to_role('Division Manager', 'opportunity:update:all');
CALL assign_permissions_to_role('Division Manager', 'opportunity:delete');
CALL assign_permissions_to_role('Division Manager', 'opportunity-sync:read');
CALL assign_permissions_to_role('Division Manager', 'opportunity-sync:config');
CALL assign_permissions_to_role('Division Manager', 'opportunity-log:read:all');
CALL assign_permissions_to_role('Division Manager', 'opportunity-assign:update:all');
CALL assign_permissions_to_role('Division Manager', 'opportunity-note:create:all');
CALL assign_permissions_to_role('Division Manager', 'opportunity-note:read:all');
CALL assign_permissions_to_role('Division Manager', 'opportunity-followup:read:all');
CALL assign_permissions_to_role('Division Manager', 'opportunity-onsite:update:all');
CALL assign_permissions_to_role('Division Manager', 'opportunity-alert:config');
CALL assign_permissions_to_role('Division Manager', 'opportunity-alert:read:all');

-- Contract Module - Division Manager permissions
CALL assign_permissions_to_role('Division Manager', 'contract:read:all');
CALL assign_permissions_to_role('Division Manager', 'contract:create');
CALL assign_permissions_to_role('Division Manager', 'contract:update:all');
CALL assign_permissions_to_role('Division Manager', 'contract:delete');
CALL assign_permissions_to_role('Division Manager', 'contract-link:update:all');
CALL assign_permissions_to_role('Division Manager', 'contract-file:read:all');
CALL assign_permissions_to_role('Division Manager', 'contract-file:create:all');
CALL assign_permissions_to_role('Division Manager', 'contract-file:delete:all');
CALL assign_permissions_to_role('Division Manager', 'payment-term:read:all');
CALL assign_permissions_to_role('Division Manager', 'payment-term:create:all');
CALL assign_permissions_to_role('Division Manager', 'payment-term:update:all');
CALL assign_permissions_to_role('Division Manager', 'payment-term:delete:all');
CALL assign_permissions_to_role('Division Manager', 'payment-status:update:all');
CALL assign_permissions_to_role('Division Manager', 'payment-status:import');
CALL assign_permissions_to_role('Division Manager', 'payment-alert:read:all');
CALL assign_permissions_to_role('Division Manager', 'payment-alert:config');
CALL assign_permissions_to_role('Division Manager', 'debt-report:read:all');
CALL assign_permissions_to_role('Division Manager', 'sales-kpi:read:all');
CALL assign_permissions_to_role('Division Manager', 'sales-kpi:create');
CALL assign_permissions_to_role('Division Manager', 'sales-kpi:update');
CALL assign_permissions_to_role('Division Manager', 'sales-kpi:delete');
CALL assign_permissions_to_role('Division Manager', 'revenue-report:read:all');
CALL assign_permissions_to_role('Division Manager', 'revenue-summary:read:all');

-- Dashboard & Report - Division Manager permissions
CALL assign_permissions_to_role('Division Manager', 'dashboard:read:all');
CALL assign_permissions_to_role('Division Manager', 'report:read:all');
CALL assign_permissions_to_role('Division Manager', 'report:export');

-- System - Division Manager permissions
CALL assign_permissions_to_role('Division Manager', 'config:read');
CALL assign_permissions_to_role('Division Manager', 'alert-threshold:read');
CALL assign_permissions_to_role('Division Manager', 'api-connect:read');
CALL assign_permissions_to_role('Division Manager', 'system-log:read:limited');

-- Assign Leader permissions
-- HRM Module - Leader permissions
CALL assign_permissions_to_role('Leader', 'employee:read:team');
CALL assign_permissions_to_role('Leader', 'employee:update:team');
CALL assign_permissions_to_role('Leader', 'employee:export');
CALL assign_permissions_to_role('Leader', 'skill-category:read');
CALL assign_permissions_to_role('Leader', 'skill:read');
CALL assign_permissions_to_role('Leader', 'employee-skill:read:team');
CALL assign_permissions_to_role('Leader', 'employee-skill:create:team');
CALL assign_permissions_to_role('Leader', 'employee-skill:update:team');
CALL assign_permissions_to_role('Leader', 'employee-skill:delete:team');
CALL assign_permissions_to_role('Leader', 'employee-skill:evaluate');
CALL assign_permissions_to_role('Leader', 'employee-suggest:read');
CALL assign_permissions_to_role('Leader', 'employee-status:read:team');
CALL assign_permissions_to_role('Leader', 'employee-status:update:team');
CALL assign_permissions_to_role('Leader', 'project-history:read:team');
CALL assign_permissions_to_role('Leader', 'utilization:read:team');
CALL assign_permissions_to_role('Leader', 'employee-alert:read:team');

-- Margin Module - Leader permissions
CALL assign_permissions_to_role('Leader', 'employee-cost:read:team');
CALL assign_permissions_to_role('Leader', 'revenue:read:team');
CALL assign_permissions_to_role('Leader', 'margin:read:team');
CALL assign_permissions_to_role('Leader', 'margin-summary:read:team');
CALL assign_permissions_to_role('Leader', 'margin-alert:read:team');

-- Opportunity Module - Leader permissions
CALL assign_permissions_to_role('Leader', 'opportunity:read:all');
CALL assign_permissions_to_role('Leader', 'opportunity:update:assigned');
CALL assign_permissions_to_role('Leader', 'opportunity-sync:read');
CALL assign_permissions_to_role('Leader', 'opportunity-log:read:all');
CALL assign_permissions_to_role('Leader', 'opportunity-note:create:assigned');
CALL assign_permissions_to_role('Leader', 'opportunity-note:read:assigned');
CALL assign_permissions_to_role('Leader', 'opportunity-followup:read:all');
CALL assign_permissions_to_role('Leader', 'opportunity-onsite:update:assigned');
CALL assign_permissions_to_role('Leader', 'opportunity-alert:read:assigned');

-- Contract Module - Leader permissions
CALL assign_permissions_to_role('Leader', 'contract:read:assigned');
CALL assign_permissions_to_role('Leader', 'contract-link:update:assigned');
CALL assign_permissions_to_role('Leader', 'contract-file:read:assigned');
CALL assign_permissions_to_role('Leader', 'payment-term:read:assigned');
CALL assign_permissions_to_role('Leader', 'payment-alert:read:assigned');
CALL assign_permissions_to_role('Leader', 'revenue-report:read:team');
CALL assign_permissions_to_role('Leader', 'revenue-summary:read:team');

-- Dashboard & Report - Leader permissions
CALL assign_permissions_to_role('Leader', 'dashboard:read:team');
CALL assign_permissions_to_role('Leader', 'report:read:team');
CALL assign_permissions_to_role('Leader', 'report:export');

-- System - Leader permissions
CALL assign_permissions_to_role('Leader', 'config:read');
CALL assign_permissions_to_role('Leader', 'alert-threshold:read');

-- Assign Sales permissions
-- HRM Module - Sales permissions
CALL assign_permissions_to_role('Sales', 'employee:read:basic');
CALL assign_permissions_to_role('Sales', 'employee:read:own');
CALL assign_permissions_to_role('Sales', 'employee:update:own');

-- Opportunity Module - Sales permissions
CALL assign_permissions_to_role('Sales', 'opportunity:read:all');
CALL assign_permissions_to_role('Sales', 'opportunity:create');
CALL assign_permissions_to_role('Sales', 'opportunity:update:own');
CALL assign_permissions_to_role('Sales', 'opportunity-sync:read');
CALL assign_permissions_to_role('Sales', 'opportunity-log:read:all');
CALL assign_permissions_to_role('Sales', 'opportunity-assign:update:all');
CALL assign_permissions_to_role('Sales', 'opportunity-note:create:all');
CALL assign_permissions_to_role('Sales', 'opportunity-note:read:all');
CALL assign_permissions_to_role('Sales', 'opportunity-followup:read:all');
CALL assign_permissions_to_role('Sales', 'opportunity-onsite:update:all');
CALL assign_permissions_to_role('Sales', 'opportunity-alert:read:all');

-- Contract Module - Sales permissions
CALL assign_permissions_to_role('Sales', 'contract:read:own');
CALL assign_permissions_to_role('Sales', 'contract:create');
CALL assign_permissions_to_role('Sales', 'contract:update:own');
CALL assign_permissions_to_role('Sales', 'contract-link:update:own');
CALL assign_permissions_to_role('Sales', 'contract-file:read:own');
CALL assign_permissions_to_role('Sales', 'contract-file:create:own');
CALL assign_permissions_to_role('Sales', 'contract-file:delete:own');
CALL assign_permissions_to_role('Sales', 'payment-term:read:own');
CALL assign_permissions_to_role('Sales', 'payment-term:create:own');
CALL assign_permissions_to_role('Sales', 'payment-term:update:own');
CALL assign_permissions_to_role('Sales', 'payment-term:delete:own');
CALL assign_permissions_to_role('Sales', 'payment-status:update:all');
CALL assign_permissions_to_role('Sales', 'payment-status:import');
CALL assign_permissions_to_role('Sales', 'payment-alert:read:own');
CALL assign_permissions_to_role('Sales', 'debt-report:read:own');
CALL assign_permissions_to_role('Sales', 'sales-kpi:read:own');
CALL assign_permissions_to_role('Sales', 'revenue-report:read:own');
CALL assign_permissions_to_role('Sales', 'revenue-summary:read:own');

-- Dashboard & Report - Sales permissions
CALL assign_permissions_to_role('Sales', 'dashboard:read:own');
CALL assign_permissions_to_role('Sales', 'report:read:own');
CALL assign_permissions_to_role('Sales', 'report:export');

-- Assign Employee permissions
-- HRM Module - Employee permissions
CALL assign_permissions_to_role('Employee', 'employee:read:own');
CALL assign_permissions_to_role('Employee', 'employee:update:own');
CALL assign_permissions_to_role('Employee', 'skill-category:read');
CALL assign_permissions_to_role('Employee', 'skill:read');
CALL assign_permissions_to_role('Employee', 'employee-skill:read:own');
CALL assign_permissions_to_role('Employee', 'employee-skill:create:own');
CALL assign_permissions_to_role('Employee', 'employee-skill:update:own');
CALL assign_permissions_to_role('Employee', 'employee-skill:delete:own');
CALL assign_permissions_to_role('Employee', 'employee-status:read:own');
CALL assign_permissions_to_role('Employee', 'project-history:read:own');

-- Drop the temporary procedure
DROP PROCEDURE IF EXISTS assign_permissions_to_role; 