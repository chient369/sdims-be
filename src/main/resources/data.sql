-- Table: contracts
CREATE TABLE IF NOT EXISTS contracts (
  id BIGSERIAL,
  contract_code VARCHAR(100) NOT NULL,
  name VARCHAR(255) NOT NULL,
  client_name VARCHAR(255) NOT NULL,
  opportunity_id BIGINT,
  sign_date DATE,
  effective_date DATE,
  expiry_date DATE,
  total_value DECIMAL(18,2),
  currency VARCHAR(3),
  contract_type VARCHAR(50),
  assigned_sales_id BIGINT,
  status VARCHAR(50),
  description TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  created_by BIGINT,
  updated_by BIGINT,
  deleted_at TIMESTAMPTZ,
  PRIMARY KEY (id)
);

-- Table: contract_employees
CREATE TABLE IF NOT EXISTS contract_employees (
  id BIGSERIAL,
  contract_id BIGINT NOT NULL,
  employee_id BIGINT NOT NULL,
  role VARCHAR(100),
  allocation_percentage DECIMAL(5,2),
  start_date DATE,
  end_date DATE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  PRIMARY KEY (id)
);

-- Table: contract_files
CREATE TABLE IF NOT EXISTS contract_files (
  id BIGSERIAL,
  contract_id BIGINT NOT NULL,
  file_name VARCHAR(255) NOT NULL,
  file_path VARCHAR(500) NOT NULL,
  file_type VARCHAR(100),
  file_size BIGINT,
  uploaded_by_id BIGINT NOT NULL,
  uploaded_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  PRIMARY KEY (id)
);

-- Table: contract_payment_terms
CREATE TABLE IF NOT EXISTS contract_payment_terms (
  id BIGSERIAL,
  contract_id BIGINT NOT NULL,
  description VARCHAR(255) NOT NULL,
  expected_payment_date DATE NOT NULL,
  expected_amount DECIMAL(15,2) NOT NULL,
  currency VARCHAR(3) NOT NULL,
  payment_status VARCHAR(50) NOT NULL,
  actual_payment_date DATE,
  actual_amount_paid DECIMAL(15,2),
  notes TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  created_by BIGINT,
  updated_by BIGINT,
  PRIMARY KEY (id)
);

-- Table: employees
CREATE TABLE IF NOT EXISTS employees (
  id BIGSERIAL,
  user_id BIGINT,
  employee_code VARCHAR(50) NOT NULL,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  birth_date DATE,
  hire_date DATE,
  company_email VARCHAR(255) NOT NULL,
  internal_account VARCHAR(100),
  address TEXT,
  phone_number VARCHAR(50),
  emergency_contact TEXT,
  position VARCHAR(100),
  team VARCHAR(100),
  reporting_leader_id BIGINT,
  current_status VARCHAR(50),
  status_updated_at TIMESTAMPTZ,
  profile_picture_url VARCHAR(500),
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  created_by BIGINT,
  updated_by BIGINT,
  deleted_at TIMESTAMPTZ,
  PRIMARY KEY (id)
);

-- Table: employee_costs
CREATE TABLE IF NOT EXISTS employee_costs (
  id BIGSERIAL,
  employee_id BIGINT NOT NULL,
  year INTEGER NOT NULL,
  month INTEGER NOT NULL,
  cost_amount DECIMAL(15,2) NOT NULL,
  currency VARCHAR(3) NOT NULL,
  description TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  PRIMARY KEY (id)
);

-- Table: employee_revenues
CREATE TABLE IF NOT EXISTS employee_revenues (
  id BIGSERIAL,
  employee_id BIGINT NOT NULL,
  contract_id BIGINT NOT NULL,
  year INTEGER NOT NULL,
  month INTEGER NOT NULL,
  billing_rate DECIMAL(15,2),
  allocation_percentage DECIMAL(5,2),
  calculated_revenue DECIMAL(15,2) NOT NULL,
  currency VARCHAR(3) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  PRIMARY KEY (id)
);

-- Table: employee_skills
CREATE TABLE IF NOT EXISTS employee_skills (
  id BIGSERIAL,
  employee_id BIGINT NOT NULL,
  skill_id BIGINT NOT NULL,
  years_experience DECIMAL(4,1),
  self_assessment_level VARCHAR(50),
  leader_assessment_level VARCHAR(50),
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  PRIMARY KEY (id)
);

-- Table: employee_status_log
CREATE TABLE IF NOT EXISTS employee_status_log (
  id BIGSERIAL,
  employee_id BIGINT NOT NULL,
  status VARCHAR(50) NOT NULL,
  project_name VARCHAR(255),
  allocation_percentage DECIMAL(5,2),
  expected_end_date DATE,
  log_timestamp TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  PRIMARY KEY (id)
);

-- Table: notifications
CREATE TABLE IF NOT EXISTS notifications (
  id BIGSERIAL,
  user_id BIGINT NOT NULL,
  message TEXT NOT NULL,
  is_read BOOLEAN NOT NULL,
  related_entity_type VARCHAR(100),
  related_entity_id BIGINT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  PRIMARY KEY (id)
);

-- Table: opportunities
CREATE TABLE IF NOT EXISTS opportunities (
  id BIGSERIAL,
  hubspot_id VARCHAR(255),
  name VARCHAR(255) NOT NULL,
  client_name VARCHAR(255),
  estimated_value DECIMAL(18,2),
  currency VARCHAR(3),
  deal_stage VARCHAR(100),
  source VARCHAR(100),
  assigned_sales_id BIGINT,
  last_interaction_date TIMESTAMPTZ,
  follow_up_status VARCHAR(50),
  onsite_priority BOOLEAN,
  hubspot_created_at TIMESTAMPTZ,
  hubspot_last_updated_at TIMESTAMPTZ,
  sync_status VARCHAR(50),
  last_sync_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  PRIMARY KEY (id)
);

-- Table: opportunity_assignments
CREATE TABLE IF NOT EXISTS opportunity_assignments (
  id BIGSERIAL,
  opportunity_id BIGINT NOT NULL,
  leader_id BIGINT NOT NULL,
  assigned_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  PRIMARY KEY (id)
);

-- Table: opportunity_notes
CREATE TABLE IF NOT EXISTS opportunity_notes (
  id BIGSERIAL,
  opportunity_id BIGINT NOT NULL,
  author_id BIGINT NOT NULL,
  note_content TEXT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  PRIMARY KEY (id)
);

-- Table: permissions
CREATE TABLE IF NOT EXISTS permissions (
  id BIGSERIAL,
  name VARCHAR(100) NOT NULL,
  description TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  PRIMARY KEY (id)
);

-- Table: project_history
CREATE TABLE IF NOT EXISTS project_history (
  id BIGSERIAL,
  employee_id BIGINT NOT NULL,
  project_name VARCHAR(255) NOT NULL,
  client_name VARCHAR(255),
  role VARCHAR(100),
  start_date DATE,
  end_date DATE,
  description TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  PRIMARY KEY (id)
);

-- Table: roles
CREATE TABLE IF NOT EXISTS roles (
  id BIGSERIAL,
  name VARCHAR(100) NOT NULL,
  description TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  PRIMARY KEY (id)
);

-- Table: role_permissions
CREATE TABLE IF NOT EXISTS role_permissions (
  role_id BIGINT NOT NULL,
  permission_id BIGINT NOT NULL,
  PRIMARY KEY (role_id, permission_id)
);

-- Table: sales_kpis
CREATE TABLE IF NOT EXISTS sales_kpis (
  id BIGSERIAL,
  sales_user_id BIGINT NOT NULL,
  year INTEGER NOT NULL,
  quarter INTEGER,
  month INTEGER,
  target_revenue DECIMAL(18,2) NOT NULL,
  currency VARCHAR(3) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  created_by BIGINT,
  updated_by BIGINT,
  PRIMARY KEY (id)
);

-- Table: skills
CREATE TABLE IF NOT EXISTS skills (
  id BIGSERIAL,
  category_id BIGINT NOT NULL,
  name VARCHAR(100) NOT NULL,
  description TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  PRIMARY KEY (id)
);

-- Table: skill_categories
CREATE TABLE IF NOT EXISTS skill_categories (
  id BIGSERIAL,
  name VARCHAR(100) NOT NULL,
  description TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  PRIMARY KEY (id)
);

-- Table: system_configs
CREATE TABLE IF NOT EXISTS system_configs (
  id BIGSERIAL,
  config_key VARCHAR(100) NOT NULL,
  config_value TEXT NOT NULL,
  description TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  PRIMARY KEY (id)
);

-- Table: users
CREATE TABLE IF NOT EXISTS users (
  id BIGSERIAL,
  username VARCHAR(255) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  full_name VARCHAR(255),
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  PRIMARY KEY (id)
);

-- Table: user_roles
CREATE TABLE IF NOT EXISTS user_roles (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id)
);

-- ==============================================
-- INITIAL DATA INSERTION
-- ==============================================

-- 1. Reference data: skill categories & skills
INSERT INTO skill_categories(name, description, created_at, updated_at)
VALUES
  ('Programming Language', 'Languages like Java, Python.', now(), now()),
  ('Framework',            'Frameworks like Spring, React.', now(), now()),
  ('Database',             'Databases like PostgreSQL.', now(), now());

INSERT INTO skills(category_id, name, description, created_at, updated_at)
VALUES
  (1, 'Java',         'Java programming language.', now(), now()),
  (1, 'Python',       'Python programming language.', now(), now()),
  (1, 'JavaScript',   'JavaScript language.', now(), now()),
  (2, 'Spring Boot',  'Spring Boot framework.', now(), now()),
  (2, 'React',        'React front-end framework.', now(), now()),
  (3, 'PostgreSQL',   'PostgreSQL database.', now(), now()),
  (3, 'MongoDB',      'MongoDB database.', now(), now());

-- 2. Reference data: contracts
INSERT INTO contracts(
  contract_code, name, client_name, opportunity_id,
  sign_date, effective_date, expiry_date,
  total_value, currency, contract_type,
  assigned_sales_id, status, description,
  created_at, updated_at, created_by, updated_by, deleted_at
)
VALUES
  ('C001','Alpha Project','Client A', NULL,'2023-01-01','2023-01-01','2023-12-31',100000,'USD','FixedPrice', 1,'Ongoing','Alpha deliverables', now(),now(),1,1,NULL),
  ('C002','Beta Project', 'Client B', NULL,'2023-02-01','2023-02-01','2023-11-30',150000,'USD','TM',         1,'Ongoing','Beta deliverables',  now(),now(),1,1,NULL),
  ('C003','Gamma Project','Client C', NULL,'2023-03-01','2023-03-01','2023-10-31',200000,'USD','FixedPrice', 1,'Ongoing','Gamma deliverables', now(),now(),1,1,NULL),
  ('C004','Delta Project','Client D', NULL,'2023-04-01','2023-04-01','2023-09-30',120000,'USD','TM',         1,'Ongoing','Delta deliverables', now(),now(),1,1,NULL),
  ('C005','Epsilon','Client E',    NULL,'2023-05-01','2023-05-01','2023-08-31',180000,'USD','FixedPrice', 1,'Ongoing','Epsilon deliverables', now(),now(),1,1,NULL);

-- 3. Generate 20 users and employees
-- 3.1 Create 20 users
INSERT INTO users(username, password_hash, email, full_name, created_at, updated_at)
SELECT
  'user' || LPAD(i::text,2,'0'),
  'hashed_' || i::text,
  'user' || LPAD(i::text,2,'0') || '@company.com',
  'Employee ' || i::text,
  now(), now()
FROM generate_series(1,20) AS s(i);

-- 3.2 Create 20 employees based on those users
WITH u AS (
  SELECT id, row_number() OVER (ORDER BY id) AS rn
  FROM users
  ORDER BY id
  LIMIT 20
)
INSERT INTO employees(
  user_id, employee_code, first_name, last_name,
  birth_date, hire_date, company_email, internal_account,
  address, phone_number, emergency_contact,
  position, team, reporting_leader_id,
  current_status, status_updated_at,
  profile_picture_url, created_at, updated_at, created_by, updated_by
)