package com.company.internalmgmt.modules.contract.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for contract creation request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractCreateRequest {
    
    @NotBlank(message = "Tên hợp đồng không được để trống")
    @Size(max = 255, message = "Tên hợp đồng không được vượt quá 255 ký tự")
    private String name;
    
    private String contractCode;
    
    @NotBlank(message = "Tên khách hàng không được để trống")
    @Size(max = 255, message = "Tên khách hàng không được vượt quá 255 ký tự")
    private String customerName;
    
    @Pattern(regexp = "FixedPrice|TimeAndMaterial|Retainer|Maintenance|Other", message = "Loại hợp đồng không hợp lệ")
    private String contractType;
    
    @NotNull(message = "Giá trị hợp đồng không được để trống")
    @DecimalMin(value = "0.01", message = "Giá trị hợp đồng phải lớn hơn 0")
    private BigDecimal amount;
    
    private LocalDate signDate;
    
    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate startDate;
    
    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDate endDate;
    
    @Pattern(regexp = "Draft|InReview|Approved|Active|InProgress|OnHold|Completed|Terminated|Expired|Cancelled", 
            message = "Trạng thái hợp đồng không hợp lệ")
    private String status;
    
    @NotNull(message = "ID người phụ trách Sales không được để trống")
    private Long salesId;
    
    private Long opportunityId;
    
    @Size(max = 2000, message = "Mô tả không được vượt quá 2000 ký tự")
    private String description;
    
    @NotEmpty(message = "Phải có ít nhất một điều khoản thanh toán")
    @Valid
    private List<PaymentTermRequest> paymentTerms;
    
    @Valid
    private List<EmployeeAssignmentRequest> employeeAssignments;
    
    @Valid
    private List<AttachmentRequest> attachments;
    
    private List<String> tags;
    
    /**
     * DTO for payment term request
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentTermRequest {
        @NotNull(message = "Số thứ tự không được để trống")
        private Integer termNumber;
        
        @NotNull(message = "Ngày đến hạn không được để trống")
        private LocalDate dueDate;
        
        @NotNull(message = "Số tiền không được để trống")
        @DecimalMin(value = "0.01", message = "Số tiền phải lớn hơn 0")
        private BigDecimal amount;
        
        @NotNull(message = "Phần trăm không được để trống")
        private BigDecimal percentage;
        
        private String description;
    }
    
    /**
     * DTO for employee assignment request
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmployeeAssignmentRequest {
        @NotNull(message = "ID nhân viên không được để trống")
        private Long employeeId;
        
        @NotNull(message = "Ngày bắt đầu không được để trống")
        private LocalDate startDate;
        
        @NotNull(message = "Ngày kết thúc không được để trống")
        private LocalDate endDate;
        
        @NotNull(message = "Phần trăm phân bổ không được để trống")
        @DecimalMin(value = "0.01", message = "Phần trăm phân bổ phải lớn hơn 0")
        private BigDecimal allocationPercentage;
        
        private BigDecimal billRate;
        
        private String role;
    }
    
    /**
     * DTO for attachment request
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AttachmentRequest {
        @NotBlank(message = "Tên file không được để trống")
        private String name;
        
        @NotBlank(message = "Loại file không được để trống")
        private String type;
        
        @NotBlank(message = "Nội dung file không được để trống")
        private String base64Content;
        
        private String description;
    }
} 