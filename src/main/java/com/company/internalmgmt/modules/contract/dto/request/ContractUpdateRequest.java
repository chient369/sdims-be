package com.company.internalmgmt.modules.contract.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for contract update request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractUpdateRequest {
    
    @Size(max = 255, message = "Tên hợp đồng không được vượt quá 255 ký tự")
    private String name;
    
    @Size(max = 50, message = "Mã hợp đồng không được vượt quá 50 ký tự")
    private String contractCode;
    
    @Size(max = 255, message = "Tên khách hàng không được vượt quá 255 ký tự")
    private String customerName;
    
    @Pattern(regexp = "FixedPrice|TimeAndMaterial|Retainer|Maintenance|Other", message = "Loại hợp đồng không hợp lệ")
    private String contractType;
    
    @DecimalMin(value = "0.01", message = "Giá trị hợp đồng phải lớn hơn 0")
    private BigDecimal amount;
    
    private LocalDate signDate;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    @Pattern(regexp = "Draft|InReview|Approved|Active|InProgress|OnHold|Completed|Terminated|Expired|Cancelled", 
            message = "Trạng thái hợp đồng không hợp lệ")
    private String status;
    
    private Long salesId;
    
    private Long opportunityId;
    
    @Size(max = 2000, message = "Mô tả không được vượt quá 2000 ký tự")
    private String description;
    
    @Valid
    private List<PaymentTermRequest> paymentTerms;
    
    @Valid
    private List<EmployeeAssignmentRequest> employeeAssignments;
    
    private List<String> tags;
    
    /**
     * DTO for payment term update request
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentTermRequest {
        private Long id;
        private Integer termNumber;
        private LocalDate dueDate;
        
        @DecimalMin(value = "0.01", message = "Số tiền phải lớn hơn 0")
        private BigDecimal amount;
        
        private BigDecimal percentage;
        private String description;
        
        @Pattern(regexp = "unpaid|partial|paid|overdue|cancelled", message = "Trạng thái thanh toán không hợp lệ")
        private String status;
        
        private LocalDate paidDate;
        private BigDecimal paidAmount;
        private String notes;
    }
    
    /**
     * DTO for employee assignment update request
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmployeeAssignmentRequest {
        private Long id;
        private Long employeeId;
        private LocalDate startDate;
        private LocalDate endDate;
        
        @DecimalMin(value = "0.01", message = "Phần trăm phân bổ phải lớn hơn 0")
        private BigDecimal allocationPercentage;
        
        private BigDecimal billRate;
        private String role;
    }
} 