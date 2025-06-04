-- Test data script for Contract Module
-- Sử dụng ID user hiện có trong hệ thống (6, 7, 12)

-- Bỏ qua insert test users vì đã có sẵn trong DB
-- Insert test teams
INSERT INTO teams (id, name, description, created_at, updated_at)
VALUES 
(1, 'Development Team', 'Software development team', NOW(), NOW()),
(2, 'QA Team', 'Quality assurance team', NOW(), NOW()),
(3, 'DevOps Team', 'DevOps and infrastructure team', NOW(), NOW());

-- Insert test employees (nếu chưa có)
INSERT INTO employees (id, employee_code, first_name, last_name, company_email, position, team_id, current_status, created_at, updated_at)
VALUES 
(1, 'EMP001', 'Nguyen', 'Van A', 'employee1@company.com', 'Java Developer', 1, 'ACTIVE', NOW(), NOW()),
(2, 'EMP002', 'Tran', 'Thi B', 'employee2@company.com', 'QA Engineer', 2, 'ACTIVE', NOW(), NOW()),
(3, 'EMP003', 'Le', 'Van C', 'employee3@company.com', 'DevOps Engineer', 3, 'ACTIVE', NOW(), NOW()),
(4, 'EMP004', 'Pham', 'Thi D', 'employee4@company.com', 'Senior Java Developer', 1, 'ACTIVE', NOW(), NOW()),
(5, 'EMP005', 'Hoang', 'Van E', 'employee5@company.com', 'Frontend Developer', 1, 'ACTIVE', NOW(), NOW()),
(6, 'EMP006', 'Nguyen', 'Thi F', 'employee6@company.com', 'QA Lead', 2, 'ACTIVE', NOW(), NOW()),
(7, 'EMP007', 'Vu', 'Van G', 'employee7@company.com', 'Backend Developer', 1, 'ACTIVE', NOW(), NOW()),
(8, 'EMP008', 'Dang', 'Van H', 'employee8@company.com', 'Project Manager', 1, 'ACTIVE', NOW(), NOW());

-- Insert test opportunities (nếu chưa có)
INSERT INTO opportunities (id, opportunity_code, name, customer_name, stage, source, created_at, updated_at, created_by, updated_by)
VALUES 
(1, 'OPP001', 'E-commerce Website Development', 'ABC Corp', 'Closed Won', 'Website', NOW(), NOW(), 6, 6),
(2, 'OPP002', 'Mobile App Development', 'XYZ Inc', 'Closed Won', 'Referral', NOW(), NOW(), 7, 7),
(3, 'OPP003', 'System Integration Project', 'DEF Ltd', 'Closed Won', 'Conference', NOW(), NOW(), 6, 6),
(4, 'OPP004', 'Cloud Migration Project', 'GHI Company', 'Closed Won', 'Cold Call', NOW(), NOW(), 7, 7),
(5, 'OPP005', 'AI Implementation', 'JKL Corp', 'Negotiation', 'Website', NOW(), NOW(), 6, 6);

-- Insert test contracts
-- Đảm bảo created_by và updated_by tham chiếu đến ID có trong bảng users
INSERT INTO contracts (id, contract_code, name, client_name, opportunity_id, sign_date, effective_date, expiry_date, 
                      total_value, currency, contract_type, assigned_sales_id, status, description, created_at, updated_at, created_by, updated_by)
VALUES 
-- Contract 1
(1, 'CTR-2025-001', 'E-commerce Development Contract', 'ABC Corp', 1, '2025-01-15', '2025-01-20', '2025-07-31', 
   150000000, 'VND', 'FixedPrice', 6, 'Active', 'Development of e-commerce platform with payment integration', NOW(), NOW(), 6, 6),

-- Contract 2
(2, 'CTR-2025-002', 'Mobile App Development Contract', 'XYZ Inc', 2, '2025-02-01', '2025-02-10', '2025-08-10', 
   200000000, 'VND', 'TimeAndMaterial', 7, 'Active', 'Development of iOS and Android mobile applications', NOW(), NOW(), 7, 7),

-- Contract 3
(3, 'CTR-2025-003', 'System Integration Project', 'DEF Ltd', 3, '2025-01-20', '2025-02-01', '2025-06-30', 
   300000000, 'VND', 'FixedPrice', 6, 'Active', 'Integration of legacy systems with new ERP', NOW(), NOW(), 6, 6),

-- Contract 4
(4, 'CTR-2025-004', 'Cloud Migration Project', 'GHI Company', 4, '2025-02-15', '2025-03-01', '2025-09-30', 
   250000000, 'VND', 'TimeAndMaterial', 7, 'Active', 'Migration of on-premise systems to AWS cloud', NOW(), NOW(), 7, 7),

-- Contract 5
(5, 'CTR-2025-005', 'DevOps Consulting', 'MNO Ltd', NULL, '2025-03-01', '2025-03-15', '2025-06-15', 
   100000000, 'VND', 'Retainer', 6, 'Draft', 'Consulting services for DevOps implementation', NOW(), NOW(), 6, 6),

-- Contract 6
(6, 'CTR-2025-006', 'Web Portal Maintenance', 'PQR Inc', NULL, '2025-01-10', '2025-01-15', '2026-01-14', 
   120000000, 'VND', 'Maintenance', 7, 'Active', 'Annual maintenance contract for web portal', NOW(), NOW(), 7, 7),

-- Contract 7
(7, 'CTR-2025-007', 'Data Analytics Project', 'STU Corp', NULL, '2025-02-20', '2025-03-01', '2025-08-31', 
   180000000, 'VND', 'FixedPrice', 6, 'InReview', 'Implementation of data analytics platform', NOW(), NOW(), 6, 6),

-- Contract 8
(8, 'CTR-2025-008', 'Software Testing Services', 'VWX Ltd', NULL, '2025-03-10', '2025-03-15', '2025-09-15', 
   90000000, 'VND', 'TimeAndMaterial', 7, 'Draft', 'Outsourced testing services for client applications', NOW(), NOW(), 7, 7),

-- Contract 9
(9, 'CTR-2025-009', 'IT Infrastructure Setup', 'YZA Company', NULL, '2025-01-25', '2025-02-10', '2025-05-10', 
   220000000, 'VND', 'FixedPrice', 6, 'Active', 'Setting up IT infrastructure for new office', NOW(), NOW(), 6, 6),

-- Contract 10
(10, 'CTR-2025-010', 'Security Audit Services', 'BCD Inc', NULL, '2025-02-28', '2025-03-10', '2025-06-10', 
    85000000, 'VND', 'Retainer', 7, 'Approved', 'Quarterly security audit and penetration testing', NOW(), NOW(), 7, 7);

-- Insert payment terms for each contract
-- Contract 1 Payment Terms
INSERT INTO contract_payment_terms (id, contract_id, term_number, description, expected_payment_date, expected_amount, currency, payment_status, created_at, updated_at, created_by, updated_by)
VALUES 
(1, 1, 1, 'Advance payment', '2025-01-25', 45000000, 'VND', 'paid', NOW(), NOW(), 6, 6),
(2, 1, 2, 'Milestone 1 completion', '2025-03-15', 60000000, 'VND', 'unpaid', NOW(), NOW(), 6, 6),
(3, 1, 3, 'Final delivery', '2025-07-31', 45000000, 'VND', 'unpaid', NOW(), NOW(), 6, 6);

-- Contract 2 Payment Terms
INSERT INTO contract_payment_terms (id, contract_id, term_number, description, expected_payment_date, expected_amount, currency, payment_status, created_at, updated_at, created_by, updated_by)
VALUES 
(4, 2, 1, 'Advance payment', '2025-02-15', 60000000, 'VND', 'paid', NOW(), NOW(), 7, 7),
(5, 2, 2, 'Monthly payment - March', '2025-04-10', 40000000, 'VND', 'unpaid', NOW(), NOW(), 7, 7),
(6, 2, 3, 'Monthly payment - April', '2025-05-10', 40000000, 'VND', 'unpaid', NOW(), NOW(), 7, 7),
(7, 2, 4, 'Monthly payment - May', '2025-06-10', 40000000, 'VND', 'unpaid', NOW(), NOW(), 7, 7),
(8, 2, 5, 'Final payment', '2025-08-10', 20000000, 'VND', 'unpaid', NOW(), NOW(), 7, 7);

-- Contract 3 Payment Terms
INSERT INTO contract_payment_terms (id, contract_id, term_number, description, expected_payment_date, expected_amount, currency, payment_status, created_at, updated_at, created_by, updated_by)
VALUES 
(9, 3, 1, 'Advance payment', '2025-02-10', 90000000, 'VND', 'paid', NOW(), NOW(), 6, 6),
(10, 3, 2, 'Milestone 1: Analysis completed', '2025-03-15', 60000000, 'VND', 'unpaid', NOW(), NOW(), 6, 6),
(11, 3, 3, 'Milestone 2: Integration completed', '2025-05-15', 90000000, 'VND', 'unpaid', NOW(), NOW(), 6, 6),
(12, 3, 4, 'Final delivery', '2025-06-30', 60000000, 'VND', 'unpaid', NOW(), NOW(), 6, 6);

-- Contract 4 Payment Terms
INSERT INTO contract_payment_terms (id, contract_id, term_number, description, expected_payment_date, expected_amount, currency, payment_status, created_at, updated_at, created_by, updated_by)
VALUES 
(13, 4, 1, 'Advance payment', '2025-03-10', 75000000, 'VND', 'paid', NOW(), NOW(), 7, 7),
(14, 4, 2, 'Monthly payment - March', '2025-04-10', 35000000, 'VND', 'unpaid', NOW(), NOW(), 7, 7),
(15, 4, 3, 'Monthly payment - April', '2025-05-10', 35000000, 'VND', 'unpaid', NOW(), NOW(), 7, 7),
(16, 4, 4, 'Monthly payment - May', '2025-06-10', 35000000, 'VND', 'unpaid', NOW(), NOW(), 7, 7),
(17, 4, 5, 'Monthly payment - June', '2025-07-10', 35000000, 'VND', 'unpaid', NOW(), NOW(), 7, 7),
(18, 4, 6, 'Final payment', '2025-09-30', 35000000, 'VND', 'unpaid', NOW(), NOW(), 7, 7);

-- Contract 5 Payment Terms
INSERT INTO contract_payment_terms (id, contract_id, term_number, description, expected_payment_date, expected_amount, currency, payment_status, created_at, updated_at, created_by, updated_by)
VALUES 
(19, 5, 1, 'Advance payment', '2025-03-15', 30000000, 'VND', 'unpaid', NOW(), NOW(), 6, 6),
(20, 5, 2, 'Monthly payment - April', '2025-04-15', 20000000, 'VND', 'unpaid', NOW(), NOW(), 6, 6),
(21, 5, 3, 'Monthly payment - May', '2025-05-15', 20000000, 'VND', 'unpaid', NOW(), NOW(), 6, 6),
(22, 5, 4, 'Final payment', '2025-06-15', 30000000, 'VND', 'unpaid', NOW(), NOW(), 6, 6);

-- Insert employee assignments to contracts
-- Contract 1 Employees
INSERT INTO contract_employees (id, contract_id, employee_id, role, allocation_percentage, start_date, end_date, bill_rate, created_at, updated_at)
VALUES 
(1, 1, 1, 'Backend Developer', 100.00, '2025-01-20', '2025-07-31', 250000.00, NOW(), NOW()),
(2, 1, 5, 'Frontend Developer', 100.00, '2025-01-20', '2025-07-31', 230000.00, NOW(), NOW()),
(3, 1, 2, 'QA Engineer', 50.00, '2025-01-20', '2025-07-31', 220000.00, NOW(), NOW());

-- Contract 2 Employees
INSERT INTO contract_employees (id, contract_id, employee_id, role, allocation_percentage, start_date, end_date, bill_rate, created_at, updated_at)
VALUES 
(4, 2, 4, 'Senior Developer', 100.00, '2025-02-10', '2025-08-10', 300000.00, NOW(), NOW()),
(5, 2, 7, 'Backend Developer', 100.00, '2025-02-10', '2025-08-10', 250000.00, NOW(), NOW()),
(6, 2, 6, 'QA Lead', 50.00, '2025-02-10', '2025-08-10', 270000.00, NOW(), NOW());

-- Contract 3 Employees
INSERT INTO contract_employees (id, contract_id, employee_id, role, allocation_percentage, start_date, end_date, bill_rate, created_at, updated_at)
VALUES 
(7, 3, 8, 'Project Manager', 50.00, '2025-02-01', '2025-06-30', 350000.00, NOW(), NOW()),
(8, 3, 1, 'Backend Developer', 100.00, '2025-02-01', '2025-06-30', 250000.00, NOW(), NOW()),
(9, 3, 2, 'QA Engineer', 50.00, '2025-02-01', '2025-06-30', 220000.00, NOW(), NOW());

-- Contract 4 Employees
INSERT INTO contract_employees (id, contract_id, employee_id, role, allocation_percentage, start_date, end_date, bill_rate, created_at, updated_at)
VALUES 
(10, 4, 3, 'DevOps Engineer', 100.00, '2025-03-01', '2025-09-30', 280000.00, NOW(), NOW()),
(11, 4, 8, 'Project Manager', 25.00, '2025-03-01', '2025-09-30', 350000.00, NOW(), NOW());

-- Contract 9 Employees
INSERT INTO contract_employees (id, contract_id, employee_id, role, allocation_percentage, start_date, end_date, bill_rate, created_at, updated_at)
VALUES 
(12, 9, 3, 'DevOps Engineer', 75.00, '2025-02-10', '2025-05-10', 280000.00, NOW(), NOW());

-- Contract 10 Employees
INSERT INTO contract_employees (id, contract_id, employee_id, role, allocation_percentage, start_date, end_date, bill_rate, created_at, updated_at)
VALUES 
(13, 10, 7, 'Security Specialist', 50.00, '2025-03-10', '2025-06-10', 300000.00, NOW(), NOW()); 