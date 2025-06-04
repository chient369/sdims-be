-- Test data for contract files
-- Script should be run after test_data_contract.sql

-- Insert contract files for each contract
INSERT INTO contract_files (id, contract_id, file_name, file_path, file_type, file_size, description, stored_file_name, uploaded_by_id, uploaded_at, created_at, updated_at)
VALUES 
-- Contract 1 Files
(1, 1, 'Contract_ABC_Corp_Signed.pdf', '/uploads/contracts/1/Contract_ABC_Corp_Signed.pdf', 'application/pdf', 2048000, 
   'Signed contract document', 'c1f1_signed_contract.pdf', 6, NOW(), NOW(), NOW()),
(2, 1, 'Requirements_Document.docx', '/uploads/contracts/1/Requirements_Document.docx', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 1536000, 
   'Detailed requirements document', 'c1f2_requirements.docx', 6, NOW(), NOW(), NOW()),
(3, 1, 'Payment_Schedule.xlsx', '/uploads/contracts/1/Payment_Schedule.xlsx', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 512000, 
   'Payment schedule and terms', 'c1f3_payment_schedule.xlsx', 6, NOW(), NOW(), NOW()),

-- Contract 2 Files
(4, 2, 'Mobile_App_Contract_Signed.pdf', '/uploads/contracts/2/Mobile_App_Contract_Signed.pdf', 'application/pdf', 2560000, 
   'Signed contract document', 'c2f1_signed_contract.pdf', 7, NOW(), NOW(), NOW()),
(5, 2, 'Mobile_App_Technical_Specs.docx', '/uploads/contracts/2/Mobile_App_Technical_Specs.docx', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 1843200, 
   'Technical specifications for mobile app', 'c2f2_tech_specs.docx', 7, NOW(), NOW(), NOW()),
(6, 2, 'App_Wireframes.zip', '/uploads/contracts/2/App_Wireframes.zip', 'application/zip', 15360000, 
   'App wireframes and design mockups', 'c2f3_wireframes.zip', 7, NOW(), NOW(), NOW()),

-- Contract 3 Files
(7, 3, 'System_Integration_Contract.pdf', '/uploads/contracts/3/System_Integration_Contract.pdf', 'application/pdf', 3072000, 
   'Signed contract document', 'c3f1_signed_contract.pdf', 6, NOW(), NOW(), NOW()),
(8, 3, 'Integration_Architecture.pdf', '/uploads/contracts/3/Integration_Architecture.pdf', 'application/pdf', 5120000, 
   'System integration architecture diagram', 'c3f2_architecture.pdf', 6, NOW(), NOW(), NOW()),

-- Contract 4 Files
(9, 4, 'Cloud_Migration_Contract.pdf', '/uploads/contracts/4/Cloud_Migration_Contract.pdf', 'application/pdf', 2867200, 
   'Signed contract document', 'c4f1_signed_contract.pdf', 7, NOW(), NOW(), NOW()),
(10, 4, 'Migration_Plan.pptx', '/uploads/contracts/4/Migration_Plan.pptx', 'application/vnd.openxmlformats-officedocument.presentationml.presentation', 4096000, 
   'Cloud migration project plan presentation', 'c4f2_migration_plan.pptx', 7, NOW(), NOW(), NOW()),
   
-- Contract 5 Files
(11, 5, 'DevOps_Consulting_Contract_Draft.pdf', '/uploads/contracts/5/DevOps_Consulting_Contract_Draft.pdf', 'application/pdf', 2457600, 
   'Draft contract document', 'c5f1_draft_contract.pdf', 6, NOW(), NOW(), NOW()),

-- Contract 6 Files
(12, 6, 'Web_Portal_Maintenance_Contract.pdf', '/uploads/contracts/6/Web_Portal_Maintenance_Contract.pdf', 'application/pdf', 2048000, 
   'Signed maintenance contract', 'c6f1_signed_contract.pdf', 7, NOW(), NOW(), NOW()),
(13, 6, 'SLA_Document.pdf', '/uploads/contracts/6/SLA_Document.pdf', 'application/pdf', 1536000, 
   'Service Level Agreement document', 'c6f2_sla.pdf', 7, NOW(), NOW(), NOW()),

-- Contract 9 Files
(14, 9, 'IT_Infrastructure_Contract.pdf', '/uploads/contracts/9/IT_Infrastructure_Contract.pdf', 'application/pdf', 3276800, 
   'Signed infrastructure setup contract', 'c9f1_signed_contract.pdf', 6, NOW(), NOW(), NOW()),
(15, 9, 'Network_Diagram.jpg', '/uploads/contracts/9/Network_Diagram.jpg', 'image/jpeg', 2048000, 
   'Network infrastructure diagram', 'c9f2_network_diagram.jpg', 6, NOW(), NOW(), NOW()),

-- Contract 10 Files
(16, 10, 'Security_Audit_Contract.pdf', '/uploads/contracts/10/Security_Audit_Contract.pdf', 'application/pdf', 2252800, 
   'Approved security audit contract', 'c10f1_approved_contract.pdf', 7, NOW(), NOW(), NOW()); 