# Tài liệu API - Module Dashboard

## 1. Dashboard APIs

### 1.1. GET /api/v1/dashboard/summary
**Mô tả:** Lấy thông tin tổng quan cho dashboard, bao gồm các widget được chỉ định.

**Quyền truy cập:**  
`dashboard:read:all`, `dashboard:read:team`, `dashboard:read:own`

**Tham số truy vấn:**
| Tên | Kiểu | Bắt buộc | Mặc định | Mô tả |
|-----|------|----------|----------|-------|
| fromDate | Date (yyyy-MM-dd) | Không | Ngày đầu tháng hiện tại | Ngày bắt đầu phạm vi dữ liệu |
| toDate | Date (yyyy-MM-dd) | Không | Ngày hiện tại | Ngày kết thúc phạm vi dữ liệu |
| teamId | Long | Không | - | ID của team cần lọc dữ liệu |
| widgets | List\<String\> | Không | ["opportunity_status", "margin_distribution", "revenue_summary", "employee_status", "utilization_rate"] | Danh sách các widget cần lấy dữ liệu |

**Phản hồi:**
```json
{
  "success": true,
  "data": {
    "opportunityStatus": { ... },
    "marginDistribution": { ... },
    "revenueSummary": { ... },
    "employeeStatus": { ... },
    "utilizationRate": { ... },
    "recentActivities": [ ... ]
  },
  "message": null
}
```

## 2. Reports APIs

### 2.1. GET /api/v1/reports/employee-list
**Mã API:** API-RPT-002  
**Mô tả:** Lấy báo cáo chi tiết danh sách nhân viên.

**Quyền truy cập:**  
`report:read:all`, `report:read:team`, `report:read:own`

**Tham số truy vấn:**
| Tên | Kiểu | Bắt buộc | Mặc định | Mô tả |
|-----|------|----------|----------|-------|
| teamId | Integer | Không | - | ID của team |
| position | String | Không | - | Vị trí/chức danh |
| status | String | Không | - | Trạng thái nhân viên |
| skills | List\<Integer\> | Không | - | Danh sách ID kỹ năng |
| minExperience | Integer | Không | - | Số năm kinh nghiệm tối thiểu |
| projectId | Integer | Không | - | ID dự án |
| utilization | String | Không | - | Mức độ sử dụng (under, normal, over) |
| includeSkills | Boolean | Không | true | Bao gồm thông tin kỹ năng |
| includeProjects | Boolean | Không | true | Bao gồm thông tin dự án |
| exportType | String | Không | json | Định dạng xuất (json, csv, excel) |
| page | Integer | Không | 1 | Số trang (bắt đầu từ 1) |
| size | Integer | Không | 50 | Số bản ghi mỗi trang |
| sortBy | String | Không | name | Trường để sắp xếp |
| sortDir | String | Không | asc | Hướng sắp xếp (asc, desc) |

**Phản hồi:**
```json
{
  "success": true,
  "data": {
    "summary": {
      "totalEmployees": 150,
      "activeEmployees": 120,
      "byStatus": { "active": 120, "inactive": 10, "onboarding": 15, "terminated": 5 },
      "byPosition": { "developer": 80, "tester": 30, "manager": 15, "designer": 25 }
    },
    "content": [
      {
        "id": 1,
        "employeeCode": "EMP001",
        "name": "Nguyễn Văn A",
        "position": "Senior Developer",
        "team": "Mobile Team",
        "skills": ["Java", "Android", "Kotlin"],
        "experienceYears": 5,
        "currentProject": "Banking App",
        "utilization": 85
      }
    ],
    "pageable": {
      "pageNumber": 1,
      "pageSize": 50,
      "totalPages": 3,
      "totalElements": 150,
      "sort": "name,asc"
    }
  },
  "message": null
}
```

### 2.2. GET /api/v1/reports/margin-detail
**Mã API:** API-RPT-003  
**Mô tả:** Lấy báo cáo chi tiết về biên lợi nhuận theo nhân viên/team.

**Quyền truy cập:**  
`report:read:all`, `report:read:team`

**Tham số truy vấn:**
| Tên | Kiểu | Bắt buộc | Mặc định | Mô tả |
|-----|------|----------|----------|-------|
| teamId | Integer | Không | - | ID của team |
| employeeId | Integer | Không | - | ID của nhân viên |
| period | String | Không | month | Kỳ báo cáo (month, quarter, year) |
| fromDate | Date | Không | 1 năm trước | Ngày bắt đầu |
| toDate | Date | Không | Ngày hiện tại | Ngày kết thúc |
| marginThreshold | String | Không | - | Ngưỡng biên lợi nhuận (red, yellow, green) |
| groupBy | String | Không | employee | Nhóm theo (employee, team) |
| includeDetails | Boolean | Không | true | Bao gồm chi tiết |
| exportType | String | Không | json | Định dạng xuất (json, csv, excel) |
| page | Integer | Không | 1 | Số trang |
| size | Integer | Không | 50 | Số bản ghi mỗi trang |
| sortBy | String | Không | margin | Trường để sắp xếp |
| sortDir | String | Không | desc | Hướng sắp xếp |

**Phản hồi:**
```json
{
  "success": true,
  "data": {
    "summary": {
      "averageMargin": 32.5,
      "totalRevenue": 15000000000,
      "totalCost": 10125000000,
      "marginDistribution": {
        "red": 15,
        "yellow": 25,
        "green": 60
      }
    },
    "content": [
      {
        "employeeId": 1,
        "employeeCode": "EMP001",
        "name": "Nguyễn Văn A",
        "position": "Senior Developer",
        "team": "Mobile Team",
        "cost": 25000000,
        "revenue": 40000000,
        "margin": 37.5,
        "marginStatus": "green",
        "periods": [
          {"period": "2023-01", "cost": 25000000, "revenue": 40000000, "margin": 37.5}
        ]
      }
    ],
    "pageable": {
      "pageNumber": 1,
      "pageSize": 50,
      "totalPages": 3,
      "totalElements": 120,
      "sort": "margin,desc"
    }
  },
  "message": null
}
```

### 2.3. GET /api/v1/reports/opportunity-list
**Mã API:** API-RPT-004  
**Mô tả:** Lấy báo cáo chi tiết danh sách cơ hội kinh doanh.

**Quyền truy cập:**  
`report:read:all`, `report:read:team`, `report:read:own`

**Tham số truy vấn:**
| Tên | Kiểu | Bắt buộc | Mặc định | Mô tả |
|-----|------|----------|----------|-------|
| customerId | Integer | Không | - | ID khách hàng |
| salesId | Integer | Không | - | ID nhân viên sales |
| leaderId | Integer | Không | - | ID leader phụ trách |
| dealStage | String | Không | - | Giai đoạn deal |
| followUpStatus | String | Không | - | Trạng thái theo dõi |
| onsite | Boolean | Không | - | Có yêu cầu onsite |
| fromDate | Date | Không | 1 năm trước | Ngày bắt đầu |
| toDate | Date | Không | Ngày hiện tại | Ngày kết thúc |
| keyword | String | Không | - | Từ khóa tìm kiếm |
| includeNotes | Boolean | Không | false | Bao gồm ghi chú |
| includeLeaders | Boolean | Không | true | Bao gồm thông tin leader |
| exportType | String | Không | json | Định dạng xuất |
| page | Integer | Không | 1 | Số trang |
| size | Integer | Không | 50 | Số bản ghi mỗi trang |
| sortBy | String | Không | lastInteractionDate | Trường để sắp xếp |
| sortDir | String | Không | desc | Hướng sắp xếp |

**Phản hồi:**
```json
{
  "success": true,
  "data": {
    "summary": {
      "totalOpportunities": 85,
      "totalAmount": 125000000000,
      "byStatus": {
        "new": 20,
        "contacted": 30,
        "proposal": 15,
        "negotiation": 10,
        "closed_won": 8,
        "closed_lost": 2
      }
    },
    "content": [
      {
        "id": 1,
        "code": "OPP-20230615-00001",
        "name": "Banking Mobile App Project",
        "customerName": "ABC Bank",
        "amount": 2500000000,
        "status": "proposal",
        "lastInteractionDate": "2023-06-01",
        "assignedLeaders": [
          {"id": 5, "name": "Nguyễn Văn B", "position": "Project Manager"}
        ],
        "notes": [
          {"id": 10, "content": "Customer requested demo", "createdAt": "2023-05-28"}
        ]
      }
    ],
    "pageable": {
      "pageNumber": 1,
      "pageSize": 50,
      "totalPages": 2,
      "totalElements": 85,
      "sort": "lastInteractionDate,desc"
    }
  },
  "message": null
}
```

### 2.4. GET /api/v1/reports/contract-list
**Mã API:** API-RPT-005  
**Mô tả:** Lấy báo cáo chi tiết danh sách hợp đồng.

**Quyền truy cập:**  
`report:read:all`, `report:read:team`, `report:read:own`

**Tham số truy vấn:**
| Tên | Kiểu | Bắt buộc | Mặc định | Mô tả |
|-----|------|----------|----------|-------|
| customerId | Integer | Không | - | ID khách hàng |
| salesId | Integer | Không | - | ID nhân viên sales |
| status | String | Không | - | Trạng thái hợp đồng |
| type | String | Không | - | Loại hợp đồng |
| opportunityId | Integer | Không | - | ID cơ hội |
| minValue | Double | Không | - | Giá trị tối thiểu |
| maxValue | Double | Không | - | Giá trị tối đa |
| fromDate | Date | Không | 1 năm trước | Ngày bắt đầu |
| toDate | Date | Không | Ngày hiện tại | Ngày kết thúc |
| expiryFromDate | Date | Không | - | Ngày hết hạn bắt đầu |
| expiryToDate | Date | Không | - | Ngày hết hạn kết thúc |
| paymentStatus | String | Không | - | Trạng thái thanh toán |
| keyword | String | Không | - | Từ khóa tìm kiếm |
| includePayments | Boolean | Không | true | Bao gồm thanh toán |
| includeEmployees | Boolean | Không | true | Bao gồm nhân viên |
| exportType | String | Không | json | Định dạng xuất |
| page | Integer | Không | 1 | Số trang |
| size | Integer | Không | 50 | Số bản ghi mỗi trang |
| sortBy | String | Không | signedDate | Trường để sắp xếp |
| sortDir | String | Không | desc | Hướng sắp xếp |

**Phản hồi:**
```json
{
  "success": true,
  "data": {
    "summary": {
      "totalContracts": 65,
      "totalValue": 180000000000,
      "byStatus": {
        "active": 45,
        "completed": 15,
        "expired": 5
      }
    },
    "content": [
      {
        "id": 1,
        "code": "CTR-2023-0001",
        "name": "Banking App Development",
        "customerName": "ABC Bank",
        "value": 2500000000,
        "signedDate": "2023-04-15",
        "startDate": "2023-05-01",
        "endDate": "2023-12-31",
        "status": "active",
        "paymentTerms": [
          {"id": 1, "amount": 500000000, "dueDate": "2023-05-15", "status": "paid"},
          {"id": 2, "amount": 1000000000, "dueDate": "2023-08-15", "status": "pending"}
        ],
        "assignedEmployees": [
          {"id": 5, "name": "Nguyễn Văn B", "position": "Project Manager"},
          {"id": 8, "name": "Trần Thị C", "position": "Senior Developer"}
        ]
      }
    ],
    "pageable": {
      "pageNumber": 1,
      "pageSize": 50,
      "totalPages": 2,
      "totalElements": 65,
      "sort": "signedDate,desc"
    }
  },
  "message": null
}
```

### 2.5. GET /api/v1/reports/payment-status
**Mã API:** API-RPT-006  
**Mô tả:** Lấy báo cáo trạng thái thanh toán.

**Quyền truy cập:**  
`report:read:all`, `report:read:team`, `report:read:own`

**Tham số truy vấn:**
| Tên | Kiểu | Bắt buộc | Mặc định | Mô tả |
|-----|------|----------|----------|-------|
| customerId | Integer | Không | - | ID khách hàng |
| salesId | Integer | Không | - | ID nhân viên sales |
| contractId | Integer | Không | - | ID hợp đồng |
| status | String | Không | - | Trạng thái thanh toán |
| fromDate | Date | Không | 6 tháng trước | Ngày bắt đầu kỳ hạn |
| toDate | Date | Không | 6 tháng sau | Ngày kết thúc kỳ hạn |
| paidFromDate | Date | Không | - | Ngày thanh toán bắt đầu |
| paidToDate | Date | Không | - | Ngày thanh toán kết thúc |
| minAmount | Double | Không | - | Số tiền tối thiểu |
| maxAmount | Double | Không | - | Số tiền tối đa |
| includeDetails | Boolean | Không | true | Bao gồm chi tiết |
| exportType | String | Không | json | Định dạng xuất |
| page | Integer | Không | 1 | Số trang |
| size | Integer | Không | 50 | Số bản ghi mỗi trang |
| sortBy | String | Không | dueDate | Trường để sắp xếp |
| sortDir | String | Không | asc | Hướng sắp xếp |

**Phản hồi:**
```json
{
  "success": true,
  "data": {
    "summary": {
      "totalPayments": 95,
      "totalAmount": 150000000000,
      "paidAmount": 100000000000,
      "pendingAmount": 30000000000,
      "overdueAmount": 20000000000,
      "byStatus": {
        "paid": 60,
        "pending": 20,
        "overdue": 15
      }
    },
    "content": [
      {
        "id": 1,
        "contractId": 5,
        "contractCode": "CTR-2023-0005",
        "customerName": "ABC Bank",
        "amount": 500000000,
        "dueDate": "2023-07-15",
        "status": "pending",
        "invoiceNumber": "INV-2023-0010",
        "invoiceDate": "2023-06-30",
        "paymentMethod": null,
        "paidDate": null,
        "paidAmount": null
      }
    ],
    "pageable": {
      "pageNumber": 1,
      "pageSize": 50,
      "totalPages": 2,
      "totalElements": 95,
      "sort": "dueDate,asc"
    }
  },
  "message": null
}
```

### 2.6. GET /api/v1/reports/kpi-progress
**Mã API:** API-RPT-007  
**Mô tả:** Lấy báo cáo tiến độ KPI của nhân viên sales.

**Quyền truy cập:**  
`report:read:all`, `report:read:own`

**Tham số truy vấn:**
| Tên | Kiểu | Bắt buộc | Mặc định | Mô tả |
|-----|------|----------|----------|-------|
| salesId | Integer | Không | - | ID nhân viên sales |
| year | Integer | Không | Năm hiện tại | Năm báo cáo |
| quarter | Integer | Không | - | Quý (1-4) |
| month | Integer | Không | - | Tháng (1-12) |
| minAchievement | Double | Không | - | Tỉ lệ đạt được tối thiểu |
| maxAchievement | Double | Không | - | Tỉ lệ đạt được tối đa |
| includeDetails | Boolean | Không | true | Bao gồm chi tiết |
| exportType | String | Không | json | Định dạng xuất |
| page | Integer | Không | 1 | Số trang |
| size | Integer | Không | 20 | Số bản ghi mỗi trang |
| sortBy | String | Không | achievementPercentage | Trường để sắp xếp |
| sortDir | String | Không | desc | Hướng sắp xếp |

**Phản hồi:**
```json
{
  "success": true,
  "data": {
    "summary": {
      "totalSalesStaff": 25,
      "averageAchievement": 87.5,
      "achievementDistribution": {
        "overAchieved": 10,
        "achieved": 12,
        "underAchieved": 3
      }
    },
    "content": [
      {
        "salesId": 10,
        "salesName": "Lê Văn D",
        "targetRevenue": 5000000000,
        "actualRevenue": 6250000000,
        "achievementRate": 125.0,
        "period": "2023-Q2",
        "details": [
          {"month": "2023-04", "target": 1500000000, "actual": 1800000000, "rate": 120.0},
          {"month": "2023-05", "target": 1700000000, "actual": 2200000000, "rate": 129.4},
          {"month": "2023-06", "target": 1800000000, "actual": 2250000000, "rate": 125.0}
        ]
      }
    ],
    "pageable": {
      "pageNumber": 1,
      "pageSize": 20,
      "totalPages": 2,
      "totalElements": 25,
      "sort": "achievementPercentage,desc"
    }
  },
  "message": null
}
```

## Ghi chú chung:

1. **Quyền truy cập:**
   - `read:all`: Quyền xem tất cả dữ liệu
   - `read:team`: Quyền xem dữ liệu trong team của người dùng
   - `read:own`: Quyền xem dữ liệu của chính người dùng

2. **Xuất báo cáo:**
   - Tất cả API báo cáo hỗ trợ xuất dữ liệu theo định dạng JSON, CSV và Excel
   - Khi exportType là "csv" hoặc "excel", API sẽ trả về file download thay vì JSON

3. **Phân trang:**
   - Các API báo cáo hỗ trợ phân trang với tham số page (từ 1) và size
   - Phản hồi bao gồm thông tin phân trang trong đối tượng "pageable"

4. **Sắp xếp:**
   - Tất cả API báo cáo hỗ trợ sắp xếp với tham số sortBy và sortDir
   - sortDir có thể là "asc" (tăng dần) hoặc "desc" (giảm dần)

5. **Lọc dữ liệu:**
   - Các API hỗ trợ nhiều tham số lọc khác nhau tùy theo loại báo cáo
   - Khi không cung cấp tham số lọc, API sẽ sử dụng giá trị mặc định 