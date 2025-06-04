# Internal Management System - Backend

Hệ thống Quản lý Nội bộ (Internal Management System) - Backend

## 1. Giới thiệu
Backend hệ thống quản lý nội bộ cho doanh nghiệp, phát triển bằng Java Spring Boot, hỗ trợ quản lý nhân sự, hiệu suất, cơ hội kinh doanh, hợp đồng, dashboard báo cáo và quản trị hệ thống.

## 2. Kiến trúc tổng quan
- **Ngôn ngữ:** Java 17+ (hoặc 11+)
- **Framework:** Spring Boot
- **Kiến trúc:** Module hóa theo domain (HRM, Margin, Opportunity, Contract, Dashboard, Admin)
- **Database:** MySQL (cấu hình trong `application.yml`)
- **Bảo mật:** Spring Security, JWT
- **Tích hợp:** Hubspot API, AI Agent (tùy chọn)

## 3. Cấu trúc thư mục chính

```
src/main/java/com/company/internalmgmt/
 ├── config/         # Cấu hình chung (DB, Security, Swagger...)
 ├── common/         # Class dùng chung (exception, util, ...)
 ├── modules/
 │    ├── hrm/           # Quản lý Nhân sự
 │    ├── margin/        # Hiệu suất & Margin
 │    ├── opportunity/   # Cơ hội Kinh doanh
 │    ├── contract/      # Hợp đồng & Doanh thu
 │    ├── dashboard/     # Dashboard & Báo cáo
 │    └── admin/         # Quản trị hệ thống
 └── security/       # Xác thực, phân quyền
```

## 4. Hướng dẫn build & chạy

### Yêu cầu
- Java 17+ (hoặc 11+)
- Maven 3.8+
- Database: MySQL

### Build
```bash
mvn clean install
```

### Run (dev)
```bash
mvn spring-boot:run
```

### Cấu hình DB
- Sửa file `src/main/resources/application.yml` cho phù hợp với môi trường của bạn.

## 5. Mô tả các module chính
- **HRM:** Quản lý hồ sơ, skills, trạng thái, phân bổ dự án nhân sự.
- **Margin:** Quản lý chi phí, doanh thu, margin, cảnh báo trạng thái margin.
- **Opportunity:** Quản lý cơ hội kinh doanh, đồng bộ Hubspot, phân công, ghi chú, trạng thái follow-up.
- **Contract:** Quản lý hợp đồng, liên kết cơ hội, tracking thu tiền, KPI doanh thu.
- **Dashboard:** Dashboard tổng hợp, báo cáo chi tiết, xuất file.
- **Admin:** Quản lý người dùng, vai trò, phân quyền, cấu hình hệ thống.

## 6. Đóng góp
- Pull request, issue hoặc liên hệ team phát triển.

## 7. Xử lý dùng chung & toàn cục (Global)

### 7.1. Xử lý Exception toàn cục
- **GlobalExceptionHandler** (`common/exception/GlobalExceptionHandler.java`):
  - Sử dụng `@ControllerAdvice` để bắt và chuẩn hóa tất cả các exception trả về cho client.
  - Tự động trả về các mã lỗi chuẩn hóa (E1001, E1002, E2000, ...), kèm message và chi tiết lỗi.
  - Hỗ trợ các loại lỗi: NotFound, BadRequest, AccessDenied, Validation, File upload, Generic error.
- **ErrorResponse** (`common/exception/ErrorResponse.java`):
  - Định dạng chuẩn cho mọi lỗi trả về API (code, message, details, timestamp).

### 7.2. Phân quyền & xác thực người dùng
- **AuthorizationService** (`security/AuthorizationService.java`):
  - Cung cấp các hàm xác định scope truy cập (all, team, own, assigned) dựa trên quyền của user hiện tại.
  - Hỗ trợ kiểm tra quyền động trong service/controller (`hasPermission`, `hasAnyPermission`).
- **@CurrentUser** (`security/jwt/CurrentUser.java`):
  - Annotation custom để inject userId hiện tại vào controller method parameter.
  - Dùng với Spring Security + JWT, tự động lấy từ token đã xác thực.

### 7.3. Logging
- Sử dụng `@Slf4j` (Lombok) cho logging nhất quán trong toàn bộ service/controller.

### 7.4. Best Practice
- Tất cả API nên trả về lỗi theo chuẩn `ErrorResponse`.
- Không throw exception thô ra ngoài controller, luôn để GlobalExceptionHandler xử lý.
- Sử dụng annotation `@PreAuthorize` để kiểm soát quyền ở controller layer.
- Inject userId qua `@CurrentUser` thay vì tự lấy từ SecurityContext.

--- 