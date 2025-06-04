-- Thêm dữ liệu test cho bảng employees và liên kết với teams

-- Tạo employees cho các nhóm
-- Lấy ID của các team
SET @engineering_id = (SELECT id FROM teams WHERE name = 'Engineering');
SET @backend_id = (SELECT id FROM teams WHERE name = 'Backend');
SET @frontend_id = (SELECT id FROM teams WHERE name = 'Frontend');
SET @devops_id = (SELECT id FROM teams WHERE name = 'DevOps');
SET @qa_id = (SELECT id FROM teams WHERE name = 'QA');
SET @sales_id = (SELECT id FROM teams WHERE name = 'Sales');
SET @corporate_sales_id = (SELECT id FROM teams WHERE name = 'Corporate Sales');
SET @partner_sales_id = (SELECT id FROM teams WHERE name = 'Partner Sales');
SET @hr_admin_id = (SELECT id FROM teams WHERE name = 'HR & Admin');
SET @recruitment_id = (SELECT id FROM teams WHERE name = 'Recruitment');
SET @office_id = (SELECT id FROM teams WHERE name = 'Office Management');
SET @product_id = (SELECT id FROM teams WHERE name = 'Product');
SET @product_mgmt_id = (SELECT id FROM teams WHERE name = 'Product Management');
SET @ux_id = (SELECT id FROM teams WHERE name = 'UX/UI Design');
SET @marketing_id = (SELECT id FROM teams WHERE name = 'Marketing');
SET @digital_id = (SELECT id FROM teams WHERE name = 'Digital Marketing');
SET @content_id = (SELECT id FROM teams WHERE name = 'Content');

-- Thêm employees - Leaders của các phòng ban chính
INSERT INTO employees (employee_code, first_name, last_name, birth_date, hire_date, company_email, internal_account, position, team_id, reporting_leader_id, current_status, status_updated_at, created_at, updated_at) VALUES
('EMP001', 'Nguyễn', 'Văn A', '1980-01-15', '2015-05-10', 'nguyena@company.com', 'nguyena', 'CTO', @engineering_id, NULL, 'Active', NOW(), NOW(), NOW()),
('EMP002', 'Trần', 'Thị B', '1982-03-20', '2016-02-15', 'tranb@company.com', 'tranb', 'Sales Director', @sales_id, NULL, 'Active', NOW(), NOW(), NOW()),
('EMP003', 'Lê', 'Văn C', '1985-07-05', '2017-01-10', 'lec@company.com', 'lec', 'HR Manager', @hr_admin_id, NULL, 'Active', NOW(), NOW(), NOW()),
('EMP004', 'Phạm', 'Thị D', '1983-11-12', '2016-06-20', 'phamd@company.com', 'phamd', 'Product Director', @product_id, NULL, 'Active', NOW(), NOW(), NOW()),
('EMP005', 'Hoàng', 'Văn E', '1987-09-25', '2018-03-15', 'hoange@company.com', 'hoange', 'Marketing Manager', @marketing_id, NULL, 'Active', NOW(), NOW(), NOW());

-- Update leaders cho các phòng ban chính
UPDATE teams SET leader_id = (SELECT id FROM employees WHERE employee_code = 'EMP001') WHERE name = 'Engineering';
UPDATE teams SET leader_id = (SELECT id FROM employees WHERE employee_code = 'EMP002') WHERE name = 'Sales';
UPDATE teams SET leader_id = (SELECT id FROM employees WHERE employee_code = 'EMP003') WHERE name = 'HR & Admin';
UPDATE teams SET leader_id = (SELECT id FROM employees WHERE employee_code = 'EMP004') WHERE name = 'Product';
UPDATE teams SET leader_id = (SELECT id FROM employees WHERE employee_code = 'EMP005') WHERE name = 'Marketing';

-- Thêm team leaders cho các team con
INSERT INTO employees (employee_code, first_name, last_name, birth_date, hire_date, company_email, internal_account, position, team_id, reporting_leader_id, current_status, status_updated_at, created_at, updated_at) VALUES
('EMP006', 'Đỗ', 'Văn F', '1988-04-18', '2018-07-10', 'dof@company.com', 'dof', 'Backend Lead', @backend_id, (SELECT id FROM employees WHERE employee_code = 'EMP001'), 'Active', NOW(), NOW(), NOW()),
('EMP007', 'Ngô', 'Thị G', '1990-06-22', '2019-01-15', 'ngog@company.com', 'ngog', 'Frontend Lead', @frontend_id, (SELECT id FROM employees WHERE employee_code = 'EMP001'), 'Active', NOW(), NOW(), NOW()),
('EMP008', 'Vũ', 'Văn H', '1986-08-30', '2017-11-01', 'vuh@company.com', 'vuh', 'DevOps Lead', @devops_id, (SELECT id FROM employees WHERE employee_code = 'EMP001'), 'Active', NOW(), NOW(), NOW()),
('EMP009', 'Đặng', 'Thị I', '1991-10-05', '2019-09-15', 'dangi@company.com', 'dangi', 'QA Lead', @qa_id, (SELECT id FROM employees WHERE employee_code = 'EMP001'), 'Active', NOW(), NOW(), NOW()),
('EMP010', 'Bùi', 'Văn J', '1984-12-08', '2017-05-20', 'buij@company.com', 'buij', 'Corporate Sales Lead', @corporate_sales_id, (SELECT id FROM employees WHERE employee_code = 'EMP002'), 'Active', NOW(), NOW(), NOW()),
('EMP011', 'Lý', 'Thị K', '1989-02-14', '2018-10-01', 'lyk@company.com', 'lyk', 'Partner Sales Lead', @partner_sales_id, (SELECT id FROM employees WHERE employee_code = 'EMP002'), 'Active', NOW(), NOW(), NOW()),
('EMP012', 'Hồ', 'Văn L', '1992-04-25', '2020-01-10', 'hol@company.com', 'hol', 'Recruitment Lead', @recruitment_id, (SELECT id FROM employees WHERE employee_code = 'EMP003'), 'Active', NOW(), NOW(), NOW()),
('EMP013', 'Mai', 'Thị M', '1990-07-30', '2019-04-15', 'maim@company.com', 'maim', 'Office Manager', @office_id, (SELECT id FROM employees WHERE employee_code = 'EMP003'), 'Active', NOW(), NOW(), NOW()),
('EMP014', 'Trịnh', 'Văn N', '1988-09-15', '2018-06-01', 'trinhn@company.com', 'trinhn', 'Product Manager', @product_mgmt_id, (SELECT id FROM employees WHERE employee_code = 'EMP004'), 'Active', NOW(), NOW(), NOW()),
('EMP015', 'Cao', 'Thị O', '1993-11-20', '2020-05-15', 'caoo@company.com', 'caoo', 'UX/UI Lead', @ux_id, (SELECT id FROM employees WHERE employee_code = 'EMP004'), 'Active', NOW(), NOW(), NOW()),
('EMP016', 'Đinh', 'Văn P', '1991-01-10', '2019-08-01', 'dinhp@company.com', 'dinhp', 'Digital Marketing Lead', @digital_id, (SELECT id FROM employees WHERE employee_code = 'EMP005'), 'Active', NOW(), NOW(), NOW()),
('EMP017', 'Dương', 'Thị Q', '1994-03-05', '2020-10-10', 'duongq@company.com', 'duongq', 'Content Lead', @content_id, (SELECT id FROM employees WHERE employee_code = 'EMP005'), 'Active', NOW(), NOW(), NOW());

-- Update leaders cho các team con
UPDATE teams SET leader_id = (SELECT id FROM employees WHERE employee_code = 'EMP006') WHERE name = 'Backend';
UPDATE teams SET leader_id = (SELECT id FROM employees WHERE employee_code = 'EMP007') WHERE name = 'Frontend';
UPDATE teams SET leader_id = (SELECT id FROM employees WHERE employee_code = 'EMP008') WHERE name = 'DevOps';
UPDATE teams SET leader_id = (SELECT id FROM employees WHERE employee_code = 'EMP009') WHERE name = 'QA';
UPDATE teams SET leader_id = (SELECT id FROM employees WHERE employee_code = 'EMP010') WHERE name = 'Corporate Sales';
UPDATE teams SET leader_id = (SELECT id FROM employees WHERE employee_code = 'EMP011') WHERE name = 'Partner Sales';
UPDATE teams SET leader_id = (SELECT id FROM employees WHERE employee_code = 'EMP012') WHERE name = 'Recruitment';
UPDATE teams SET leader_id = (SELECT id FROM employees WHERE employee_code = 'EMP013') WHERE name = 'Office Management';
UPDATE teams SET leader_id = (SELECT id FROM employees WHERE employee_code = 'EMP014') WHERE name = 'Product Management';
UPDATE teams SET leader_id = (SELECT id FROM employees WHERE employee_code = 'EMP015') WHERE name = 'UX/UI Design';
UPDATE teams SET leader_id = (SELECT id FROM employees WHERE employee_code = 'EMP016') WHERE name = 'Digital Marketing';
UPDATE teams SET leader_id = (SELECT id FROM employees WHERE employee_code = 'EMP017') WHERE name = 'Content';

-- Thêm một số nhân viên trong từng team
INSERT INTO employees (employee_code, first_name, last_name, birth_date, hire_date, company_email, internal_account, position, team_id, reporting_leader_id, current_status, status_updated_at, created_at, updated_at) VALUES
-- Backend Team
('EMP018', 'Nguyễn', 'Văn R', '1995-05-15', '2021-01-10', 'nguyenr@company.com', 'nguyenr', 'Senior Backend Developer', @backend_id, (SELECT id FROM employees WHERE employee_code = 'EMP006'), 'Active', NOW(), NOW(), NOW()),
('EMP019', 'Trần', 'Thị S', '1996-07-20', '2021-03-15', 'trans@company.com', 'trans', 'Backend Developer', @backend_id, (SELECT id FROM employees WHERE employee_code = 'EMP006'), 'Active', NOW(), NOW(), NOW()),
('EMP020', 'Lê', 'Văn T', '1997-09-25', '2021-06-01', 'let@company.com', 'let', 'Junior Backend Developer', @backend_id, (SELECT id FROM employees WHERE employee_code = 'EMP006'), 'Active', NOW(), NOW(), NOW()),

-- Frontend Team
('EMP021', 'Phạm', 'Thị U', '1994-02-10', '2020-08-15', 'phamu@company.com', 'phamu', 'Senior Frontend Developer', @frontend_id, (SELECT id FROM employees WHERE employee_code = 'EMP007'), 'Active', NOW(), NOW(), NOW()),
('EMP022', 'Hoàng', 'Văn V', '1995-04-15', '2020-11-01', 'hoangv@company.com', 'hoangv', 'Frontend Developer', @frontend_id, (SELECT id FROM employees WHERE employee_code = 'EMP007'), 'Active', NOW(), NOW(), NOW()),
('EMP023', 'Đỗ', 'Thị X', '1996-06-20', '2021-02-15', 'dox@company.com', 'dox', 'Junior Frontend Developer', @frontend_id, (SELECT id FROM employees WHERE employee_code = 'EMP007'), 'Active', NOW(), NOW(), NOW()),

-- DevOps Team
('EMP024', 'Ngô', 'Văn Y', '1993-08-25', '2020-05-10', 'ngoy@company.com', 'ngoy', 'Senior DevOps Engineer', @devops_id, (SELECT id FROM employees WHERE employee_code = 'EMP008'), 'Active', NOW(), NOW(), NOW()),
('EMP025', 'Vũ', 'Thị Z', '1994-10-30', '2020-07-15', 'vuz@company.com', 'vuz', 'DevOps Engineer', @devops_id, (SELECT id FROM employees WHERE employee_code = 'EMP008'), 'Active', NOW(), NOW(), NOW()),

-- QA Team
('EMP026', 'Đặng', 'Văn AA', '1995-12-05', '2020-09-01', 'dangaa@company.com', 'dangaa', 'Senior QA Engineer', @qa_id, (SELECT id FROM employees WHERE employee_code = 'EMP009'), 'Active', NOW(), NOW(), NOW()),
('EMP027', 'Bùi', 'Thị BB', '1997-02-10', '2021-04-15', 'buibb@company.com', 'buibb', 'QA Engineer', @qa_id, (SELECT id FROM employees WHERE employee_code = 'EMP009'), 'Active', NOW(), NOW(), NOW());

-- Thêm một số nhân viên đang bench hoặc sắp kết thúc dự án
INSERT INTO employees (employee_code, first_name, last_name, birth_date, hire_date, company_email, internal_account, position, team_id, reporting_leader_id, current_status, status_updated_at, created_at, updated_at) VALUES
('EMP028', 'Lý', 'Văn CC', '1996-04-15', '2021-07-01', 'lycc@company.com', 'lycc', 'Backend Developer', @backend_id, (SELECT id FROM employees WHERE employee_code = 'EMP006'), 'Bench', NOW(), NOW(), NOW()),
('EMP029', 'Hồ', 'Thị DD', '1995-06-20', '2020-12-15', 'hodd@company.com', 'hodd', 'Frontend Developer', @frontend_id, (SELECT id FROM employees WHERE employee_code = 'EMP007'), 'EndingSoon', NOW(), NOW(), NOW()),
('EMP030', 'Mai', 'Văn EE', '1994-08-25', '2020-10-01', 'maiee@company.com', 'maiee', 'QA Engineer', @qa_id, (SELECT id FROM employees WHERE employee_code = 'EMP009'), 'Bench', NOW(), NOW(), NOW()); 