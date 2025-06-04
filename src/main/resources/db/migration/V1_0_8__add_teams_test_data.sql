-- Thêm dữ liệu test cho bảng teams

-- Đầu tiên xóa dữ liệu hiện có (nếu cần)
-- DELETE FROM teams;

-- Thêm các nhóm chính (parent teams)
INSERT INTO teams (name, department, description, created_at, updated_at) VALUES
('Engineering', 'Technical', 'Nhóm phát triển và vận hành các sản phẩm công nghệ', NOW(), NOW()),
('Sales', 'Business', 'Nhóm phát triển kinh doanh và bán hàng', NOW(), NOW()),
('HR & Admin', 'Operations', 'Nhóm quản lý nhân sự và hành chính', NOW(), NOW()),
('Product', 'Technical', 'Nhóm quản lý sản phẩm', NOW(), NOW()),
('Marketing', 'Business', 'Nhóm tiếp thị và truyền thông', NOW(), NOW());

-- Lấy ID của các nhóm chính để tạo các nhóm con
SET @engineering_id = (SELECT id FROM teams WHERE name = 'Engineering');
SET @sales_id = (SELECT id FROM teams WHERE name = 'Sales');
SET @hr_admin_id = (SELECT id FROM teams WHERE name = 'HR & Admin');
SET @product_id = (SELECT id FROM teams WHERE name = 'Product');
SET @marketing_id = (SELECT id FROM teams WHERE name = 'Marketing');

-- Thêm các nhóm con
INSERT INTO teams (name, department, description, parent_team_id, created_at, updated_at) VALUES
('Backend', 'Technical', 'Phát triển các dịch vụ back-end', @engineering_id, NOW(), NOW()),
('Frontend', 'Technical', 'Phát triển giao diện người dùng', @engineering_id, NOW(), NOW()),
('DevOps', 'Technical', 'Quản lý hạ tầng và triển khai', @engineering_id, NOW(), NOW()),
('QA', 'Technical', 'Đảm bảo chất lượng phần mềm', @engineering_id, NOW(), NOW()),
('Corporate Sales', 'Business', 'Bán hàng cho khách hàng doanh nghiệp', @sales_id, NOW(), NOW()),
('Partner Sales', 'Business', 'Phát triển quan hệ đối tác', @sales_id, NOW(), NOW()),
('Recruitment', 'HR', 'Tuyển dụng nhân sự mới', @hr_admin_id, NOW(), NOW()),
('Office Management', 'Admin', 'Quản lý văn phòng và cơ sở vật chất', @hr_admin_id, NOW(), NOW()),
('Product Management', 'Product', 'Quản lý chiến lược sản phẩm', @product_id, NOW(), NOW()),
('UX/UI Design', 'Product', 'Thiết kế trải nghiệm người dùng', @product_id, NOW(), NOW()),
('Digital Marketing', 'Marketing', 'Marketing trực tuyến', @marketing_id, NOW(), NOW()),
('Content', 'Marketing', 'Sản xuất nội dung', @marketing_id, NOW(), NOW());

-- Cập nhật leader cho các nhóm (giả sử có sẵn employee với id 1-5)
-- Nếu chưa có employee nào, hãy comment các câu lệnh này lại
-- UPDATE teams SET leader_id = 1 WHERE name = 'Engineering';
-- UPDATE teams SET leader_id = 2 WHERE name = 'Sales';
-- UPDATE teams SET leader_id = 3 WHERE name = 'HR & Admin';
-- UPDATE teams SET leader_id = 4 WHERE name = 'Product';
-- UPDATE teams SET leader_id = 5 WHERE name = 'Marketing'; 