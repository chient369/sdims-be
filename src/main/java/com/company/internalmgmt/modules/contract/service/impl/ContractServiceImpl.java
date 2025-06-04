package com.company.internalmgmt.modules.contract.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.company.internalmgmt.common.exception.BadRequestException;
import com.company.internalmgmt.common.exception.ResourceNotFoundException;
import com.company.internalmgmt.modules.admin.model.User;
import com.company.internalmgmt.modules.admin.repository.UserRepository;
import com.company.internalmgmt.modules.contract.dto.ContractDTO;
import com.company.internalmgmt.modules.contract.dto.mapper.ContractMapper;
import com.company.internalmgmt.modules.contract.dto.request.ContractCreateRequest;
import com.company.internalmgmt.modules.contract.dto.request.ContractUpdateRequest;
import com.company.internalmgmt.modules.contract.model.Contract;
import com.company.internalmgmt.modules.contract.model.ContractEmployee;
import com.company.internalmgmt.modules.contract.model.ContractPaymentTerm;
import com.company.internalmgmt.modules.contract.model.enums.ContractStatus;
import com.company.internalmgmt.modules.contract.model.enums.ContractType;
import com.company.internalmgmt.modules.contract.model.enums.PaymentStatus;
import com.company.internalmgmt.modules.contract.repository.ContractEmployeeRepository;
import com.company.internalmgmt.modules.contract.repository.ContractFileRepository;
import com.company.internalmgmt.modules.contract.repository.ContractPaymentTermRepository;
import com.company.internalmgmt.modules.contract.repository.ContractRepository;
import com.company.internalmgmt.modules.contract.repository.specification.ContractSpecification;
import com.company.internalmgmt.modules.contract.service.ContractService;
import com.company.internalmgmt.modules.hrm.model.Employee;
import com.company.internalmgmt.modules.hrm.repository.EmployeeRepository;
import com.company.internalmgmt.modules.opportunity.model.Opportunity;
import com.company.internalmgmt.modules.opportunity.repository.OpportunityRepository;

/**
 * Implementation of the ContractService interface
 */
@Service
public class ContractServiceImpl implements ContractService {

    @Autowired
    private ContractRepository contractRepository;
    
    @Autowired
    private ContractPaymentTermRepository paymentTermRepository;
    
    @Autowired
    private ContractEmployeeRepository contractEmployeeRepository;
    
    @Autowired
    private ContractFileRepository contractFileRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private OpportunityRepository opportunityRepository;

    @Override
    public ContractDTO getContractById(Long id, Boolean includePaymentTerms, Boolean includeEmployees, Boolean includeFiles) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + id));
        
        return ContractMapper.toDtoWithDetails(contract, 
                includePaymentTerms != null && includePaymentTerms, 
                includeEmployees != null && includeEmployees, 
                includeFiles != null && includeFiles);
    }

    @Override
    public ContractDTO getContractByCode(String contractCode) {
        Contract contract = contractRepository.findByContractCode(contractCode)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with code: " + contractCode));
        
        return ContractMapper.toDtoWithPaymentStatus(contract, contract.getPaymentTerms());
    }

    @Override
    public Page<ContractDTO> searchContracts(String customerName, String contractCode, String status, String contractType,
            Long salesId, Double minAmount, Double maxAmount, LocalDate fromDate, LocalDate toDate, String paymentStatus,
            Pageable pageable) {
        
        Specification<Contract> spec = ContractSpecification.searchContracts(
                customerName, 
                contractCode, 
                status, 
                contractType, 
                salesId,
                minAmount != null ? BigDecimal.valueOf(minAmount) : null,
                maxAmount != null ? BigDecimal.valueOf(maxAmount) : null,
                fromDate,
                toDate,
                paymentStatus);
        
        return contractRepository.findAll(spec, pageable)
                .map(contract -> ContractMapper.toDtoWithPaymentStatus(contract, contract.getPaymentTerms()));
    }

    @Override
    @Transactional
    public ContractDTO createContract(ContractCreateRequest request, Long currentUserId) {
        // Validate user
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUserId));
        
        // Validate sales person
        User salesPerson = userRepository.findById(request.getSalesId())
                .orElseThrow(() -> new ResourceNotFoundException("Sales person not found with id: " + request.getSalesId()));
        
        // Validate contract code uniqueness if provided
        if (StringUtils.hasText(request.getContractCode())) {
            if (contractRepository.findByContractCode(request.getContractCode()).isPresent()) {
                throw new BadRequestException("Contract code already exists: " + request.getContractCode());
            }
        }
        
        // Validate opportunity if provided
        Opportunity opportunity = null;
        if (request.getOpportunityId() != null) {
            opportunity = opportunityRepository.findById(request.getOpportunityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with id: " + request.getOpportunityId()));
        }
        
        // Validate payment terms total amount
        BigDecimal totalTermsAmount = request.getPaymentTerms().stream()
                .map(term -> term.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalTermsAmount.compareTo(request.getAmount()) != 0) {
            throw new BadRequestException("Sum of payment terms amount (" + totalTermsAmount + 
                    ") does not match contract total amount (" + request.getAmount() + ")");
        }
        
        // Create contract
        Contract contract = new Contract();
        contract.setContractCode(StringUtils.hasText(request.getContractCode()) ? 
                request.getContractCode() : generateContractCode());
        contract.setName(request.getName());
        contract.setClientName(request.getCustomerName());
        contract.setTotalValue(request.getAmount());
        contract.setCurrency("VND"); // Default currency
        contract.setContractType(request.getContractType());
        contract.setSignDate(request.getSignDate());
        contract.setEffectiveDate(request.getStartDate());
        contract.setExpiryDate(request.getEndDate());
        contract.setStatus(request.getStatus());
        contract.setDescription(request.getDescription());
        contract.setAssignedSales(salesPerson);
        contract.setOpportunity(opportunity);
        contract.setCreatedByUser(currentUser);
        contract.setUpdatedByUser(currentUser);
        
        contract = contractRepository.save(contract);
        
        // Create payment terms
        List<ContractPaymentTerm> paymentTerms = new ArrayList<>();
        for (ContractCreateRequest.PaymentTermRequest termRequest : request.getPaymentTerms()) {
            ContractPaymentTerm term = new ContractPaymentTerm();
            term.setContract(contract);
            term.setTermNumber(termRequest.getTermNumber());
            term.setDescription(termRequest.getDescription());
            term.setExpectedPaymentDate(termRequest.getDueDate());
            term.setExpectedAmount(termRequest.getAmount());
            term.setCurrency("VND"); // Default currency
            term.setPaymentStatus(PaymentStatus.UNPAID.getValue());
            term.setCreatedByUser(currentUser);
            term.setUpdatedByUser(currentUser);
            
            paymentTerms.add(term);
        }
        
        paymentTermRepository.saveAll(paymentTerms);
        
        // Create employee assignments if provided
        if (request.getEmployeeAssignments() != null && !request.getEmployeeAssignments().isEmpty()) {
            List<ContractEmployee> contractEmployees = new ArrayList<>();
            
            for (ContractCreateRequest.EmployeeAssignmentRequest assignment : request.getEmployeeAssignments()) {
                Employee employee = employeeRepository.findById(assignment.getEmployeeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + assignment.getEmployeeId()));
                
                ContractEmployee contractEmployee = new ContractEmployee();
                contractEmployee.setContract(contract);
                contractEmployee.setEmployee(employee);
                contractEmployee.setRole(assignment.getRole());
                contractEmployee.setStartDate(assignment.getStartDate());
                contractEmployee.setEndDate(assignment.getEndDate());
                contractEmployee.setAllocationPercentage(assignment.getAllocationPercentage());
                contractEmployee.setBillRate(assignment.getBillRate());
                
                contractEmployees.add(contractEmployee);
            }
            
            contractEmployeeRepository.saveAll(contractEmployees);
        }
        
        // Load the contract with all relationships for returning
        Contract savedContract = contractRepository.findById(contract.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found after saving"));
        
        return ContractMapper.toDtoWithDetails(savedContract, true, true, false);
    }

    @Override
    @Transactional
    public ContractDTO updateContract(Long id, ContractUpdateRequest request, Long currentUserId) {
        // Validate user
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUserId));
        
        // Get existing contract
        Contract existingContract = contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + id));
        
        // Validate contract code uniqueness if provided and changed
        if (StringUtils.hasText(request.getContractCode()) && 
                !request.getContractCode().equals(existingContract.getContractCode())) {
            if (contractRepository.findByContractCode(request.getContractCode()).isPresent()) {
                throw new BadRequestException("Contract code already exists: " + request.getContractCode());
            }
        }
        
        // Update fields if provided
        if (StringUtils.hasText(request.getName())) {
            existingContract.setName(request.getName());
        }
        
        if (StringUtils.hasText(request.getContractCode())) {
            existingContract.setContractCode(request.getContractCode());
        }
        
        if (StringUtils.hasText(request.getCustomerName())) {
            existingContract.setClientName(request.getCustomerName());
        }
        
        if (StringUtils.hasText(request.getContractType())) {
            existingContract.setContractType(request.getContractType());
        }
        
        if (request.getAmount() != null) {
            existingContract.setTotalValue(request.getAmount());
        }
        
        if (request.getSignDate() != null) {
            existingContract.setSignDate(request.getSignDate());
        }
        
        if (request.getStartDate() != null) {
            existingContract.setEffectiveDate(request.getStartDate());
        }
        
        if (request.getEndDate() != null) {
            existingContract.setExpiryDate(request.getEndDate());
        }
        
        if (StringUtils.hasText(request.getStatus())) {
            existingContract.setStatus(request.getStatus());
        }
        
        if (StringUtils.hasText(request.getDescription())) {
            existingContract.setDescription(request.getDescription());
        }
        
        // Update sales person if provided
        if (request.getSalesId() != null) {
            User salesPerson = userRepository.findById(request.getSalesId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sales person not found with id: " + request.getSalesId()));
            existingContract.setAssignedSales(salesPerson);
        }
        
        // Update opportunity if provided
        if (request.getOpportunityId() != null) {
            Opportunity opportunity = opportunityRepository.findById(request.getOpportunityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with id: " + request.getOpportunityId()));
            existingContract.setOpportunity(opportunity);
        }
        
        existingContract.setUpdatedByUser(currentUser);
        
        contractRepository.save(existingContract);
        
        // Update payment terms if provided
        if (request.getPaymentTerms() != null && !request.getPaymentTerms().isEmpty()) {
            // Process payment terms: update existing or create new ones
            for (ContractUpdateRequest.PaymentTermRequest termRequest : request.getPaymentTerms()) {
                if (termRequest.getId() != null) {
                    // Update existing term
                    ContractPaymentTerm existingTerm = paymentTermRepository.findById(termRequest.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Payment term not found with id: " + termRequest.getId()));
                    
                    if (existingTerm.getContract().getId() != id) {
                        throw new BadRequestException("Payment term with id " + termRequest.getId() + 
                                " does not belong to contract with id " + id);
                    }
                    
                    if (termRequest.getTermNumber() != null) {
                        existingTerm.setTermNumber(termRequest.getTermNumber());
                    }
                    
                    if (StringUtils.hasText(termRequest.getDescription())) {
                        existingTerm.setDescription(termRequest.getDescription());
                    }
                    
                    if (termRequest.getDueDate() != null) {
                        existingTerm.setExpectedPaymentDate(termRequest.getDueDate());
                    }
                    
                    if (termRequest.getAmount() != null) {
                        existingTerm.setExpectedAmount(termRequest.getAmount());
                    }
                    
                    if (StringUtils.hasText(termRequest.getStatus())) {
                        existingTerm.setPaymentStatus(termRequest.getStatus());
                    }
                    
                    if (termRequest.getPaidDate() != null) {
                        existingTerm.setActualPaymentDate(termRequest.getPaidDate());
                    }
                    
                    if (termRequest.getPaidAmount() != null) {
                        existingTerm.setActualAmountPaid(termRequest.getPaidAmount());
                    }
                    
                    if (StringUtils.hasText(termRequest.getNotes())) {
                        existingTerm.setNotes(termRequest.getNotes());
                    }
                    
                    existingTerm.setUpdatedByUser(currentUser);
                    paymentTermRepository.save(existingTerm);
                } else {
                    // Create new term
                    ContractPaymentTerm newTerm = new ContractPaymentTerm();
                    newTerm.setContract(existingContract);
                    newTerm.setTermNumber(termRequest.getTermNumber());
                    newTerm.setDescription(termRequest.getDescription());
                    newTerm.setExpectedPaymentDate(termRequest.getDueDate());
                    newTerm.setExpectedAmount(termRequest.getAmount());
                    newTerm.setCurrency("VND"); // Default currency
                    newTerm.setPaymentStatus(StringUtils.hasText(termRequest.getStatus()) ? 
                            termRequest.getStatus() : PaymentStatus.UNPAID.getValue());
                    newTerm.setActualPaymentDate(termRequest.getPaidDate());
                    newTerm.setActualAmountPaid(termRequest.getPaidAmount());
                    newTerm.setNotes(termRequest.getNotes());
                    newTerm.setCreatedByUser(currentUser);
                    newTerm.setUpdatedByUser(currentUser);
                    
                    paymentTermRepository.save(newTerm);
                }
            }
        }
        
        // Update employee assignments if provided
        if (request.getEmployeeAssignments() != null && !request.getEmployeeAssignments().isEmpty()) {
            for (ContractUpdateRequest.EmployeeAssignmentRequest assignment : request.getEmployeeAssignments()) {
                if (assignment.getId() != null) {
                    // Update existing assignment
                    ContractEmployee existingAssignment = contractEmployeeRepository.findById(assignment.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Contract employee assignment not found with id: " + assignment.getId()));
                    
                    if (existingAssignment.getContract().getId() != id) {
                        throw new BadRequestException("Contract employee assignment with id " + assignment.getId() + 
                                " does not belong to contract with id " + id);
                    }
                    
                    // Update employee if provided
                    if (assignment.getEmployeeId() != null && !assignment.getEmployeeId().equals(existingAssignment.getEmployee().getId())) {
                        Employee employee = employeeRepository.findById(assignment.getEmployeeId())
                                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + assignment.getEmployeeId()));
                        existingAssignment.setEmployee(employee);
                    }
                    
                    if (StringUtils.hasText(assignment.getRole())) {
                        existingAssignment.setRole(assignment.getRole());
                    }
                    
                    if (assignment.getStartDate() != null) {
                        existingAssignment.setStartDate(assignment.getStartDate());
                    }
                    
                    if (assignment.getEndDate() != null) {
                        existingAssignment.setEndDate(assignment.getEndDate());
                    }
                    
                    if (assignment.getAllocationPercentage() != null) {
                        existingAssignment.setAllocationPercentage(assignment.getAllocationPercentage());
                    }
                    
                    if (assignment.getBillRate() != null) {
                        existingAssignment.setBillRate(assignment.getBillRate());
                    }
                    
                    contractEmployeeRepository.save(existingAssignment);
                } else {
                    // Create new assignment
                    if (assignment.getEmployeeId() == null) {
                        throw new BadRequestException("Employee ID is required for new assignment");
                    }
                    
                    Employee employee = employeeRepository.findById(assignment.getEmployeeId())
                            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + assignment.getEmployeeId()));
                    
                    // Check if assignment already exists
                    Optional<ContractEmployee> existingAssignment = 
                            contractEmployeeRepository.findByContractIdAndEmployeeId(id, assignment.getEmployeeId());
                    
                    if (existingAssignment.isPresent()) {
                        throw new BadRequestException("Employee with id " + assignment.getEmployeeId() + 
                                " is already assigned to this contract");
                    }
                    
                    ContractEmployee newAssignment = new ContractEmployee();
                    newAssignment.setContract(existingContract);
                    newAssignment.setEmployee(employee);
                    newAssignment.setRole(assignment.getRole());
                    newAssignment.setStartDate(assignment.getStartDate());
                    newAssignment.setEndDate(assignment.getEndDate());
                    newAssignment.setAllocationPercentage(assignment.getAllocationPercentage());
                    newAssignment.setBillRate(assignment.getBillRate());
                    
                    contractEmployeeRepository.save(newAssignment);
                }
            }
        }
        
        // Load the updated contract with all relationships for returning
        Contract updatedContract = contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found after updating"));
        
        return ContractMapper.toDtoWithDetails(updatedContract, true, true, true);
    }

    @Override
    @Transactional
    public void deleteContract(Long id, Long currentUserId) {
        // Validate user
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUserId));
        
        // Get existing contract
        Contract existingContract = contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + id));
        
        // Soft delete by setting deletedAt field
        existingContract.setDeletedAt(LocalDateTime.now());
        existingContract.setUpdatedByUser(currentUser);
        
        contractRepository.save(existingContract);
    }

    @Override
    public Page<ContractDTO> getContractsByEmployeeId(Long employeeId, Pageable pageable) {
        // Validate employee exists
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }
        
        Specification<Contract> spec = ContractSpecification.findByEmployeeId(employeeId);
        
        return contractRepository.findAll(spec, pageable)
                .map(contract -> ContractMapper.toDtoWithPaymentStatus(contract, contract.getPaymentTerms()));
    }

    @Override
    public Page<ContractDTO> getContractsByTeamId(Long teamId, Pageable pageable) {
        Specification<Contract> spec = ContractSpecification.findByTeamId(teamId);
        
        return contractRepository.findAll(spec, pageable)
                .map(contract -> ContractMapper.toDtoWithPaymentStatus(contract, contract.getPaymentTerms()));
    }

    @Override
    public Page<ContractDTO> getContractsByOpportunityId(Long opportunityId, Pageable pageable) {
        // Validate opportunity exists
        if (!opportunityRepository.existsById(opportunityId)) {
            throw new ResourceNotFoundException("Opportunity not found with id: " + opportunityId);
        }
        
        Specification<Contract> spec = ContractSpecification.findByOpportunityId(opportunityId);
        
        return contractRepository.findAll(spec, pageable)
                .map(contract -> ContractMapper.toDtoWithPaymentStatus(contract, contract.getPaymentTerms()));
    }

    @Override
    public Page<ContractDTO> getContractsBySalesId(Long salesId, Pageable pageable) {
        // Validate sales person exists
        if (!userRepository.existsById(salesId)) {
            throw new ResourceNotFoundException("Sales person not found with id: " + salesId);
        }
        
        return contractRepository.findByAssignedSalesId(salesId, pageable)
                .map(contract -> ContractMapper.toDtoWithPaymentStatus(contract, contract.getPaymentTerms()));
    }
    
    /**
     * Generate a new contract code
     * 
     * @return the generated contract code
     */
    private String generateContractCode() {
        // Format: CTR-YYYYMM-XXX where XXX is a sequential number
        String prefix = "CTR-" + LocalDate.now().getYear() + String.format("%02d", LocalDate.now().getMonthValue()) + "-";
        
        // Find the highest existing code with this prefix
        List<Contract> contracts = contractRepository.findAll();
        int maxNumber = 0;
        
        for (Contract contract : contracts) {
            String code = contract.getContractCode();
            if (code != null && code.startsWith(prefix)) {
                try {
                    int number = Integer.parseInt(code.substring(prefix.length()));
                    if (number > maxNumber) {
                        maxNumber = number;
                    }
                } catch (NumberFormatException e) {
                    // Ignore if not a number
                }
            }
        }
        
        // Format the new code with padding
        return prefix + String.format("%03d", maxNumber + 1);
    }
} 