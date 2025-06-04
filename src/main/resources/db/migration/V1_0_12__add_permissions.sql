-- Add permissions based on permissions_definition.md
-- Script created on: 2023-11-23

-- HRM Module Permissions
INSERT INTO permissions (name, description, created_at, updated_at) VALUES
-- Employee permissions
('employee:read:all', 'View information of all employees', NOW(), NOW()),
('employee:read:team', 'View information of employees in team', NOW(), NOW()),
('employee:read:basic', 'View basic information of employees', NOW(), NOW()),
('employee:read:own', 'View own information', NOW(), NOW()),
('employee:create', 'Create new employee', NOW(), NOW()),
('employee:update:all', 'Update information of all employees', NOW(), NOW()),
('employee:update:team', 'Update information of employees in team', NOW(), NOW()),
('employee:update:own', 'Update own information', NOW(), NOW()),
('employee:delete', 'Delete employee (soft delete)', NOW(), NOW()),
('employee:import', 'Import employee list from file', NOW(), NOW()),
('employee:export', 'Export employee list', NOW(), NOW()),

-- Skill category permissions
('skill-category:read', 'View skill categories', NOW(), NOW()),
('skill-category:create', 'Create skill category', NOW(), NOW()),
('skill-category:update', 'Update skill category', NOW(), NOW()),
('skill-category:delete', 'Delete skill category', NOW(), NOW()),

-- Skill permissions
('skill:read', 'View skills', NOW(), NOW()),
('skill:create', 'Create skill', NOW(), NOW()),
('skill:update', 'Update skill', NOW(), NOW()),
('skill:delete', 'Delete skill', NOW(), NOW()),

-- Employee skill permissions
('employee-skill:read:all', 'View skills of all employees', NOW(), NOW()),
('employee-skill:read:team', 'View skills of employees in team', NOW(), NOW()),
('employee-skill:read:own', 'View own skills', NOW(), NOW()),
('employee-skill:create:all', 'Add skill for any employee', NOW(), NOW()),
('employee-skill:create:team', 'Add skill for employee in team', NOW(), NOW()),
('employee-skill:create:own', 'Add own skill', NOW(), NOW()),
('employee-skill:update:all', 'Update skill of any employee', NOW(), NOW()),
('employee-skill:update:team', 'Update skill of employee in team', NOW(), NOW()),
('employee-skill:update:own', 'Update own skill', NOW(), NOW()),
('employee-skill:delete:all', 'Delete skill of any employee', NOW(), NOW()),
('employee-skill:delete:team', 'Delete skill of employee in team', NOW(), NOW()),
('employee-skill:delete:own', 'Delete own skill', NOW(), NOW()),
('employee-skill:evaluate', 'Evaluate employee skills', NOW(), NOW()),

-- Employee suggest permissions
('employee-suggest:read', 'View employee suggestions', NOW(), NOW()),

-- Employee status permissions
('employee-status:read:all', 'View status and project allocation of all employees', NOW(), NOW()),
('employee-status:read:team', 'View status and project allocation of employees in team', NOW(), NOW()),
('employee-status:read:own', 'View own status and project allocation', NOW(), NOW()),
('employee-status:update:all', 'Update status and project allocation of all employees', NOW(), NOW()),
('employee-status:update:team', 'Update status and project allocation of employees in team', NOW(), NOW()),

-- Project history permissions
('project-history:read:all', 'View project history of all employees', NOW(), NOW()),
('project-history:read:team', 'View project history of employees in team', NOW(), NOW()),
('project-history:read:own', 'View own project history', NOW(), NOW()),

-- Utilization permissions
('utilization:read:all', 'View utilization report of all employees', NOW(), NOW()),
('utilization:read:team', 'View utilization report of team', NOW(), NOW()),

-- Employee alert permissions
('employee-alert:read:all', 'View alerts for employees ending projects soon (all)', NOW(), NOW()),
('employee-alert:read:team', 'View alerts for employees ending projects soon (team)', NOW(), NOW()),

-- Margin Module Permissions
-- Employee cost permissions
('employee-cost:read:all', 'View costs of all employees', NOW(), NOW()),
('employee-cost:read:team', 'View costs of employees in team', NOW(), NOW()),
('employee-cost:create', 'Create employee cost', NOW(), NOW()),
('employee-cost:update:all', 'Update cost of all employees', NOW(), NOW()),
('employee-cost:delete', 'Delete employee cost', NOW(), NOW()),
('employee-cost:import', 'Import employee costs from file', NOW(), NOW()),

-- Revenue permissions
('revenue:read:all', 'View revenue of all employees', NOW(), NOW()),
('revenue:read:team', 'View revenue of employees in team', NOW(), NOW()),

-- Margin permissions
('margin:read:all', 'View margin of all employees', NOW(), NOW()),
('margin:read:team', 'View margin of employees in team', NOW(), NOW()),
('margin-summary:read:all', 'View overall margin summary', NOW(), NOW()),
('margin-summary:read:team', 'View team margin summary', NOW(), NOW()),
('margin-alert:read:all', 'View all margin alerts', NOW(), NOW()),
('margin-alert:read:team', 'View team margin alerts', NOW(), NOW()),
('margin-alert:config', 'Configure low margin notifications', NOW(), NOW()),

-- Opportunity Module Permissions
-- Opportunity permissions
('opportunity:read:all', 'View all business opportunities', NOW(), NOW()),
('opportunity:create', 'Create business opportunity', NOW(), NOW()),
('opportunity:update:all', 'Update all business opportunities', NOW(), NOW()),
('opportunity:update:own', 'Update own business opportunities', NOW(), NOW()),
('opportunity:update:assigned', 'Update assigned business opportunities', NOW(), NOW()),
('opportunity:delete', 'Delete business opportunity', NOW(), NOW()),

-- Opportunity sync permissions
('opportunity-sync:read', 'View Hubspot sync information', NOW(), NOW()),
('opportunity-sync:config', 'Configure Hubspot sync', NOW(), NOW()),
('opportunity-log:read:all', 'View Hubspot sync logs', NOW(), NOW()),

-- Opportunity assignment permissions
('opportunity-assign:update:all', 'Assign leader to opportunity (any)', NOW(), NOW()),

-- Opportunity note permissions
('opportunity-note:create:all', 'Add note to any opportunity', NOW(), NOW()),
('opportunity-note:create:assigned', 'Add note to assigned opportunity', NOW(), NOW()),
('opportunity-note:read:all', 'View notes of all opportunities', NOW(), NOW()),
('opportunity-note:read:assigned', 'View notes of assigned opportunities', NOW(), NOW()),

-- Opportunity followup permissions
('opportunity-followup:read:all', 'View follow-up status of all opportunities', NOW(), NOW()),
('opportunity-onsite:update:all', 'Update onsite priority status (any)', NOW(), NOW()),
('opportunity-onsite:update:assigned', 'Update onsite priority status (assigned)', NOW(), NOW()),
('opportunity-alert:config', 'Configure opportunity notifications', NOW(), NOW()),
('opportunity-alert:read:all', 'View all opportunity notifications', NOW(), NOW()),
('opportunity-alert:read:assigned', 'View notifications for assigned opportunities', NOW(), NOW()),

-- Contract Module Permissions
-- Contract permissions
('contract:read:all', 'View all contracts', NOW(), NOW()),
('contract:read:own', 'View own contracts', NOW(), NOW()),
('contract:read:assigned', 'View assigned contracts', NOW(), NOW()),
('contract:create', 'Create new contract', NOW(), NOW()),
('contract:update:all', 'Update all contracts', NOW(), NOW()),
('contract:update:own', 'Update own contracts', NOW(), NOW()),
('contract:delete', 'Delete contract', NOW(), NOW()),

-- Contract link permissions
('contract-link:update:all', 'Link contract with opportunity/employee (any)', NOW(), NOW()),
('contract-link:update:own', 'Link contract with opportunity/employee (own)', NOW(), NOW()),
('contract-link:update:assigned', 'Link contract with opportunity/employee (assigned)', NOW(), NOW()),

-- Contract file permissions
('contract-file:read:all', 'View attachments (all contracts)', NOW(), NOW()),
('contract-file:read:own', 'View attachments (own contracts)', NOW(), NOW()),
('contract-file:read:assigned', 'View attachments (assigned contracts)', NOW(), NOW()),
('contract-file:create:all', 'Upload file for any contract', NOW(), NOW()),
('contract-file:create:own', 'Upload file for own contract', NOW(), NOW()),
('contract-file:delete:all', 'Delete attachment of any contract', NOW(), NOW()),
('contract-file:delete:own', 'Delete attachment of own contract', NOW(), NOW()),

-- Payment term permissions
('payment-term:read:all', 'View payment terms (all)', NOW(), NOW()),
('payment-term:read:own', 'View payment terms (own contracts)', NOW(), NOW()),
('payment-term:read:assigned', 'View payment terms (assigned contracts)', NOW(), NOW()),
('payment-term:create:all', 'Add payment term (any contract)', NOW(), NOW()),
('payment-term:create:own', 'Add payment term (own contract)', NOW(), NOW()),
('payment-term:update:all', 'Update payment term (any)', NOW(), NOW()),
('payment-term:update:own', 'Update payment term (own contract)', NOW(), NOW()),
('payment-term:delete:all', 'Delete payment term (any)', NOW(), NOW()),
('payment-term:delete:own', 'Delete payment term (own contract)', NOW(), NOW()),

-- Payment status permissions
('payment-status:update:all', 'Update payment status (any)', NOW(), NOW()),
('payment-status:import', 'Import payment status from file', NOW(), NOW()),

-- Payment alert permissions
('payment-alert:read:all', 'View payment alerts (all)', NOW(), NOW()),
('payment-alert:read:own', 'View payment alerts (own contracts)', NOW(), NOW()),
('payment-alert:read:assigned', 'View payment alerts (assigned contracts)', NOW(), NOW()),
('payment-alert:config', 'Configure payment alerts', NOW(), NOW()),

-- Debt report permissions
('debt-report:read:all', 'View debt report (all)', NOW(), NOW()),
('debt-report:read:own', 'View debt report (own)', NOW(), NOW()),

-- Sales KPI permissions
('sales-kpi:read:all', 'View revenue KPI (all)', NOW(), NOW()),
('sales-kpi:read:own', 'View revenue KPI (own)', NOW(), NOW()),
('sales-kpi:create', 'Set revenue KPI', NOW(), NOW()),
('sales-kpi:update', 'Update revenue KPI', NOW(), NOW()),
('sales-kpi:delete', 'Delete revenue KPI', NOW(), NOW()),

-- Revenue report permissions
('revenue-report:read:all', 'View actual revenue report (all)', NOW(), NOW()),
('revenue-report:read:team', 'View actual revenue report (team)', NOW(), NOW()),
('revenue-report:read:own', 'View actual revenue report (own)', NOW(), NOW()),
('revenue-summary:read:all', 'View revenue summary (all)', NOW(), NOW()),
('revenue-summary:read:team', 'View revenue summary (team)', NOW(), NOW()),
('revenue-summary:read:own', 'View revenue summary (own)', NOW(), NOW()),

-- Dashboard & Report Module Permissions
-- Dashboard permissions
('dashboard:read:all', 'View summary dashboard (all data)', NOW(), NOW()),
('dashboard:read:team', 'View summary dashboard (team data)', NOW(), NOW()),
('dashboard:read:own', 'View summary dashboard (personal data)', NOW(), NOW()),
('report:read:all', 'View detailed report (all)', NOW(), NOW()),
('report:read:team', 'View detailed report (team)', NOW(), NOW()),
('report:read:own', 'View detailed report (own)', NOW(), NOW()),
('report:export', 'Export report to file', NOW(), NOW()),

-- System Admin Module Permissions
-- User permissions
('user:read', 'View user list', NOW(), NOW()),
('user:create', 'Create new user', NOW(), NOW()),
('user:update', 'Update user information', NOW(), NOW()),
('user:delete', 'Delete user', NOW(), NOW()),

-- Role permissions
('role:read', 'View role list', NOW(), NOW()),
('role:create', 'Create new role', NOW(), NOW()),
('role:update', 'Update role', NOW(), NOW()),
('role:delete', 'Delete role', NOW(), NOW()),

-- Permission permissions
('permission:read', 'View permission list', NOW(), NOW()),
('permission:assign', 'Assign permission to role', NOW(), NOW()),

-- Config permissions
('config:read', 'View system configuration', NOW(), NOW()),
('config:update', 'Update system configuration', NOW(), NOW()),
('alert-threshold:read', 'View alert thresholds', NOW(), NOW()),
('alert-threshold:update', 'Update alert thresholds', NOW(), NOW()),
('api-connect:read', 'View API connection information', NOW(), NOW()),
('api-connect:update', 'Update API connection information', NOW(), NOW()),
('system-log:read:all', 'View system log (all)', NOW(), NOW()),
('system-log:read:limited', 'View system log (limited)', NOW(), NOW()); 