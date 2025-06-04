# Module Margin

## 1. Giới thiệu
Module Margin quản lý hiệu suất tài chính của nhân sự, bao gồm:
- Import chi phí nhân sự (từ file hoặc nhập tay)
- Tính toán doanh thu, margin từng nhân viên
- Tổng hợp margin theo team, trạng thái
- Cảnh báo trạng thái margin (Red/Yellow/Green)
- Phân quyền chặt chẽ theo vai trò (Division Manager, Leader, Employee)

## 2. Các API chính
- `GET /api/v1/margins/employee` — Lấy danh sách margin nhân sự (filter, paging, sorting)
- `GET /api/v1/margins/summary` — Tổng hợp margin theo team/trạng thái
- `POST /api/v1/margins/costs/import` — Import chi phí nhân sự từ file
- `POST /api/v1/margins/costs` — Nhập/ghi đè chi phí nhân sự thủ công

## 3. Phân quyền & Security
- Chỉ Division Manager có quyền truy cập toàn bộ (`margin:read:all`, `employee-cost:read:all`)
- Leader chỉ xem được dữ liệu team mình (`margin:read:team`, `employee-cost:read:team`)
- Employee chỉ xem được dữ liệu cá nhân
- Import/Update chi phí chỉ dành cho Division Manager (`employee-cost:import`, `employee-cost:update:all`)
- Kiểm tra scope và quyền được thực hiện ở cả Service và Controller

## 4. Các lưu ý & TODO còn lại
- [x] Viết unit tests cho MarginService (đảm bảo coverage ≥ 80%)
- [x] Viết integration tests cho MarginController (MockMvc/WebTestClient)
- [ ] Seed data cho ngưỡng margin mặc định (`system_configs`)
- [ ] Annotate API với Swagger/OpenAPI (`@Operation`, `@ApiResponse`)
- [ ] Kiểm thử hiệu năng (response time < 2s cho get margins)
- [ ] Viết hướng dẫn sử dụng API (Postman collection)
- [ ] Tối ưu hóa truy vấn, kiểm tra thực tế phân quyền
- [ ] Xử lý triệt để các linter error còn lại

## 5. Tài liệu liên quan
- [permissions_definition.md](../../../Document/permissions_definition.md)
- [Task Plans/Margin_Module_Plan.md](../../../Task Plans/Margin_Module_Plan.md)

---
*Vui lòng tham khảo tài liệu và checklist để đảm bảo triển khai đúng yêu cầu nghiệp vụ và bảo mật.* 