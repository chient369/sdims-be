package com.company.internalmgmt.modules.contract.dto.mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.company.internalmgmt.modules.admin.dto.UserBasicDTO;
import com.company.internalmgmt.modules.admin.model.User;
import com.company.internalmgmt.modules.contract.dto.ContractDTO;
import com.company.internalmgmt.modules.contract.dto.ContractEmployeeDTO;
import com.company.internalmgmt.modules.contract.dto.ContractFileDTO;
import com.company.internalmgmt.modules.contract.dto.ContractPaymentTermDTO;
import com.company.internalmgmt.modules.contract.dto.PaymentStatusSummaryDTO;
import com.company.internalmgmt.modules.contract.model.Contract;
import com.company.internalmgmt.modules.contract.model.ContractEmployee;
import com.company.internalmgmt.modules.contract.model.ContractFile;
import com.company.internalmgmt.modules.contract.model.ContractPaymentTerm;
import com.company.internalmgmt.modules.contract.model.enums.PaymentStatus;
import com.company.internalmgmt.modules.hrm.dto.EmployeeBasicDTO;
import com.company.internalmgmt.modules.hrm.model.Employee;
import com.company.internalmgmt.modules.opportunity.dto.OpportunityBasicDTO;
import com.company.internalmgmt.modules.opportunity.model.Opportunity;

/**
 * Mapper for converting between Contract entities and DTOs
 */
public class ContractMapper {

    /**
     * Convert Contract entity to ContractDTO
     * 
     * @param contract the contract entity
     * @return the contract DTO
     */
    public static ContractDTO toDto(Contract contract) {
        if (contract == null) {
            return null;
        }
        
        return ContractDTO.builder()
                .id(contract.getId())
                .contractCode(contract.getContractCode())
                .name(contract.getName())
                .customerName(contract.getClientName())
                .contractType(contract.getContractType())
                .amount(contract.getTotalValue())
                .currency(contract.getCurrency())
                .signDate(contract.getSignDate())
                .startDate(contract.getEffectiveDate())
                .endDate(contract.getExpiryDate())
                .status(contract.getStatus())
                .salesPerson(toUserBasicDto(contract.getAssignedSales()))
                .relatedOpportunity(toOpportunityBasicDto(contract.getOpportunity()))
                .description(contract.getDescription())
                .employeeCount(contract.getContractEmployees() != null ? contract.getContractEmployees().size() : 0)
                .createdBy(toUserBasicDto(contract.getCreatedByUser()))
                .createdAt(contract.getCreatedAt())
                .updatedBy(toUserBasicDto(contract.getUpdatedByUser()))
                .updatedAt(contract.getUpdatedAt())
                .build();
    }
    
    /**
     * Convert Contract entity to ContractDTO with payment status
     * 
     * @param contract the contract entity
     * @param paymentTerms the payment terms
     * @return the contract DTO with payment status
     */
    public static ContractDTO toDtoWithPaymentStatus(Contract contract, List<ContractPaymentTerm> paymentTerms) {
        ContractDTO dto = toDto(contract);
        if (dto == null) {
            return null;
        }
        
        dto.setPaymentStatus(calculatePaymentStatus(paymentTerms, contract.getTotalValue()));
        return dto;
    }
    
    /**
     * Convert Contract entity to ContractDTO with all details
     * 
     * @param contract the contract entity
     * @param includePaymentTerms whether to include payment terms
     * @param includeEmployees whether to include employees
     * @param includeFiles whether to include files
     * @return the detailed contract DTO
     */
    public static ContractDTO toDtoWithDetails(
            Contract contract, 
            boolean includePaymentTerms, 
            boolean includeEmployees, 
            boolean includeFiles) {
        
        ContractDTO dto = toDtoWithPaymentStatus(contract, contract.getPaymentTerms());
        if (dto == null) {
            return null;
        }
        
        return dto;
    }
    
    /**
     * Convert ContractPaymentTerm entity to ContractPaymentTermDTO
     * 
     * @param paymentTerm the payment term entity
     * @return the payment term DTO
     */
    public static ContractPaymentTermDTO toPaymentTermDto(ContractPaymentTerm paymentTerm) {
        if (paymentTerm == null) {
            return null;
        }
        
        return ContractPaymentTermDTO.builder()
                .id(paymentTerm.getId())
                .termNumber(paymentTerm.getTermNumber())
                .dueDate(paymentTerm.getExpectedPaymentDate())
                .amount(paymentTerm.getExpectedAmount())
                .percentage(calculatePercentage(paymentTerm.getExpectedAmount(), paymentTerm.getContract().getTotalValue()))
                .description(paymentTerm.getDescription())
                .status(paymentTerm.getPaymentStatus())
                .paidDate(paymentTerm.getActualPaymentDate())
                .paidAmount(paymentTerm.getActualAmountPaid())
                .notes(paymentTerm.getNotes())
                .build();
    }
    
    /**
     * Convert ContractEmployee entity to ContractEmployeeDTO
     * 
     * @param contractEmployee the contract employee entity
     * @return the contract employee DTO
     */
    public static ContractEmployeeDTO toContractEmployeeDto(ContractEmployee contractEmployee) {
        if (contractEmployee == null) {
            return null;
        }
        
        return ContractEmployeeDTO.builder()
                .id(contractEmployee.getId())
                .employee(toEmployeeBasicDto(contractEmployee.getEmployee()))
                .startDate(contractEmployee.getStartDate())
                .endDate(contractEmployee.getEndDate())
                .allocationPercentage(contractEmployee.getAllocationPercentage())
                .billRate(contractEmployee.getBillRate())
                .role(contractEmployee.getRole())
                .build();
    }
    
    /**
     * Convert ContractFile entity to ContractFileDTO
     * 
     * @param contractFile the contract file entity
     * @return the contract file DTO
     */
    public static ContractFileDTO toContractFileDto(ContractFile contractFile) {
        if (contractFile == null) {
            return null;
        }
        
        return ContractFileDTO.builder()
                .id(contractFile.getId())
                .name(contractFile.getFileName())
                .type(contractFile.getFileType())
                .size(contractFile.getFileSize())
                .uploadedAt(contractFile.getUploadedAt())
                .uploadedBy(toUserBasicDto(contractFile.getUploadedBy()))
                .url("/api/v1/contracts/files/" + contractFile.getId() + "/download")
                .description(contractFile.getDescription())
                .build();
    }
    
    /**
     * Convert User entity to UserBasicDTO
     * 
     * @param user the user entity
     * @return the user basic DTO
     */
    public static UserBasicDTO toUserBasicDto(User user) {
        if (user == null) {
            return null;
        }
        
        return UserBasicDTO.builder()
                .id(user.getId())
                .name(user.getFullName())
                .build();
    }
    
    /**
     * Convert Employee entity to EmployeeBasicDTO
     * 
     * @param employee the employee entity
     * @return the employee basic DTO
     */
    public static EmployeeBasicDTO toEmployeeBasicDto(Employee employee) {
        if (employee == null) {
            return null;
        }
        
        EmployeeBasicDTO.TeamBasicDTO teamDto = null;
        if (employee.getTeam() != null) {
            teamDto = EmployeeBasicDTO.TeamBasicDTO.builder()
                    .id(employee.getTeam().getId())
                    .name(employee.getTeam().getName())
                    .build();
        }
        
        return EmployeeBasicDTO.builder()
                .id(employee.getId())
                .name(employee.getFullName())
                .position(employee.getPosition())
                .team(teamDto)
                .build();
    }
    
    /**
     * Convert Opportunity entity to OpportunityBasicDTO
     * 
     * @param opportunity the opportunity entity
     * @return the opportunity basic DTO
     */
    public static OpportunityBasicDTO toOpportunityBasicDto(Opportunity opportunity) {
        if (opportunity == null) {
            return null;
        }
        
        return OpportunityBasicDTO.builder()
                .id(opportunity.getId())
                .code(opportunity.getCode())
                .name(opportunity.getName())
                .build();
    }
    
    /**
     * Calculate payment status summary from payment terms
     * 
     * @param paymentTerms the list of payment terms
     * @param totalAmount the total contract amount
     * @return the payment status summary DTO
     */
    public static PaymentStatusSummaryDTO calculatePaymentStatus(List<ContractPaymentTerm> paymentTerms, BigDecimal totalAmount) {
        if (paymentTerms == null || paymentTerms.isEmpty()) {
            return PaymentStatusSummaryDTO.builder()
                    .status(PaymentStatus.UNPAID.getValue())
                    .paidAmount(BigDecimal.ZERO)
                    .totalAmount(totalAmount)
                    .paidPercentage(BigDecimal.ZERO)
                    .nextDueDate(null)
                    .nextDueAmount(BigDecimal.ZERO)
                    .totalTerms(0)
                    .paidTerms(0)
                    .remainingAmount(totalAmount)
                    .build();
        }
        
        BigDecimal paidAmount = BigDecimal.ZERO;
        int paidTerms = 0;
        LocalDate nextDueDate = null;
        BigDecimal nextDueAmount = BigDecimal.ZERO;
        
        // Sort payment terms by due date
        List<ContractPaymentTerm> sortedTerms = paymentTerms.stream()
                .sorted((a, b) -> a.getExpectedPaymentDate().compareTo(b.getExpectedPaymentDate()))
                .collect(Collectors.toList());
        
        // Count paid terms and calculate paid amount
        for (ContractPaymentTerm term : sortedTerms) {
            if ("paid".equalsIgnoreCase(term.getPaymentStatus())) {
                paidAmount = paidAmount.add(term.getActualAmountPaid() != null ? term.getActualAmountPaid() : BigDecimal.ZERO);
                paidTerms++;
            } else {
                // Find next due payment
                if (nextDueDate == null || term.getExpectedPaymentDate().isBefore(nextDueDate)) {
                    nextDueDate = term.getExpectedPaymentDate();
                    nextDueAmount = term.getExpectedAmount();
                }
            }
        }
        
        // Calculate remaining amount
        BigDecimal remainingAmount = totalAmount.subtract(paidAmount);
        
        // Calculate paid percentage
        BigDecimal paidPercentage = BigDecimal.ZERO;
        if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
            paidPercentage = paidAmount.multiply(new BigDecimal("100")).divide(totalAmount, 2, RoundingMode.HALF_UP);
        }
        
        // Determine overall status
        String status;
        if (paidAmount.compareTo(BigDecimal.ZERO) == 0) {
            status = PaymentStatus.UNPAID.getValue();
        } else if (paidAmount.compareTo(totalAmount) < 0) {
            // Check for overdue payments
            boolean hasOverdue = sortedTerms.stream()
                    .anyMatch(term -> !term.getPaymentStatus().equalsIgnoreCase("paid") && 
                              term.getExpectedPaymentDate().isBefore(LocalDate.now()));
            
            status = hasOverdue ? PaymentStatus.OVERDUE.getValue() : PaymentStatus.PARTIAL.getValue();
        } else {
            status = PaymentStatus.PAID.getValue();
        }
        
        return PaymentStatusSummaryDTO.builder()
                .status(status)
                .paidAmount(paidAmount)
                .totalAmount(totalAmount)
                .paidPercentage(paidPercentage)
                .nextDueDate(nextDueDate)
                .nextDueAmount(nextDueAmount)
                .totalTerms(sortedTerms.size())
                .paidTerms(paidTerms)
                .remainingAmount(remainingAmount)
                .build();
    }
    
    /**
     * Calculate percentage of an amount relative to total
     * 
     * @param amount the amount
     * @param total the total amount
     * @return the percentage as BigDecimal
     */
    private static BigDecimal calculatePercentage(BigDecimal amount, BigDecimal total) {
        if (total == null || total.compareTo(BigDecimal.ZERO) == 0 || amount == null) {
            return BigDecimal.ZERO;
        }
        
        return amount.multiply(new BigDecimal("100")).divide(total, 2, RoundingMode.HALF_UP);
    }
} 