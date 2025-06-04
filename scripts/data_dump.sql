-- SQL Data Dump for Internal Management System
-- Generated on YYYY-MM-DD HH:MM:SS

-- Disable foreign key checks for bulk insert
SET FOREIGN_KEY_CHECKS=0;

-- Truncate existing data (optional, uncomment if needed)
/*
TRUNCATE TABLE users;
TRUNCATE TABLE roles;
TRUNCATE TABLE permissions;
TRUNCATE TABLE user_roles;
TRUNCATE TABLE role_permissions;
TRUNCATE TABLE skill_categories;
TRUNCATE TABLE skills;
TRUNCATE TABLE teams;
TRUNCATE TABLE employees;
TRUNCATE TABLE employee_skills;
TRUNCATE TABLE project_history;
TRUNCATE TABLE employee_status_log;
TRUNCATE TABLE opportunities;
TRUNCATE TABLE opportunity_assignments;
TRUNCATE TABLE opportunity_notes;
TRUNCATE TABLE contracts;
TRUNCATE TABLE contract_payment_terms;
TRUNCATE TABLE contract_employees;
TRUNCATE TABLE contract_files;
TRUNCATE TABLE employee_costs;
TRUNCATE TABLE employee_revenues;
TRUNCATE TABLE sales_kpis;
TRUNCATE TABLE system_configs;
TRUNCATE TABLE notifications;
*/

-- Users & Authorization
INSERT INTO `users` (`id`, `username`, `email`, `password`, `full_name`, `avatar_url`, `enabled`, `account_non_expired`, `account_non_locked`, `credentials_non_expired`, `created_at`, `updated_at`, `last_login_at`, `password_changed_at`) VALUES
(1, 'admin', 'admin@example.com', '$2a$10$sF2vQ0sP.m0vV9eZ6q9zHO.uH/WwR/E/3r9H4w.B4/PzJ0yS4lK0e', 'Admin User', NULL, 1, 1, 1, 1, NOW(), NOW(), NULL, NULL),
(2, 'manager', 'manager@example.com', '$2a$10$sF2vQ0sP.m0vV9eZ6q9zHO.uH/WwR/E/3r9H4w.B4/PzJ0yS4lK0e', 'Manager User', NULL, 1, 1, 1, 1, NOW(), NOW(), NULL, NULL),
(3, 'employee1', 'employee1@example.com', '$2a$10$sF2vQ0sP.m0vV9eZ6q9zHO.uH/WwR/E/3r9H4w.B4/PzJ0yS4lK0e', 'Employee One', NULL, 1, 1, 1, 1, NOW(), NOW(), NULL, NULL),
(4, 'employee2', 'employee2@example.com', '$2a$10$sF2vQ0sP.m0vV9eZ6q9zHO.uH/WwR/E/3r9H4w.B4/PzJ0yS4lK0e', 'Employee Two', NULL, 1, 1, 1, 1, NOW(), NOW(), NULL, NULL),
(5, 'sales1', 'sales1@example.com', '$2a$10$sF2vQ0sP.m0vV9eZ6q9zHO.uH/WwR/E/3r9H4w.B4/PzJ0yS4lK0e', 'Sales Person One', NULL, 1, 1, 1, 1, NOW(), NOW(), NULL, NULL),
(6, 'sales2', 'sales2@example.com', '$2a$10$sF2vQ0sP.m0vV9eZ6q9zHO.uH/WwR/E/3r9H4w.B4/PzJ0yS4lK0e', 'Sales Person Two', NULL, 1, 1, 1, 1, NOW(), NOW(), NULL, NULL),
(7, 'dev_lead1', 'devlead1@example.com', '$2a$10$sF2vQ0sP.m0vV9eZ6q9zHO.uH/WwR/E/3r9H4w.B4/PzJ0yS4lK0e', 'Dev Lead One', NULL, 1, 1, 1, 1, NOW(), NOW(), NULL, NULL),
(8, 'hr_manager', 'hr.manager@example.com', '$2a$10$sF2vQ0sP.m0vV9eZ6q9zHO.uH/WwR/E/3r9H4w.B4/PzJ0yS4lK0e', 'HR Manager', NULL, 1, 1, 1, 1, NOW(), NOW(), NULL, NULL),
(9, 'accountant', 'accountant@example.com', '$2a$10$sF2vQ0sP.m0vV9eZ6q9zHO.uH/WwR/E/3r9H4w.B4/PzJ0yS4lK0e', 'Accountant User', NULL, 1, 1, 1, 1, NOW(), NOW(), NULL, NULL),
(10, 'ceo', 'ceo@example.com', '$2a$10$sF2vQ0sP.m0vV9eZ6q9zHO.uH/WwR/E/3r9H4w.B4/PzJ0yS4lK0e', 'Chief Executive Officer', NULL, 1, 1, 1, 1, NOW(), NOW(), NULL, NULL),
(11, 'employee3', 'employee3@example.com', '$2a$10$sF2vQ0sP.m0vV9eZ6q9zHO.uH/WwR/E/3r9H4w.B4/PzJ0yS4lK0e', 'Employee Three', NULL, 1, 1, 1, 1, NOW(), NOW(), NULL, NULL),
(12, 'employee4', 'employee4@example.com', '$2a$10$sF2vQ0sP.m0vV9eZ6q9zHO.uH/WwR/E/3r9H4w.B4/PzJ0yS4lK0e', 'Employee Four', NULL, 1, 1, 1, 1, NOW(), NOW(), NULL, NULL),
(13, 'employee5', 'employee5@example.com', '$2a$10$sF2vQ0sP.m0vV9eZ6q9zHO.uH/WwR/E/3r9H4w.B4/PzJ0yS4lK0e', 'Employee Five', NULL, 1, 1, 1, 1, NOW(), NOW(), NULL, NULL),
(14, 'employee6', 'employee6@example.com', '$2a$10$sF2vQ0sP.m0vV9eZ6q9zHO.uH/WwR/E/3r9H4w.B4/PzJ0yS4lK0e', 'Employee Six', NULL, 1, 1, 1, 1, NOW(), NOW(), NULL, NULL),
(15, 'employee7', 'employee7@example.com', '$2a$10$sF2vQ0sP.m0vV9eZ6q9zHO.uH/WwR/E/3r9H4w.B4/PzJ0yS4lK0e', 'Employee Seven', NULL, 1, 1, 1, 1, NOW(), NOW(), NULL, NULL),
(16, 'employee8', 'employee8@example.com', '$2a$10$sF2vQ0sP.m0vV9eZ6q9zHO.uH/WwR/E/3r9H4w.B4/PzJ0yS4lK0e', 'Employee Eight', NULL, 1, 1, 1, 1, NOW(), NOW(), NULL, NULL),
(17, 'employee9', 'employee9@example.com', '$2a$10$sF2vQ0sP.m0vV9eZ6q9zHO.uH/WwR/E/3r9H4w.B4/PzJ0yS4lK0e', 'Employee Nine', NULL, 1, 1, 1, 1, NOW(), NOW(), NULL, NULL),
(18, 'employee10', 'employee10@example.com', '$2a$10$sF2vQ0sP.m0vV9eZ6q9zHO.uH/WwR/E/3r9H4w.B4/PzJ0yS4lK0e', 'Employee Ten', NULL, 1, 1, 1, 1, NOW(), NOW(), NULL, NULL);

INSERT INTO `roles` (`id`, `name`, `description`, `created_at`, `updated_at`) VALUES
(1, 'ROLE_ADMIN', 'Administrator role with full access', NOW(), NOW()),
(2, 'ROLE_MANAGER', 'Manager role with specific module access', NOW(), NOW()),
(3, 'ROLE_EMPLOYEE', 'Standard employee role', NOW(), NOW()),
(4, 'ROLE_SALES', 'Sales role for opportunity and contract management', NOW(), NOW()),
(5, 'ROLE_HR', 'HR role for employee management', NOW(), NOW());

-- Permissions defined from markdown
INSERT INTO `permissions` (`id`, `name`, `description`, `created_at`, `updated_at`) VALUES
(1, 'alert-threshold:read', 'Xem ngưỡng cảnh báo', NOW(), NOW()),
(2, 'alert-threshold:update', 'Cập nhật ngưỡng cảnh báo', NOW(), NOW()),
(3, 'api-connect:read', 'Xem thông tin kết nối API', NOW(), NOW()),
(4, 'api-connect:update', 'Cập nhật thông tin kết nối API', NOW(), NOW()),
(5, 'config:read', 'Xem cấu hình hệ thống', NOW(), NOW()),
(6, 'config:update', 'Cập nhật cấu hình hệ thống', NOW(), NOW()),
(7, 'contract-file:create:all', 'Upload file cho bất kỳ hợp đồng', NOW(), NOW()),
(8, 'contract-file:create:own', 'Upload file cho hợp đồng của mình', NOW(), NOW()),
(9, 'contract-file:delete:all', 'Xoá file đính kèm của bất kỳ hợp đồng', NOW(), NOW()),
(10, 'contract-file:delete:own', 'Xoá file đính kèm của hợp đồng mình quản lý', NOW(), NOW()),
(11, 'contract-file:read:all', 'Xem file đính kèm (tất cả hợp đồng)', NOW(), NOW()),
(12, 'contract-file:read:assigned', 'Xem file đính kèm (hợp đồng được gán)', NOW(), NOW()),
(13, 'contract-file:read:own', 'Xem file đính kèm (hợp đồng của mình)', NOW(), NOW()),
(14, 'contract-link:update:all', 'Liên kết hợp đồng với cơ hội/nhân sự (bất kỳ)', NOW(), NOW()),
(15, 'contract-link:update:assigned', 'Liên kết hợp đồng với cơ hội/nhân sự (được gán)', NOW(), NOW()),
(16, 'contract-link:update:own', 'Liên kết hợp đồng với cơ hội/nhân sự (của mình)', NOW(), NOW()),
(17, 'contract:create', 'Tạo hợp đồng mới', NOW(), NOW()),
(18, 'contract:delete', 'Xoá hợp đồng', NOW(), NOW()),
(19, 'contract:read:all', 'Xem tất cả hợp đồng', NOW(), NOW()),
(20, 'contract:read:assigned', 'Xem hợp đồng được gán', NOW(), NOW()),
(21, 'contract:read:own', 'Xem hợp đồng do mình quản lý', NOW(), NOW()),
(22, 'contract:update:all', 'Cập nhật tất cả hợp đồng', NOW(), NOW()),
(23, 'contract:update:own', 'Cập nhật hợp đồng do mình quản lý', NOW(), NOW()),
(24, 'dashboard:read:all', 'Xem dashboard tổng hợp (tất cả dữ liệu)', NOW(), NOW()),
(25, 'dashboard:read:own', 'Xem dashboard tổng hợp (dữ liệu cá nhân)', NOW(), NOW()),
(26, 'dashboard:read:team', 'Xem dashboard tổng hợp (dữ liệu team)', NOW(), NOW()),
(27, 'debt-report:read:all', 'Xem báo cáo công nợ (tất cả)', NOW(), NOW()),
(28, 'debt-report:read:own', 'Xem báo cáo công nợ (của mình)', NOW(), NOW()),
(29, 'employee-alert:read:all', 'Xem cảnh báo nhân sự sắp hết dự án (tất cả)', NOW(), NOW()),
(30, 'employee-alert:read:team', 'Xem cảnh báo nhân sự sắp hết dự án (team)', NOW(), NOW()),
(31, 'employee-cost:create', 'Tạo chi phí nhân viên', NOW(), NOW()),
(32, 'employee-cost:delete', 'Xoá chi phí nhân viên', NOW(), NOW()),
(33, 'employee-cost:import', 'Import chi phí nhân viên từ file', NOW(), NOW()),
(34, 'employee-cost:read:all', 'Xem chi phí của tất cả nhân viên', NOW(), NOW()),
(35, 'employee-cost:read:team', 'Xem chi phí của nhân viên trong team', NOW(), NOW()),
(36, 'employee-cost:update:all', 'Cập nhật chi phí của tất cả nhân viên', NOW(), NOW()),
(37, 'employee-skill:create:all', 'Thêm kỹ năng cho bất kỳ nhân viên', NOW(), NOW()),
(38, 'employee-skill:create:own', 'Thêm kỹ năng cá nhân', NOW(), NOW()),
(39, 'employee-skill:create:team', 'Thêm kỹ năng cho nhân viên trong team', NOW(), NOW()),
(40, 'employee-skill:delete:all', 'Xoá kỹ năng của bất kỳ nhân viên', NOW(), NOW()),
(41, 'employee-skill:delete:own', 'Xoá kỹ năng cá nhân', NOW(), NOW()),
(42, 'employee-skill:delete:team', 'Xoá kỹ năng của nhân viên trong team', NOW(), NOW()),
(43, 'employee-skill:evaluate', 'Đánh giá kỹ năng nhân viên', NOW(), NOW()),
(44, 'employee-skill:read:all', 'Xem kỹ năng của tất cả nhân viên', NOW(), NOW()),
(45, 'employee-skill:read:own', 'Xem kỹ năng cá nhân', NOW(), NOW()),
(46, 'employee-skill:read:team', 'Xem kỹ năng của nhân viên trong team', NOW(), NOW()),
(47, 'employee-skill:update:all', 'Cập nhật kỹ năng của bất kỳ nhân viên', NOW(), NOW()),
(48, 'employee-skill:update:own', 'Cập nhật kỹ năng cá nhân', NOW(), NOW()),
(49, 'employee-skill:update:team', 'Cập nhật kỹ năng của nhân viên trong team', NOW(), NOW()),
(50, 'employee-status:read:all', 'Xem trạng thái & phân bổ dự án của tất cả nhân viên', NOW(), NOW()),
(51, 'employee-status:read:own', 'Xem trạng thái & phân bổ dự án cá nhân', NOW(), NOW()),
(52, 'employee-status:read:team', 'Xem trạng thái & phân bổ dự án của nhân viên trong team', NOW(), NOW()),
(53, 'employee-status:update:all', 'Cập nhật trạng thái & phân bổ dự án tất cả nhân viên', NOW(), NOW()),
(54, 'employee-status:update:team', 'Cập nhật trạng thái & phân bổ dự án nhân viên trong team', NOW(), NOW()),
(55, 'employee-suggest:read', 'Xem gợi ý nhân sự phù hợp', NOW(), NOW()),
(56, 'employee:create', 'Tạo nhân viên mới', NOW(), NOW()),
(57, 'employee:delete', 'Xoá nhân viên (soft delete)', NOW(), NOW()),
(58, 'employee:export', 'Export danh sách nhân viên', NOW(), NOW()),
(59, 'employee:import', 'Import danh sách nhân viên từ file', NOW(), NOW()),
(60, 'employee:read:all', 'Xem thông tin tất cả nhân viên', NOW(), NOW()),
(61, 'employee:read:basic', 'Xem thông tin cơ bản của nhân viên', NOW(), NOW()),
(62, 'employee:read:own', 'Xem thông tin cá nhân', NOW(), NOW()),
(63, 'employee:read:team', 'Xem thông tin nhân viên trong team', NOW(), NOW()),
(64, 'employee:update:all', 'Cập nhật thông tin tất cả nhân viên', NOW(), NOW()),
(65, 'employee:update:own', 'Cập nhật thông tin cá nhân', NOW(), NOW()),
(66, 'employee:update:team', 'Cập nhật thông tin nhân viên trong team', NOW(), NOW()),
(67, 'margin-alert:config', 'Cấu hình thông báo margin thấp', NOW(), NOW()),
(68, 'margin-alert:read:all', 'Xem cảnh báo margin toàn bộ', NOW(), NOW()),
(69, 'margin-alert:read:team', 'Xem cảnh báo margin team', NOW(), NOW()),
(70, 'margin-summary:read:all', 'Xem tổng hợp margin toàn bộ', NOW(), NOW()),
(71, 'margin-summary:read:team', 'Xem tổng hợp margin team', NOW(), NOW()),
(72, 'margin:read:all', 'Xem margin của tất cả nhân viên', NOW(), NOW()),
(73, 'margin:read:team', 'Xem margin của nhân viên trong team', NOW(), NOW()),
(74, 'opportunity-alert:config', 'Cấu hình thông báo cơ hội', NOW(), NOW()),
(75, 'opportunity-alert:read:all', 'Xem thông báo tất cả cơ hội', NOW(), NOW()),
(76, 'opportunity-alert:read:assigned', 'Xem thông báo cơ hội được gán', NOW(), NOW()),
(77, 'opportunity-assign:update:all', 'Gán Leader vào cơ hội (bất kỳ)', NOW(), NOW()),
(78, 'opportunity-followup:read:all', 'Xem trạng thái follow-up tất cả cơ hội', NOW(), NOW()),
(79, 'opportunity-log:read:all', 'Xem log đồng bộ Hubspot', NOW(), NOW()),
(80, 'opportunity-note:create:all', 'Thêm ghi chú cho bất kỳ cơ hội', NOW(), NOW()),
(81, 'opportunity-note:create:assigned', 'Thêm ghi chú cho cơ hội được gán', NOW(), NOW()),
(82, 'opportunity-note:read:all', 'Xem ghi chú của tất cả cơ hội', NOW(), NOW()),
(83, 'opportunity-note:read:assigned', 'Xem ghi chú của cơ hội được gán', NOW(), NOW()),
(84, 'opportunity-onsite:update:all', 'Cập nhật trạng thái ưu tiên onsite (bất kỳ)', NOW(), NOW()),
(85, 'opportunity-onsite:update:assigned', 'Cập nhật trạng thái ưu tiên onsite (assigned)', NOW(), NOW()),
(86, 'opportunity-sync:config', 'Cấu hình đồng bộ Hubspot', NOW(), NOW()),
(87, 'opportunity-sync:read', 'Xem thông tin đồng bộ từ Hubspot', NOW(), NOW()),
(88, 'opportunity:create', 'Tạo cơ hội kinh doanh', NOW(), NOW()),
(89, 'opportunity:delete', 'Xoá cơ hội kinh doanh', NOW(), NOW()),
(90, 'opportunity:read:all', 'Xem tất cả cơ hội kinh doanh', NOW(), NOW()),
(91, 'opportunity:update:all', 'Cập nhật tất cả cơ hội kinh doanh', NOW(), NOW()),
(92, 'opportunity:update:assigned', 'Cập nhật cơ hội kinh doanh được gán', NOW(), NOW()),
(93, 'opportunity:update:own', 'Cập nhật cơ hội kinh doanh do mình tạo', NOW(), NOW()),
(94, 'payment-alert:config', 'Cấu hình cảnh báo thanh toán', NOW(), NOW()),
(95, 'payment-alert:read:all', 'Xem cảnh báo thanh toán (tất cả)', NOW(), NOW()),
(96, 'payment-alert:read:assigned', 'Xem cảnh báo thanh toán (hợp đồng được gán)', NOW(), NOW()),
(97, 'payment-alert:read:own', 'Xem cảnh báo thanh toán (hợp đồng của mình)', NOW(), NOW()),
(98, 'payment-status:import', 'Import trạng thái thu tiền từ file', NOW(), NOW()),
(99, 'payment-status:update:all', 'Cập nhật trạng thái thu tiền (bất kỳ)', NOW(), NOW()),
(100, 'payment-term:create:all', 'Thêm điều khoản thanh toán (bất kỳ hợp đồng)', NOW(), NOW()),
(101, 'payment-term:create:own', 'Thêm điều khoản thanh toán (hợp đồng của mình)', NOW(), NOW()),
(102, 'payment-term:delete:all', 'Xoá điều khoản thanh toán (bất kỳ)', NOW(), NOW()),
(103, 'payment-term:delete:own', 'Xoá điều khoản thanh toán (hợp đồng của mình)', NOW(), NOW()),
(104, 'payment-term:read:all', 'Xem điều khoản thanh toán (tất cả)', NOW(), NOW()),
(105, 'payment-term:read:assigned', 'Xem điều khoản thanh toán (hợp đồng được gán)', NOW(), NOW()),
(106, 'payment-term:read:own', 'Xem điều khoản thanh toán (hợp đồng của mình)', NOW(), NOW()),
(107, 'payment-term:update:all', 'Cập nhật điều khoản thanh toán (bất kỳ)', NOW(), NOW()),
(108, 'payment-term:update:own', 'Cập nhật điều khoản thanh toán (hợp đồng của mình)', NOW(), NOW()),
(109, 'permission:assign', 'Gán quyền cho vai trò', NOW(), NOW()),
(110, 'permission:read', 'Xem danh sách quyền', NOW(), NOW()),
(111, 'project-history:read:all', 'Xem lịch sử dự án của tất cả nhân viên', NOW(), NOW()),
(112, 'project-history:read:own', 'Xem lịch sử dự án cá nhân', NOW(), NOW()),
(113, 'project-history:read:team', 'Xem lịch sử dự án của nhân viên trong team', NOW(), NOW()),
(114, 'report:export', 'Xuất báo cáo ra file', NOW(), NOW()),
(115, 'report:read:all', 'Xem báo cáo chi tiết (tất cả)', NOW(), NOW()),
(116, 'report:read:own', 'Xem báo cáo chi tiết (của mình)', NOW(), NOW()),
(117, 'report:read:team', 'Xem báo cáo chi tiết (team)', NOW(), NOW()),
(118, 'revenue-report:read:all', 'Xem báo cáo doanh thu thực tế (tất cả)', NOW(), NOW()),
(119, 'revenue-report:read:own', 'Xem báo cáo doanh thu thực tế (của mình)', NOW(), NOW()),
(120, 'revenue-report:read:team', 'Xem báo cáo doanh thu thực tế (team)', NOW(), NOW()),
(121, 'revenue-summary:read:all', 'Xem tổng hợp doanh thu (tất cả)', NOW(), NOW()),
(122, 'revenue-summary:read:own', 'Xem tổng hợp doanh thu (của mình)', NOW(), NOW()),
(123, 'revenue-summary:read:team', 'Xem tổng hợp doanh thu (team)', NOW(), NOW()),
(124, 'revenue:read:all', 'Xem doanh thu của tất cả nhân viên', NOW(), NOW()),
(125, 'revenue:read:team', 'Xem doanh thu của nhân viên trong team', NOW(), NOW()),
(126, 'role:create', 'Tạo vai trò mới', NOW(), NOW()),
(127, 'role:delete', 'Xoá vai trò', NOW(), NOW()),
(128, 'role:read', 'Xem danh sách vai trò', NOW(), NOW()),
(129, 'role:update', 'Cập nhật vai trò', NOW(), NOW()),
(130, 'sales-kpi:create', 'Thiết lập KPI doanh thu', NOW(), NOW()),
(131, 'sales-kpi:delete', 'Xoá KPI doanh thu', NOW(), NOW()),
(132, 'sales-kpi:read:all', 'Xem KPI doanh thu (tất cả)', NOW(), NOW()),
(133, 'sales-kpi:read:own', 'Xem KPI doanh thu (của mình)', NOW(), NOW()),
(134, 'sales-kpi:update', 'Cập nhật KPI doanh thu', NOW(), NOW()),
(135, 'skill-category:create', 'Tạo danh mục kỹ năng', NOW(), NOW()),
(136, 'skill-category:delete', 'Xoá danh mục kỹ năng', NOW(), NOW()),
(137, 'skill-category:read', 'Xem danh mục kỹ năng', NOW(), NOW()),
(138, 'skill-category:update', 'Cập nhật danh mục kỹ năng', NOW(), NOW()),
(139, 'skill:create', 'Tạo kỹ năng mới', NOW(), NOW()),
(140, 'skill:delete', 'Xoá kỹ năng', NOW(), NOW()),
(141, 'skill:read', 'Xem danh sách kỹ năng', NOW(), NOW()),
(142, 'skill:update', 'Cập nhật kỹ năng', NOW(), NOW()),
(143, 'system-log:read:all', 'Xem log hệ thống (toàn bộ)', NOW(), NOW()),
(144, 'system-log:read:limited', 'Xem log hệ thống (giới hạn)', NOW(), NOW()),
(145, 'user:create', 'Tạo người dùng mới', NOW(), NOW()),
(146, 'user:delete', 'Xoá người dùng', NOW(), NOW()),
(147, 'user:read', 'Xem danh sách người dùng', NOW(), NOW()),
(148, 'user:update', 'Cập nhật thông tin người dùng', NOW(), NOW()),
(149, 'utilization:read:all', 'Xem báo cáo utilization toàn bộ nhân viên', NOW(), NOW()),
(150, 'utilization:read:team', 'Xem báo cáo utilization team', NOW(), NOW());

-- User Roles mapping
INSERT INTO `user_roles` (`user_id`, `role_id`) VALUES
(1, 1), -- admin has ROLE_ADMIN
(2, 2), -- manager has ROLE_MANAGER
(3, 3), -- employee1 has ROLE_EMPLOYEE
(4, 3), -- employee2 has ROLE_EMPLOYEE
(5, 4), -- sales1 has ROLE_SALES
(6, 4), -- sales2 has ROLE_SALES
(7, 2), -- dev_lead1 has ROLE_MANAGER (can also be specific lead role)
(8, 5), -- hr_manager has ROLE_HR
(9, 3), -- accountant has ROLE_EMPLOYEE (can be more specific financial role)
(10, 1), -- ceo has ROLE_ADMIN (or a top-level manager role)
(11, 3),
(12, 3),
(13, 3),
(14, 3),
(15, 3),
(16, 3),
(17, 3),
(18, 3);

-- Role Permissions defined from markdown
INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(1, 5),
(1, 6),
(1, 109),
(1, 110),
(1, 126),
(1, 127),
(1, 128),
(1, 129),
(1, 143),
(1, 145),
(1, 146),
(1, 147),
(1, 148),
(2, 1),
(2, 5),
(2, 7),
(2, 9),
(2, 11),
(2, 12),
(2, 14),
(2, 15),
(2, 17),
(2, 18),
(2, 19),
(2, 20),
(2, 22),
(2, 24),
(2, 26),
(2, 27),
(2, 29),
(2, 30),
(2, 31),
(2, 32),
(2, 33),
(2, 34),
(2, 35),
(2, 36),
(2, 37),
(2, 39),
(2, 40),
(2, 42),
(2, 43),
(2, 44),
(2, 46),
(2, 47),
(2, 49),
(2, 50),
(2, 52),
(2, 53),
(2, 54),
(2, 55),
(2, 56),
(2, 57),
(2, 58),
(2, 59),
(2, 60),
(2, 63),
(2, 64),
(2, 66),
(2, 67),
(2, 68),
(2, 69),
(2, 70),
(2, 71),
(2, 72),
(2, 73),
(2, 74),
(2, 75),
(2, 76),
(2, 77),
(2, 78),
(2, 79),
(2, 80),
(2, 81),
(2, 82),
(2, 83),
(2, 84),
(2, 85),
(2, 86),
(2, 87),
(2, 88),
(2, 89),
(2, 90),
(2, 91),
(2, 92),
(2, 93),
(2, 94),
(2, 95),
(2, 96),
(2, 98),
(2, 99),
(2, 100),
(2, 101),
(2, 102),
(2, 103),
(2, 104),
(2, 105),
(2, 106),
(2, 107),
(2, 108),
(2, 111),
(2, 113),
(2, 114),
(2, 115),
(2, 117),
(2, 118),
(2, 120),
(2, 121),
(2, 123),
(2, 124),
(2, 125),
(2, 130),
(2, 131),
(2, 132),
(2, 133),
(2, 134),
(2, 135),
(2, 136),
(2, 137),
(2, 138),
(2, 139),
(2, 140),
(2, 141),
(2, 142),
(2, 144),
(2, 149),
(2, 150),
(3, 38),
(3, 41),
(3, 45),
(3, 48),
(3, 51),
(3, 62),
(3, 65),
(3, 112),
(3, 137),
(3, 141),
(4, 8),
(4, 10),
(4, 13),
(4, 16),
(4, 17),
(4, 21),
(4, 23),
(4, 25),
(4, 28),
(4, 61),
(4, 75),
(4, 76),
(4, 77),
(4, 78),
(4, 80),
(4, 81),
(4, 82),
(4, 83),
(4, 84),
(4, 85),
(4, 87),
(4, 88),
(4, 90),
(4, 92),
(4, 93),
(4, 97),
(4, 98),
(4, 99),
(4, 101),
(4, 103),
(4, 106),
(4, 108),
(4, 114),
(4, 116),
(4, 119),
(4, 122),
(4, 133);

-- Skill Categories
INSERT INTO `skill_categories` (`id`, `name`, `description`, `created_at`, `updated_at`) VALUES
(1, 'Programming Languages', 'Skills related to programming languages', NOW(), NOW()),
(2, 'Frameworks/Libraries', 'Skills related to specific frameworks and libraries', NOW(), NOW()),
(3, 'Databases', 'Skills related to database technologies', NOW(), NOW()),
(4, 'Cloud Platforms', 'Skills related to cloud computing platforms', NOW(), NOW()),
(5, 'Soft Skills', 'Interpersonal and communication skills', NOW(), NOW());

-- Skills
INSERT INTO `skills` (`id`, `category_id`, `name`, `description`, `created_at`, `updated_at`) VALUES
(1, 1, 'Java', 'Java programming language', NOW(), NOW()),
(2, 1, 'Python', 'Python programming language', NOW(), NOW()),
(3, 1, 'JavaScript', 'JavaScript programming language', NOW(), NOW()),
(4, 2, 'Spring Boot', 'Spring Boot framework', NOW(), NOW()),
(5, 2, 'React', 'React JavaScript library', NOW(), NOW()),
(6, 2, 'Angular', 'Angular framework', NOW(), NOW()),
(7, 3, 'MySQL', 'MySQL relational database', NOW(), NOW()),
(8, 3, 'PostgreSQL', 'PostgreSQL relational database', NOW(), NOW()),
(9, 3, 'MongoDB', 'MongoDB NoSQL database', NOW(), NOW()),
(10, 4, 'AWS', 'Amazon Web Services', NOW(), NOW()),
(11, 4, 'Azure', 'Microsoft Azure', NOW(), NOW()),
(12, 5, 'Communication', 'Effective communication skills', NOW(), NOW()),
(13, 5, 'Teamwork', 'Ability to work effectively in a team', NOW(), NOW()),
(14, 5, 'Problem Solving', 'Analytical and problem-solving skills', NOW(), NOW());

-- Teams
INSERT INTO `teams` (`id`, `name`, `department`, `description`, `leader_id`, `parent_team_id`, `created_at`, `updated_at`, `created_by`, `updated_by`, `deleted_at`) VALUES
(1, 'Alpha Development Team', 'Technology', 'Core product development team A', NULL, NULL, NOW(), NOW(), 1, 1, NULL),
(2, 'Bravo Development Team', 'Technology', 'Core product development team B', NULL, NULL, NOW(), NOW(), 1, 1, NULL),
(3, 'Sales Team A', 'Sales', 'Handles enterprise clients', NULL, NULL, NOW(), NOW(), 1, 1, NULL),
(4, 'HR Department', 'Human Resources', 'Manages all HR functions', NULL, NULL, NOW(), NOW(), 1, 1, NULL),
(5, 'Marketing Team', 'Marketing', 'Handles product marketing and campaigns', NULL, NULL, NOW(), NOW(), 1, 1, NULL);

-- Employees (10 employees)
-- Note: user_id is a link to users table.
-- reporting_leader_id and team_id will be updated after all employees and teams are inserted if needed.
INSERT INTO `employees` (`id`, `user_id`, `employee_code`, `first_name`, `last_name`, `birth_date`, `hire_date`, `company_email`, `internal_account`, `address`, `phone_number`, `emergency_contact`, `position`, `team_id`, `reporting_leader_id`, `current_status`, `status_updated_at`, `profile_picture_url`, `created_at`, `updated_at`, `created_by`, `updated_by`, `deleted_at`) VALUES
(1, 3, 'EMP001', 'Alice', 'Smith', '1990-05-15', '2020-01-10', 'alice.smith@example.com', 'asmith', '123 Main St, Anytown', '555-0101', 'John Smith (Spouse) 555-0102', 'Software Engineer', 1, NULL, 'Active', NOW(), NULL, NOW(), NOW(), 1, 1, NULL),
(2, 4, 'EMP002', 'Bob', 'Johnson', '1988-08-20', '2019-07-01', 'bob.johnson@example.com', 'bjohnson', '456 Oak St, Anytown', '555-0103', 'Jane Johnson (Sister) 555-0104', 'Senior Software Engineer', 1, NULL, 'Active', NOW(), NULL, NOW(), NOW(), 1, 1, NULL),
(3, 5, 'EMP003', 'Carol', 'Williams', '1992-11-30', '2021-03-15', 'carol.williams@example.com', 'cwilliams', '789 Pine St, Anytown', '555-0105', 'David Williams (Brother) 555-0106', 'Sales Executive', 3, NULL, 'Active', NOW(), NULL, NOW(), NOW(), 1, 1, NULL),
(4, 6, 'EMP004', 'David', 'Brown', '1985-02-25', '2018-05-01', 'david.brown@example.com', 'dbrown', '101 Maple St, Anytown', '555-0107', 'Susan Brown (Wife) 555-0108', 'Sales Manager', 3, NULL, 'Active', NOW(), NULL, NOW(), NOW(), 1, 1, NULL),
(5, 7, 'EMP005', 'Eve', 'Davis', '1987-07-12', '2017-10-20', 'eve.davis@example.com', 'edavis', '202 Birch St, Anytown', '555-0109', 'Michael Davis (Husband) 555-0110', 'Development Lead', 2, NULL, 'Active', NOW(), NULL, NOW(), NOW(), 1, 1, NULL),
(6, 8, 'EMP006', 'Frank', 'Miller', '1980-09-03', '2015-02-11', 'frank.miller@example.com', 'fmiller', '303 Cedar St, Anytown', '555-0111', 'Grace Miller (Daughter) 555-0112', 'HR Manager', 4, NULL, 'Active', NOW(), NULL, NOW(), NOW(), 1, 1, NULL),
(7, 9, 'EMP007', 'Grace', 'Wilson', '1995-04-18', '2022-08-01', 'grace.wilson@example.com', 'gwilson', '404 Elm St, Anytown', '555-0113', 'Henry Wilson (Father) 555-0114', 'Accountant', NULL, NULL, 'Active', NOW(), NULL, NOW(), NOW(), 1, 1, NULL),
(8, 10, 'EMP008', 'Henry', 'Moore', '1975-12-01', '2010-01-05', 'henry.moore@example.com', 'hmoore', '505 Spruce St, Anytown', '555-0115', 'Ivy Moore (Wife) 555-0116', 'CEO', NULL, NULL, 'Active', NOW(), NULL, NOW(), NOW(), 1, 1, NULL),
(9, 11, 'EMP009', 'Ivy', 'Taylor', '1993-06-22', '2023-01-09', 'ivy.taylor@example.com', 'itaylor', '606 Walnut St, Anytown', '555-0117', 'Jack Taylor (Brother) 555-0118', 'Software Engineer', 2, 5, 'Active', NOW(), NULL, NOW(), NOW(), 1, 1, NULL),
(10, 12, 'EMP010', 'Jack', 'Anderson', '1991-03-10', '2023-02-20', 'jack.anderson@example.com', 'janderson', '707 Chestnut St, Anytown', '555-0119', 'Karen Anderson (Sister) 555-0120', 'Marketing Specialist', 5, NULL, 'Active', NOW(), NULL, NOW(), NOW(), 1, 1, NULL),
(11, 13, 'EMP011', 'Karen', 'Thomas', '1994-10-05', '2022-06-15', 'karen.thomas@example.com', 'kthomas', '808 Poplar St, Anytown', '555-0121', 'Liam Thomas (Husband) 555-0122', 'QA Engineer', 1, 2, 'Active', NOW(), NULL, NOW(), NOW(), 1, 1, NULL),
(12, 14, 'EMP012', 'Liam', 'Jackson', '1989-01-20', '2021-09-01', 'liam.jackson@example.com', 'ljackson', '909 Willow St, Anytown', '555-0123', 'Mia Jackson (Wife) 555-0124', 'DevOps Engineer', 2, 5, 'Active', NOW(), NULL, NOW(), NOW(), 1, 1, NULL);


-- Update Team Leaders and Employee reporting leaders
UPDATE `teams` SET `leader_id` = 2 WHERE `id` = 1; -- Bob Johnson leads Alpha Team
UPDATE `teams` SET `leader_id` = 5 WHERE `id` = 2; -- Eve Davis leads Bravo Team
UPDATE `teams` SET `leader_id` = 4 WHERE `id` = 3; -- David Brown leads Sales Team A
UPDATE `teams` SET `leader_id` = 6 WHERE `id` = 4; -- Frank Miller leads HR Department
UPDATE `employees` SET `reporting_leader_id` = 2 WHERE `id` = 1; -- Alice reports to Bob
UPDATE `employees` SET `reporting_leader_id` = 4 WHERE `id` = 3; -- Carol reports to David
UPDATE `employees` SET `reporting_leader_id` = 5 WHERE `id` = 9; -- Ivy reports to Eve

-- Employee Skills
INSERT INTO `employee_skills` (`id`, `employee_id`, `skill_id`, `years_experience`, `self_assessment_level`, `leader_assessment_level`, `self_comment`, `leader_comment`, `created_at`, `updated_at`, `created_by`, `updated_by`) VALUES
(1, 1, 1, 3.5, 'Proficient', 'Proficient', 'Comfortable with Java 8+', 'Strong Java skills', NOW(), NOW(), 1, 1),
(2, 1, 4, 3.0, 'Proficient', 'Intermediate', 'Good experience with Spring Boot', 'Developing well in Spring Boot', NOW(), NOW(), 1, 1),
(3, 2, 1, 5.0, 'Expert', 'Expert', 'Extensive Java experience', 'Our go-to Java expert', NOW(), NOW(), 1, 1),
(4, 2, 4, 4.5, 'Expert', 'Expert', 'Deep understanding of Spring Boot', 'Excellent Spring Boot architect', NOW(), NOW(), 1, 1),
(5, 2, 7, 4.0, 'Proficient', 'Proficient', 'MySQL for several projects', 'Solid MySQL knowledge', NOW(), NOW(), 1, 1),
(6, 3, 12, 2.0, 'Intermediate', 'Intermediate', 'Improving communication', 'Good client interaction', NOW(), NOW(), 1, 1),
(7, 3, 14, 2.5, 'Proficient', 'Intermediate', 'Good at finding solutions', 'Shows promise in problem solving', NOW(), NOW(), 1, 1),
(8, 5, 2, 6.0, 'Expert', 'Expert', 'Python expert', 'Leads Python initiatives', NOW(), NOW(), 1, 1),
(9, 5, 10, 4.0, 'Proficient', 'Proficient', 'Experienced with AWS services', 'Handles AWS deployments well', NOW(), NOW(), 1, 1),
(10, 9, 3, 1.5, 'Intermediate', 'Beginner', 'Learning JavaScript and React', 'Eager to learn frontend tech', NOW(), NOW(), 1, 1),
(11, 9, 5, 1.0, 'Beginner', 'Beginner', NULL, NULL, NOW(), NOW(), 1, 1),
(12, 11, 1, 2.0, 'Intermediate', 'Intermediate', 'Java for test automation', NULL, NOW(), NOW(), 1, 1);


-- Employee Status Log
INSERT INTO `employee_status_log` (`id`, `employee_id`, `status`, `project_name`, `client_name`, `note`, `start_date`, `expected_end_date`, `allocation_percentage`, `is_billable`, `contract_id`, `log_timestamp`, `created_at`, `updated_at`, `created_by`, `updated_by`) VALUES
(1, 1, 'Active - Project A', 'Project Alpha', 'Client X', 'Joined Project Alpha', '2023-01-15', '2023-12-31', 100, 1, 1, NOW(), NOW(), NOW(), 1, 1),
(2, 2, 'Active - Project A', 'Project Alpha', 'Client X', 'Leading Project Alpha tasks', '2023-01-10', '2023-12-31', 100, 1, 1, NOW(), NOW(), NOW(), 1, 1),
(3, 9, 'Active - Project B', 'Project Bravo', 'Client Y', 'Joined Project Bravo', '2023-02-01', '2024-01-31', 80, 1, 2, NOW(), NOW(), NOW(), 1, 1);

-- Opportunities (10 opportunities)
-- assigned_to_id refers to a user_id (typically sales user)
-- created_by_id refers to a user_id
INSERT INTO `opportunities` (`id`, `code`, `hubspot_id`, `name`, `description`, `client_name`, `client_contact`, `client_email`, `client_phone`, `client_address`, `client_website`, `client_industry`, `amount`, `currency`, `status`, `deal_size`, `source`, `external_id`, `closing_date`, `closing_probability`, `created_by_user_id`, `assigned_to_user_id`, `last_interaction_date`, `priority`, `follow_up_status`, `hubspot_created_at`, `hubspot_last_updated_at`, `sync_status`, `last_sync_at`, `created_at`, `updated_at`, `deleted_at`) VALUES
(1, 'OPP001', 'HS001', 'New CRM System for Acme Corp', 'Requirement for a new CRM system implementation.', 'Acme Corp', 'John Doe', 'john.doe@acme.com', '555-1111', '1 Acme Way', 'www.acme.com', 'Manufacturing', 50000.00, 'USD', 'Prospecting', 'Medium', 'Referral', NULL, '2024-08-30', 20, 5, 5, NOW(), 0, 'Initial Contact', NOW(), NOW(), 'Synced', NOW(), NOW(), NOW(), NULL),
(2, 'OPP002', 'HS002', 'Mobile App Development for Beta Ltd', 'Development of iOS and Android apps.', 'Beta Ltd', 'Jane Smith', 'jane.smith@betaltd.com', '555-2222', '2 Beta Rd', 'www.betaltd.com', 'Retail', 75000.00, 'USD', 'Qualification', 'Large', 'Website', NULL, '2024-09-15', 40, 5, 5, NOW(), 1, 'Follow-up Scheduled', NOW(), NOW(), 'Synced', NOW(), NOW(), NOW(), NULL),
(3, 'OPP003', 'HS003', 'Cloud Migration for Gamma Inc', 'Migrate on-premise servers to AWS.', 'Gamma Inc', 'Peter Jones', 'peter.jones@gammainc.com', '555-3333', '3 Gamma Blvd', 'www.gammainc.com', 'Finance', 120000.00, 'USD', 'Proposal Sent', 'Large', 'Partner', NULL, '2024-07-31', 60, 6, 6, NOW(), 1, 'Proposal Review', NOW(), NOW(), 'Synced', NOW(), NOW(), NOW(), NULL),
(4, 'OPP004', 'HS004', 'Website Redesign for Delta LLC', 'Complete redesign of corporate website.', 'Delta LLC', 'Alice Green', 'alice.green@deltallc.com', '555-4444', '4 Delta Dr', 'www.deltallc.com', 'Healthcare', 30000.00, 'USD', 'Negotiation', 'Small', 'Cold Call', NULL, '2024-06-30', 75, 5, 5, NOW(), 0, 'Contract Pending', NOW(), NOW(), 'Synced', NOW(), NOW(), NOW(), NULL),
(5, 'OPP005', 'HS005', 'Data Analytics Platform for Epsilon Co', 'Build a custom data analytics platform.', 'Epsilon Co', 'Bob White', 'bob.white@epsilon.co', '555-5555', '5 Epsilon Ave', 'www.epsilon.co', 'Technology', 200000.00, 'USD', 'Closed Won', 'X-Large', 'Existing Client', NULL, '2024-05-30', 100, 6, 6, NOW(), 1, 'Won', NOW(), NOW(), 'Synced', NOW(), NOW(), NOW(), NULL),
(6, 'OPP006', 'HS006', 'IT Support Services for Zeta Group', 'Ongoing IT support contract.', 'Zeta Group', 'Carol Black', 'carol.black@zetagroup.com', '555-6666', '6 Zeta St', 'www.zetagroup.com', 'Services', 15000.00, 'USD', 'Closed Lost', 'Small', 'Referral', NULL, '2024-04-30', 0, 5, 5, NOW(), 0, 'Lost to Competitor', NOW(), NOW(), 'Synced', NOW(), NOW(), NOW(), NULL),
(7, 'OPP007', 'HS007', 'E-commerce Platform for Eta Store', 'New e-commerce website development.', 'Eta Store', 'David Blue', 'david.blue@etastore.com', '555-7777', '7 Eta Pl', 'www.etastore.com', 'Retail', 60000.00, 'USD', 'Prospecting', 'Medium', 'Marketing Campaign', NULL, '2024-10-15', 15, 6, 6, NOW(), 0, 'Initial Contact', NOW(), NOW(), 'Synced', NOW(), NOW(), NOW(), NULL),
(8, 'OPP008', 'HS008', 'Custom Software for Theta Solutions', 'Bespoke software for internal operations.', 'Theta Solutions', 'Eve Red', 'eve.red@thetasolutions.com', '555-8888', '8 Theta Ct', 'www.thetasolutions.com', 'Consulting', 90000.00, 'USD', 'Qualification', 'Large', 'Website', NULL, '2024-11-30', 30, 5, 5, NOW(), 1, 'Needs Analysis', NOW(), NOW(), 'Synced', NOW(), NOW(), NOW(), NULL),
(9, 'OPP009', 'HS009', 'Security Audit for Iota Systems', 'Comprehensive security audit and report.', 'Iota Systems', 'Frank Purple', 'frank.purple@iotasystems.com', '555-9999', '9 Iota Ln', 'www.iotasystems.com', 'IT Security', 25000.00, 'USD', 'Proposal Sent', 'Small', 'Partner', NULL, '2024-07-20', 50, 6, 6, NOW(), 0, 'Awaiting Feedback', NOW(), NOW(), 'Synced', NOW(), NOW(), NOW(), NULL),
(10, 'OPP010', 'HS010', 'Training Program for Kappa Edu', 'Develop and deliver a training program.', 'Kappa Edu', 'Grace Orange', 'grace.orange@kappaedu.com', '555-1010', '10 Kappa Sq', 'www.kappaedu.com', 'Education', 40000.00, 'USD', 'Prospecting', 'Medium', 'Referral', NULL, '2024-12-15', 10, 5, 5, NOW(), 0, 'Initial Contact', NOW(), NOW(), 'Synced', NOW(), NOW(), NOW(), NULL);

-- Opportunity Assignments (Example: some opportunities might be assigned to employees other than sales for technical lead)
INSERT INTO `opportunity_assignments` (`id`, `opportunity_id`, `employee_id`, `assigned_at`, `created_at`, `updated_at`) VALUES
(1, 1, 5, NOW(), NOW(), NOW()), -- Eve Davis (Dev Lead) assigned to OPP001 for technical consultation
(2, 2, 5, NOW(), NOW(), NOW()), -- Eve Davis (Dev Lead) assigned to OPP002
(3, 3, 2, NOW(), NOW(), NOW()); -- Bob Johnson (Senior SE) assigned to OPP003

-- Opportunity Notes
INSERT INTO `opportunity_notes` (`id`, `opportunity_id`, `author_user_id`, `content`, `activity_type`, `meeting_date`, `is_private`, `created_at`, `updated_at`) VALUES
(1, 1, 5, 'Initial call with John Doe. Seems interested. Sent brochure.', 'Call', NOW(), 0, NOW(), NOW()),
(2, 2, 5, 'Meeting scheduled for next week to discuss requirements.', 'Meeting', '2024-06-10 10:00:00', 0, NOW(), NOW()),
(3, 3, 6, 'Proposal sent. They are reviewing with their technical team.', 'Email', NOW(), 0, NOW(), NOW()),
(4, 5, 6, 'Contract signed. Project kickoff next month.', 'Update', NOW(), 0, NOW(), NOW());

-- Contracts (10 contracts)
-- opportunity_id links to opportunities.
-- assigned_sales_user_id links to a sales user.
-- created_by_user_id and updated_by_user_id link to users.
INSERT INTO `contracts` (`id`, `contract_code`, `name`, `client_name`, `opportunity_id`, `sign_date`, `effective_date`, `expiry_date`, `total_value`, `currency`, `contract_type`, `assigned_sales_user_id`, `status`, `description`, `created_at`, `updated_at`, `created_by_user_id`, `updated_by_user_id`, `deleted_at`) VALUES
(1, 'CTR001', 'CRM System for Acme Corp - Phase 1', 'Acme Corp', 1, '2024-06-01', '2024-06-15', '2025-06-14', 45000.00, 'USD', 'Fixed Price', 5, 'Active', 'Phase 1 of CRM implementation.', NOW(), NOW(), 5, 5, NULL),
(2, 'CTR002', 'Mobile App Dev - Beta Ltd', 'Beta Ltd', 2, '2024-07-01', '2024-07-10', '2025-01-10', 70000.00, 'USD', 'Time & Material', 5, 'Pending Start', 'iOS and Android app development.', NOW(), NOW(), 5, 5, NULL),
(3, 'CTR003', 'Cloud Migration - Gamma Inc', 'Gamma Inc', 3, '2024-05-15', '2024-06-01', '2024-12-01', 110000.00, 'USD', 'Fixed Price', 6, 'Active', 'AWS migration project.', NOW(), NOW(), 6, 6, NULL),
(4, 'CTR004', 'Website Redesign - Delta LLC', 'Delta LLC', 4, '2024-04-20', '2024-05-01', '2024-08-01', 28000.00, 'USD', 'Fixed Price', 5, 'Completed', 'Corporate website redesign.', NOW(), NOW(), 5, 5, NULL),
(5, 'CTR005', 'Data Analytics Platform - Epsilon Co', 'Epsilon Co', 5, '2024-03-10', '2024-04-01', '2025-03-31', 180000.00, 'USD', 'Time & Material', 6, 'Active', 'Custom analytics platform build.', NOW(), NOW(), 6, 6, NULL),
(6, 'CTR006', 'Support Services - Alpha IT', 'Alpha IT Solutions', NULL, '2023-01-01', '2023-01-01', '2024-12-31', 2000.00, 'USD', 'Retainer', 5, 'Active', 'Monthly IT support retainer.', NOW(), NOW(), 5, 5, NULL),
(7, 'CTR007', 'Consulting Services - Bravo Consult', 'Bravo Consulting', NULL, '2023-05-01', '2023-05-15', '2024-05-14', 25000.00, 'USD', 'Fixed Price', 6, 'Active', 'Strategic consulting engagement.', NOW(), NOW(), 6, 6, NULL),
(8, 'CTR008', 'Software License - Charlie Software', 'Charlie Software Ltd', NULL, '2024-02-01', '2024-02-01', '2025-01-31', 10000.00, 'USD', 'Subscription', 5, 'Active', 'Annual software license.', NOW(), NOW(), 5, 5, NULL),
(9, 'CTR009', 'Hardware Maintenance - Delta Hardware', 'Delta Hardware Inc', NULL, '2024-03-15', '2024-04-01', '2025-03-31', 5000.00, 'USD', 'Retainer', 6, 'Pending Start', 'Hardware maintenance contract.', NOW(), NOW(), 6, 6, NULL),
(10, 'CTR010', 'Project Phoenix - Epsilon Ent', 'Epsilon Enterprises', NULL, '2024-07-20', '2024-08-01', '2025-07-31', 150000.00, 'USD', 'Fixed Price', 5, 'Negotiation', 'Large scale development project.', NOW(), NOW(), 5, 5, NULL);

-- Contract Payment Terms
INSERT INTO `contract_payment_terms` (`id`, `contract_id`, `term_number`, `description`, `expected_payment_date`, `expected_amount`, `currency`, `payment_status`, `actual_payment_date`, `actual_amount_paid`, `notes`, `created_at`, `updated_at`, `created_by_user_id`, `updated_by_user_id`) VALUES
(1, 1, 1, 'Upfront 50%', '2024-06-15', 22500.00, 'USD', 'Paid', '2024-06-14', 22500.00, 'Received', NOW(), NOW(), 5, 5),
(2, 1, 2, 'Final 50% on Completion', '2025-06-14', 22500.00, 'USD', 'Pending', NULL, NULL, NULL, NOW(), NOW(), 5, 5),
(3, 3, 1, 'Milestone 1', '2024-07-01', 40000.00, 'USD', 'Pending', NULL, NULL, NULL, NOW(), NOW(), 6, 6),
(4, 3, 2, 'Milestone 2', '2024-09-01', 40000.00, 'USD', 'Pending', NULL, NULL, NULL, NOW(), NOW(), 6, 6),
(5, 3, 3, 'Final Payment', '2024-12-01', 30000.00, 'USD', 'Pending', NULL, NULL, NULL, NOW(), NOW(), 6, 6),
(6, 5, 1, 'Monthly Billing - April', '2024-05-05', 15000.00, 'USD', 'Paid', '2024-05-04', 15000.00, 'First month', NOW(), NOW(), 6, 6),
(7, 5, 2, 'Monthly Billing - May', '2024-06-05', 15000.00, 'USD', 'Pending', NULL, NULL, NULL, NOW(), NOW(), 6, 6);

-- Contract Employees (linking employees to contracts)
INSERT INTO `contract_employees` (`id`, `contract_id`, `employee_id`, `role`, `allocation_percentage`, `start_date`, `end_date`, `bill_rate`, `created_at`, `updated_at`) VALUES
(1, 1, 1, 'Software Engineer', 100.00, '2024-06-15', '2025-06-14', 80.00, NOW(), NOW()),
(2, 1, 2, 'Senior Software Engineer', 50.00, '2024-06-15', '2025-06-14', 120.00, NOW(), NOW()),
(3, 3, 9, 'Software Engineer', 100.00, '2024-06-01', '2024-12-01', 75.00, NOW(), NOW()),
(4, 3, 5, 'Development Lead', 25.00, '2024-06-01', '2024-12-01', 150.00, NOW(), NOW()),
(5, 5, 11, 'QA Engineer', 80.00, '2024-04-01', '2025-03-31', 60.00, NOW(), NOW()),
(6, 5, 12, 'DevOps Engineer', 70.00, '2024-04-01', '2025-03-31', 90.00, NOW(), NOW());

-- Employee Costs (Monthly cost for each of the 10 employees for a few months)
-- Year 2024, Months 4, 5, 6 (April, May, June)
INSERT INTO `employee_costs` (`id`, `employee_id`, `year`, `month`, `cost_amount`, `basic_salary`, `allowance`, `overtime`, `other_costs`, `currency`, `note`, `created_at`, `updated_at`, `created_by`, `updated_by`, `deleted_at`) VALUES
-- EMP001 Alice Smith (SE, ~3000/month)
(1, 1, 2024, 4, 3000.00, 2800.00, 200.00, 0.00, 0.00, 'USD', 'April Cost', NOW(), NOW(), 1, 1, NULL),
(2, 1, 2024, 5, 3000.00, 2800.00, 200.00, 0.00, 0.00, 'USD', 'May Cost', NOW(), NOW(), 1, 1, NULL),
(3, 1, 2024, 6, 3050.00, 2800.00, 200.00, 50.00, 0.00, 'USD', 'June Cost with OT', NOW(), NOW(), 1, 1, NULL),
-- EMP002 Bob Johnson (Sr. SE, ~4500/month)
(4, 2, 2024, 4, 4500.00, 4200.00, 300.00, 0.00, 0.00, 'USD', 'April Cost', NOW(), NOW(), 1, 1, NULL),
(5, 2, 2024, 5, 4500.00, 4200.00, 300.00, 0.00, 0.00, 'USD', 'May Cost', NOW(), NOW(), 1, 1, NULL),
(6, 2, 2024, 6, 4500.00, 4200.00, 300.00, 0.00, 0.00, 'USD', 'June Cost', NOW(), NOW(), 1, 1, NULL),
-- EMP003 Carol Williams (Sales Exec, ~3500/month base + commission)
(7, 3, 2024, 4, 3500.00, 3000.00, 500.00, 0.00, 0.00, 'USD', 'April Cost', NOW(), NOW(), 1, 1, NULL),
(8, 3, 2024, 5, 3500.00, 3000.00, 500.00, 0.00, 0.00, 'USD', 'May Cost', NOW(), NOW(), 1, 1, NULL),
(9, 3, 2024, 6, 3500.00, 3000.00, 500.00, 0.00, 0.00, 'USD', 'June Cost', NOW(), NOW(), 1, 1, NULL),
-- EMP004 David Brown (Sales Mgr, ~5000/month base + commission)
(10, 4, 2024, 4, 5000.00, 4500.00, 500.00, 0.00, 0.00, 'USD', 'April Cost', NOW(), NOW(), 1, 1, NULL),
(11, 4, 2024, 5, 5000.00, 4500.00, 500.00, 0.00, 0.00, 'USD', 'May Cost', NOW(), NOW(), 1, 1, NULL),
(12, 4, 2024, 6, 5000.00, 4500.00, 500.00, 0.00, 0.00, 'USD', 'June Cost', NOW(), NOW(), 1, 1, NULL),
-- EMP005 Eve Davis (Dev Lead, ~5500/month)
(13, 5, 2024, 4, 5500.00, 5200.00, 300.00, 0.00, 0.00, 'USD', 'April Cost', NOW(), NOW(), 1, 1, NULL),
(14, 5, 2024, 5, 5500.00, 5200.00, 300.00, 0.00, 0.00, 'USD', 'May Cost', NOW(), NOW(), 1, 1, NULL),
(15, 5, 2024, 6, 5500.00, 5200.00, 300.00, 0.00, 0.00, 'USD', 'June Cost', NOW(), NOW(), 1, 1, NULL),
-- EMP006 Frank Miller (HR Mgr, ~4800/month)
(16, 6, 2024, 4, 4800.00, 4500.00, 300.00, 0.00, 0.00, 'USD', 'April Cost', NOW(), NOW(), 1, 1, NULL),
(17, 6, 2024, 5, 4800.00, 4500.00, 300.00, 0.00, 0.00, 'USD', 'May Cost', NOW(), NOW(), 1, 1, NULL),
(18, 6, 2024, 6, 4800.00, 4500.00, 300.00, 0.00, 0.00, 'USD', 'June Cost', NOW(), NOW(), 1, 1, NULL),
-- EMP007 Grace Wilson (Accountant, ~3200/month)
(19, 7, 2024, 4, 3200.00, 3000.00, 200.00, 0.00, 0.00, 'USD', 'April Cost', NOW(), NOW(), 1, 1, NULL),
(20, 7, 2024, 5, 3200.00, 3000.00, 200.00, 0.00, 0.00, 'USD', 'May Cost', NOW(), NOW(), 1, 1, NULL),
(21, 7, 2024, 6, 3200.00, 3000.00, 200.00, 0.00, 0.00, 'USD', 'June Cost', NOW(), NOW(), 1, 1, NULL),
-- EMP008 Henry Moore (CEO, ~10000/month)
(22, 8, 2024, 4, 10000.00, 9500.00, 500.00, 0.00, 0.00, 'USD', 'April Cost', NOW(), NOW(), 1, 1, NULL),
(23, 8, 2024, 5, 10000.00, 9500.00, 500.00, 0.00, 0.00, 'USD', 'May Cost', NOW(), NOW(), 1, 1, NULL),
(24, 8, 2024, 6, 10000.00, 9500.00, 500.00, 0.00, 0.00, 'USD', 'June Cost', NOW(), NOW(), 1, 1, NULL),
-- EMP009 Ivy Taylor (SE, ~2800/month)
(25, 9, 2024, 4, 2800.00, 2600.00, 200.00, 0.00, 0.00, 'USD', 'April Cost', NOW(), NOW(), 1, 1, NULL),
(26, 9, 2024, 5, 2800.00, 2600.00, 200.00, 0.00, 0.00, 'USD', 'May Cost', NOW(), NOW(), 1, 1, NULL),
(27, 9, 2024, 6, 2800.00, 2600.00, 200.00, 0.00, 0.00, 'USD', 'June Cost', NOW(), NOW(), 1, 1, NULL),
-- EMP010 Jack Anderson (Marketing, ~3300/month)
(28, 10, 2024, 4, 3300.00, 3000.00, 300.00, 0.00, 0.00, 'USD', 'April Cost', NOW(), NOW(), 1, 1, NULL),
(29, 10, 2024, 5, 3300.00, 3000.00, 300.00, 0.00, 0.00, 'USD', 'May Cost', NOW(), NOW(), 1, 1, NULL),
(30, 10, 2024, 6, 3300.00, 3000.00, 300.00, 0.00, 0.00, 'USD', 'June Cost', NOW(), NOW(), 1, 1, NULL);

-- Employee Revenues (Based on contract employees for June 2024)
-- Alice (EMP001) on CTR001, 100% alloc, $80/hr bill rate. Assume 160 billable hours. 160*80 = 12800
INSERT INTO `employee_revenues` (`id`, `employee_id`, `contract_id`, `year`, `month`, `billing_rate`, `allocation_percentage`, `calculated_revenue`, `currency`, `note`, `created_at`, `updated_at`, `created_by`, `updated_by`, `deleted_at`) VALUES
(1, 1, 1, 2024, 6, 80.00, 100.00, 12800.00, 'USD', 'June Revenue from CTR001', NOW(), NOW(), 1, 1, NULL),
-- Bob (EMP002) on CTR001, 50% alloc, $120/hr bill rate. Assume 80 billable hours (50% of 160). 80*120 = 9600
(2, 2, 1, 2024, 6, 120.00, 50.00, 9600.00, 'USD', 'June Revenue from CTR001', NOW(), NOW(), 1, 1, NULL),
-- Ivy (EMP009) on CTR003, 100% alloc, $75/hr bill rate. Assume 160 billable hours. 160*75 = 12000
(3, 9, 3, 2024, 6, 75.00, 100.00, 12000.00, 'USD', 'June Revenue from CTR003', NOW(), NOW(), 1, 1, NULL),
-- Eve (EMP005) on CTR003, 25% alloc, $150/hr bill rate. Assume 40 billable hours. 40*150 = 6000
(4, 5, 3, 2024, 6, 150.00, 25.00, 6000.00, 'USD', 'June Revenue from CTR003', NOW(), NOW(), 1, 1, NULL),
-- Karen (EMP011) on CTR005, 80% alloc, $60/hr bill rate. Assume 128 billable hours. 128*60 = 7680
(5, 11, 5, 2024, 6, 60.00, 80.00, 7680.00, 'USD', 'June Revenue from CTR005', NOW(), NOW(), 1, 1, NULL),
-- Liam (EMP012) on CTR005, 70% alloc, $90/hr bill rate. Assume 112 billable hours. 112*90 = 10080
(6, 12, 5, 2024, 6, 90.00, 70.00, 10080.00, 'USD', 'June Revenue from CTR005', NOW(), NOW(), 1, 1, NULL);


-- System Configs
INSERT INTO `system_configs` (`id`, `config_key`, `config_value`, `description`, `created_at`, `updated_at`, `created_by`, `updated_by`) VALUES
(1, 'SYSTEM_CURRENCY', 'USD', 'Default currency for the system', NOW(), NOW(), 1, 1),
(2, 'MAX_SESSION_TIMEOUT', '3600', 'Maximum session timeout in seconds', NOW(), NOW(), 1, 1),
(3, 'HUBSPOT_API_KEY', 'dummy_api_key_replace_me', 'API Key for Hubspot Integration', NOW(), NOW(), 1, 1);

-- Enable foreign key checks
SET FOREIGN_KEY_CHECKS=1;

-- End of Data Dump 