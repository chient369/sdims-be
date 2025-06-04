package com.company.internalmgmt.modules.contract.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.company.internalmgmt.common.exception.BadRequestException;
import com.company.internalmgmt.common.exception.ResourceNotFoundException;
import com.company.internalmgmt.modules.admin.model.User;
import com.company.internalmgmt.modules.admin.repository.UserRepository;
import com.company.internalmgmt.modules.contract.dto.ContractPaymentTermDTO;
import com.company.internalmgmt.modules.contract.dto.mapper.ContractMapper;
import com.company.internalmgmt.modules.contract.model.ContractPaymentTerm;
import com.company.internalmgmt.modules.contract.model.enums.PaymentStatus;
import com.company.internalmgmt.modules.contract.repository.ContractPaymentTermRepository;
import com.company.internalmgmt.modules.contract.service.ContractPaymentTermService;

/**
 * Implementation of the ContractPaymentTermService interface
 */
@Service
public class ContractPaymentTermServiceImpl implements ContractPaymentTermService {

    @Autowired
    private ContractPaymentTermRepository paymentTermRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<ContractPaymentTermDTO> getPaymentTermsByContractId(Long contractId) {
        List<ContractPaymentTerm> paymentTerms = paymentTermRepository.findByContractIdOrderByTermNumberAsc(contractId);
        
        return paymentTerms.stream()
                .map(ContractMapper::toPaymentTermDto)
                .collect(Collectors.toList());
    }

    @Override
    public ContractPaymentTermDTO getPaymentTermById(Long id) {
        ContractPaymentTerm paymentTerm = paymentTermRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment term not found with id: " + id));
        
        return ContractMapper.toPaymentTermDto(paymentTerm);
    }

    @Override
    @Transactional
    public ContractPaymentTermDTO updatePaymentTermStatus(Long id, String status, LocalDate paidDate, Double paidAmount, String notes, Long currentUserId) {
        // Validate user
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUserId));
        
        // Get existing payment term
        ContractPaymentTerm paymentTerm = paymentTermRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment term not found with id: " + id));
        
        // Validate status
        try {
            PaymentStatus.fromValue(status);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid payment status: " + status);
        }
        
        // Update payment term
        paymentTerm.setPaymentStatus(status);
        
        if (PaymentStatus.PAID.getValue().equalsIgnoreCase(status)) {
            if (paidDate == null) {
                paymentTerm.setActualPaymentDate(LocalDate.now());
            } else {
                paymentTerm.setActualPaymentDate(paidDate);
            }
            
            if (paidAmount != null) {
                paymentTerm.setActualAmountPaid(BigDecimal.valueOf(paidAmount));
            } else {
                // If no paid amount is provided, use the expected amount
                paymentTerm.setActualAmountPaid(paymentTerm.getExpectedAmount());
            }
        } else if (paidDate != null) {
            paymentTerm.setActualPaymentDate(paidDate);
        }
        
        if (paidAmount != null) {
            paymentTerm.setActualAmountPaid(BigDecimal.valueOf(paidAmount));
        }
        
        if (StringUtils.hasText(notes)) {
            paymentTerm.setNotes(notes);
        }
        
        paymentTerm.setUpdatedByUser(currentUser);
        
        paymentTermRepository.save(paymentTerm);
        
        return ContractMapper.toPaymentTermDto(paymentTerm);
    }

    @Override
    public List<ContractPaymentTermDTO> findOverduePaymentTerms() {
        LocalDate today = LocalDate.now();
        
        List<ContractPaymentTerm> overdueTerms = paymentTermRepository
                .findByExpectedPaymentDateBeforeAndPaymentStatusNot(today, PaymentStatus.PAID.getValue());
        
        return overdueTerms.stream()
                .map(ContractMapper::toPaymentTermDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ContractPaymentTermDTO> findPaymentTermsDueSoon(int days) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);
        
        List<ContractPaymentTerm> upcomingTerms = paymentTermRepository
                .findByExpectedPaymentDateBetweenAndPaymentStatusNot(today, endDate, PaymentStatus.PAID.getValue());
        
        return upcomingTerms.stream()
                .map(ContractMapper::toPaymentTermDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ContractPaymentTermDTO> findPaymentTermsByStatus(String status) {
        // Validate status
        try {
            PaymentStatus.fromValue(status);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid payment status: " + status);
        }
        
        List<ContractPaymentTerm> paymentTerms = paymentTermRepository.findByPaymentStatus(status);
        
        return paymentTerms.stream()
                .map(ContractMapper::toPaymentTermDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ContractPaymentTermDTO> findPaymentTermsByContractIds(List<Long> contractIds) {
        // Implementation to find payment terms by contract IDs
        List<ContractPaymentTerm> paymentTerms = paymentTermRepository.findByContractIdIn(contractIds);
        
        return paymentTerms.stream()
                .map(ContractMapper::toPaymentTermDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ContractPaymentTermDTO> findOverduePaymentTermsPaged(Pageable pageable) {
        LocalDate today = LocalDate.now();
        
        // Get overdue payment terms with pagination
        Page<ContractPaymentTerm> overdueTermsPage = paymentTermRepository
                .findByExpectedPaymentDateBeforeAndPaymentStatusNot(today, PaymentStatus.PAID.getValue(), pageable);
        
        // Convert to DTOs
        return overdueTermsPage.map(ContractMapper::toPaymentTermDto);
    }

    @Override
    public Page<ContractPaymentTermDTO> findPaymentTermsDueSoonPaged(int days, Pageable pageable) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);
        
        // Get upcoming payment terms with pagination
        Page<ContractPaymentTerm> upcomingTermsPage = paymentTermRepository
                .findByExpectedPaymentDateBetweenAndPaymentStatusNot(today, endDate, PaymentStatus.PAID.getValue(), pageable);
        
        // Convert to DTOs
        return upcomingTermsPage.map(ContractMapper::toPaymentTermDto);
    }

    @Override
    public Page<ContractPaymentTermDTO> findPaymentTermsByStatusPaged(String status, Pageable pageable) {
        // Validate status
        try {
            PaymentStatus.fromValue(status);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid payment status: " + status);
        }
        
        // Get payment terms by status with pagination
        Page<ContractPaymentTerm> paymentTermsPage = paymentTermRepository.findByPaymentStatus(status, pageable);
        
        // Convert to DTOs
        return paymentTermsPage.map(ContractMapper::toPaymentTermDto);
    }
} 