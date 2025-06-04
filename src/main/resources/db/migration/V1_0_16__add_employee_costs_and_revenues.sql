-- Add test data for employee costs and revenues
-- Script created on: 2023-11-23

-- Get admin user id for created_by field
SET @admin_id = (SELECT id FROM users WHERE username = 'admin');

-- Insert employee costs for Backend team members
-- First get employee IDs
SET @backend_lead_id = (SELECT id FROM employees WHERE employee_code = 'EMP006');
SET @backend_senior_id = (SELECT id FROM employees WHERE employee_code = 'EMP018');
SET @backend_mid_id = (SELECT id FROM employees WHERE employee_code = 'EMP019');
SET @backend_junior_id = (SELECT id FROM employees WHERE employee_code = 'EMP020');
SET @backend_bench_id = (SELECT id FROM employees WHERE employee_code = 'EMP028');

-- Insert costs for Backend team
-- Team lead
INSERT INTO employee_costs (employee_id, year, month, cost_amount, basic_salary, allowance, overtime, other_costs, currency, note, created_by, updated_by, created_at, updated_at) VALUES
(@backend_lead_id, 2023, 10, 35000000, 30000000, 3000000, 0, 2000000, 'VND', 'Monthly cost October 2023', @admin_id, @admin_id, NOW(), NOW()),
(@backend_lead_id, 2023, 11, 35000000, 30000000, 3000000, 0, 2000000, 'VND', 'Monthly cost November 2023', @admin_id, @admin_id, NOW(), NOW());

-- Senior developer
INSERT INTO employee_costs (employee_id, year, month, cost_amount, basic_salary, allowance, overtime, other_costs, currency, note, created_by, updated_by, created_at, updated_at) VALUES
(@backend_senior_id, 2023, 10, 28000000, 25000000, 2000000, 0, 1000000, 'VND', 'Monthly cost October 2023', @admin_id, @admin_id, NOW(), NOW()),
(@backend_senior_id, 2023, 11, 28000000, 25000000, 2000000, 0, 1000000, 'VND', 'Monthly cost November 2023', @admin_id, @admin_id, NOW(), NOW());

-- Mid-level developer
INSERT INTO employee_costs (employee_id, year, month, cost_amount, basic_salary, allowance, overtime, other_costs, currency, note, created_by, updated_by, created_at, updated_at) VALUES
(@backend_mid_id, 2023, 10, 22000000, 20000000, 1500000, 0, 500000, 'VND', 'Monthly cost October 2023', @admin_id, @admin_id, NOW(), NOW()),
(@backend_mid_id, 2023, 11, 22000000, 20000000, 1500000, 0, 500000, 'VND', 'Monthly cost November 2023', @admin_id, @admin_id, NOW(), NOW());

-- Junior developer
INSERT INTO employee_costs (employee_id, year, month, cost_amount, basic_salary, allowance, overtime, other_costs, currency, note, created_by, updated_by, created_at, updated_at) VALUES
(@backend_junior_id, 2023, 10, 16500000, 15000000, 1000000, 0, 500000, 'VND', 'Monthly cost October 2023', @admin_id, @admin_id, NOW(), NOW()),
(@backend_junior_id, 2023, 11, 16500000, 15000000, 1000000, 0, 500000, 'VND', 'Monthly cost November 2023', @admin_id, @admin_id, NOW(), NOW());

-- Bench developer
INSERT INTO employee_costs (employee_id, year, month, cost_amount, basic_salary, allowance, overtime, other_costs, currency, note, created_by, updated_by, created_at, updated_at) VALUES
(@backend_bench_id, 2023, 10, 22000000, 20000000, 1500000, 0, 500000, 'VND', 'Monthly cost October 2023', @admin_id, @admin_id, NOW(), NOW()),
(@backend_bench_id, 2023, 11, 22000000, 20000000, 1500000, 0, 500000, 'VND', 'Monthly cost November 2023', @admin_id, @admin_id, NOW(), NOW());

-- Insert employee costs for Frontend team members
-- First get employee IDs
SET @frontend_lead_id = (SELECT id FROM employees WHERE employee_code = 'EMP007');
SET @frontend_senior_id = (SELECT id FROM employees WHERE employee_code = 'EMP021');
SET @frontend_mid_id = (SELECT id FROM employees WHERE employee_code = 'EMP022');
SET @frontend_junior_id = (SELECT id FROM employees WHERE employee_code = 'EMP023');
SET @frontend_end_soon_id = (SELECT id FROM employees WHERE employee_code = 'EMP029');

-- Insert costs for Frontend team
-- Team lead
INSERT INTO employee_costs (employee_id, year, month, cost_amount, basic_salary, allowance, overtime, other_costs, currency, note, created_by, updated_by, created_at, updated_at) VALUES
(@frontend_lead_id, 2023, 10, 33000000, 28000000, 3000000, 0, 2000000, 'VND', 'Monthly cost October 2023', @admin_id, @admin_id, NOW(), NOW()),
(@frontend_lead_id, 2023, 11, 33000000, 28000000, 3000000, 0, 2000000, 'VND', 'Monthly cost November 2023', @admin_id, @admin_id, NOW(), NOW());

-- Senior developer
INSERT INTO employee_costs (employee_id, year, month, cost_amount, basic_salary, allowance, overtime, other_costs, currency, note, created_by, updated_by, created_at, updated_at) VALUES
(@frontend_senior_id, 2023, 10, 27500000, 24000000, 2500000, 0, 1000000, 'VND', 'Monthly cost October 2023', @admin_id, @admin_id, NOW(), NOW()),
(@frontend_senior_id, 2023, 11, 27500000, 24000000, 2500000, 0, 1000000, 'VND', 'Monthly cost November 2023', @admin_id, @admin_id, NOW(), NOW());

-- Mid-level developer
INSERT INTO employee_costs (employee_id, year, month, cost_amount, basic_salary, allowance, overtime, other_costs, currency, note, created_by, updated_by, created_at, updated_at) VALUES
(@frontend_mid_id, 2023, 10, 21000000, 19000000, 1500000, 0, 500000, 'VND', 'Monthly cost October 2023', @admin_id, @admin_id, NOW(), NOW()),
(@frontend_mid_id, 2023, 11, 21000000, 19000000, 1500000, 0, 500000, 'VND', 'Monthly cost November 2023', @admin_id, @admin_id, NOW(), NOW());

-- Junior developer
INSERT INTO employee_costs (employee_id, year, month, cost_amount, basic_salary, allowance, overtime, other_costs, currency, note, created_by, updated_by, created_at, updated_at) VALUES
(@frontend_junior_id, 2023, 10, 16000000, 14500000, 1000000, 0, 500000, 'VND', 'Monthly cost October 2023', @admin_id, @admin_id, NOW(), NOW()),
(@frontend_junior_id, 2023, 11, 16000000, 14500000, 1000000, 0, 500000, 'VND', 'Monthly cost November 2023', @admin_id, @admin_id, NOW(), NOW());

-- EndingSoon developer
INSERT INTO employee_costs (employee_id, year, month, cost_amount, basic_salary, allowance, overtime, other_costs, currency, note, created_by, updated_by, created_at, updated_at) VALUES
(@frontend_end_soon_id, 2023, 10, 21000000, 19000000, 1500000, 0, 500000, 'VND', 'Monthly cost October 2023', @admin_id, @admin_id, NOW(), NOW()),
(@frontend_end_soon_id, 2023, 11, 21000000, 19000000, 1500000, 0, 500000, 'VND', 'Monthly cost November 2023', @admin_id, @admin_id, NOW(), NOW());

-- Create mock contracts for revenue records
INSERT INTO contracts (name, contract_number, client_name, start_date, end_date, status, created_by, updated_by, created_at, updated_at) VALUES
('Project Alpha', 'CT-2023-001', 'Client A Corporation', '2023-01-01', '2023-12-31', 'ACTIVE', @admin_id, @admin_id, NOW(), NOW()),
('Project Beta', 'CT-2023-002', 'Client B Corporation', '2023-03-01', '2024-02-28', 'ACTIVE', @admin_id, @admin_id, NOW(), NOW()),
('Project Gamma', 'CT-2023-003', 'Client C Ltd', '2023-06-01', '2023-12-15', 'ACTIVE', @admin_id, @admin_id, NOW(), NOW()),
('Project Delta', 'CT-2023-004', 'Client D Inc', '2023-05-01', '2024-04-30', 'ACTIVE', @admin_id, @admin_id, NOW(), NOW());

-- Get contract IDs for revenue records
SET @contract_alpha_id = (SELECT id FROM contracts WHERE contract_number = 'CT-2023-001');
SET @contract_beta_id = (SELECT id FROM contracts WHERE contract_number = 'CT-2023-002');
SET @contract_gamma_id = (SELECT id FROM contracts WHERE contract_number = 'CT-2023-003');
SET @contract_delta_id = (SELECT id FROM contracts WHERE contract_number = 'CT-2023-004');

-- Insert employee revenues
-- Backend Lead - High margin (Project Alpha - 100% allocation)
INSERT INTO employee_revenues (employee_id, contract_id, year, month, billing_rate, allocation_percentage, calculated_revenue, currency, note, created_by, updated_by, created_at, updated_at) VALUES
(@backend_lead_id, @contract_alpha_id, 2023, 10, 4000, 100, 4000, 'USD', 'Team Lead Revenue - October 2023', @admin_id, @admin_id, NOW(), NOW()),
(@backend_lead_id, @contract_alpha_id, 2023, 11, 4000, 100, 4000, 'USD', 'Team Lead Revenue - November 2023', @admin_id, @admin_id, NOW(), NOW());

-- Backend Senior - Good margin (Project Beta - 100% allocation)
INSERT INTO employee_revenues (employee_id, contract_id, year, month, billing_rate, allocation_percentage, calculated_revenue, currency, note, created_by, updated_by, created_at, updated_at) VALUES
(@backend_senior_id, @contract_beta_id, 2023, 10, 3200, 100, 3200, 'USD', 'Senior Dev Revenue - October 2023', @admin_id, @admin_id, NOW(), NOW()),
(@backend_senior_id, @contract_beta_id, 2023, 11, 3200, 100, 3200, 'USD', 'Senior Dev Revenue - November 2023', @admin_id, @admin_id, NOW(), NOW());

-- Backend Mid - Split between two projects (Project Alpha - 50%, Project Gamma - 50%)
INSERT INTO employee_revenues (employee_id, contract_id, year, month, billing_rate, allocation_percentage, calculated_revenue, currency, note, created_by, updated_by, created_at, updated_at) VALUES
(@backend_mid_id, @contract_alpha_id, 2023, 10, 2400, 50, 1200, 'USD', 'Mid Dev Revenue (Project Alpha) - October 2023', @admin_id, @admin_id, NOW(), NOW()),
(@backend_mid_id, @contract_alpha_id, 2023, 11, 2400, 50, 1200, 'USD', 'Mid Dev Revenue (Project Alpha) - November 2023', @admin_id, @admin_id, NOW(), NOW()),
(@backend_mid_id, @contract_gamma_id, 2023, 10, 2300, 50, 1150, 'USD', 'Mid Dev Revenue (Project Gamma) - October 2023', @admin_id, @admin_id, NOW(), NOW()),
(@backend_mid_id, @contract_gamma_id, 2023, 11, 2300, 50, 1150, 'USD', 'Mid Dev Revenue (Project Gamma) - November 2023', @admin_id, @admin_id, NOW(), NOW());

-- Backend Junior - Low margin (Project Gamma - 100% allocation)
INSERT INTO employee_revenues (employee_id, contract_id, year, month, billing_rate, allocation_percentage, calculated_revenue, currency, note, created_by, updated_by, created_at, updated_at) VALUES
(@backend_junior_id, @contract_gamma_id, 2023, 10, 1800, 100, 1800, 'USD', 'Junior Dev Revenue - October 2023', @admin_id, @admin_id, NOW(), NOW()),
(@backend_junior_id, @contract_gamma_id, 2023, 11, 1800, 100, 1800, 'USD', 'Junior Dev Revenue - November 2023', @admin_id, @admin_id, NOW(), NOW());

-- Frontend Lead - High margin (Project Beta - 100% allocation)
INSERT INTO employee_revenues (employee_id, contract_id, year, month, billing_rate, allocation_percentage, calculated_revenue, currency, note, created_by, updated_by, created_at, updated_at) VALUES
(@frontend_lead_id, @contract_beta_id, 2023, 10, 3800, 100, 3800, 'USD', 'Team Lead Revenue - October 2023', @admin_id, @admin_id, NOW(), NOW()),
(@frontend_lead_id, @contract_beta_id, 2023, 11, 3800, 100, 3800, 'USD', 'Team Lead Revenue - November 2023', @admin_id, @admin_id, NOW(), NOW());

-- Frontend Senior - Good margin (Project Alpha - 100% allocation)
INSERT INTO employee_revenues (employee_id, contract_id, year, month, billing_rate, allocation_percentage, calculated_revenue, currency, note, created_by, updated_by, created_at, updated_at) VALUES
(@frontend_senior_id, @contract_alpha_id, 2023, 10, 3100, 100, 3100, 'USD', 'Senior Dev Revenue - October 2023', @admin_id, @admin_id, NOW(), NOW()),
(@frontend_senior_id, @contract_alpha_id, 2023, 11, 3100, 100, 3100, 'USD', 'Senior Dev Revenue - November 2023', @admin_id, @admin_id, NOW(), NOW());

-- Frontend Mid - Good margin (Project Delta - 100% allocation)
INSERT INTO employee_revenues (employee_id, contract_id, year, month, billing_rate, allocation_percentage, calculated_revenue, currency, note, created_by, updated_by, created_at, updated_at) VALUES
(@frontend_mid_id, @contract_delta_id, 2023, 10, 2400, 100, 2400, 'USD', 'Mid Dev Revenue - October 2023', @admin_id, @admin_id, NOW(), NOW()),
(@frontend_mid_id, @contract_delta_id, 2023, 11, 2400, 100, 2400, 'USD', 'Mid Dev Revenue - November 2023', @admin_id, @admin_id, NOW(), NOW());

-- Frontend Junior - Moderate margin (Project Delta - 100% allocation)
INSERT INTO employee_revenues (employee_id, contract_id, year, month, billing_rate, allocation_percentage, calculated_revenue, currency, note, created_by, updated_by, created_at, updated_at) VALUES
(@frontend_junior_id, @contract_delta_id, 2023, 10, 1900, 100, 1900, 'USD', 'Junior Dev Revenue - October 2023', @admin_id, @admin_id, NOW(), NOW()),
(@frontend_junior_id, @contract_delta_id, 2023, 11, 1900, 100, 1900, 'USD', 'Junior Dev Revenue - November 2023', @admin_id, @admin_id, NOW(), NOW());

-- EndingSoon developer - ending in December (Project Gamma - 100% allocation)
INSERT INTO employee_revenues (employee_id, contract_id, year, month, billing_rate, allocation_percentage, calculated_revenue, currency, note, created_by, updated_by, created_at, updated_at) VALUES
(@frontend_end_soon_id, @contract_gamma_id, 2023, 10, 2200, 100, 2200, 'USD', 'Mid Dev Revenue - October 2023', @admin_id, @admin_id, NOW(), NOW()),
(@frontend_end_soon_id, @contract_gamma_id, 2023, 11, 2200, 100, 2200, 'USD', 'Mid Dev Revenue - November 2023', @admin_id, @admin_id, NOW(), NOW()); 