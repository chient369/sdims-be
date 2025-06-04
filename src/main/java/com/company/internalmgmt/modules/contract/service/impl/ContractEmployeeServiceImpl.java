package com.company.internalmgmt.modules.contract.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.company.internalmgmt.common.exception.BadRequestException;
import com.company.internalmgmt.common.exception.ResourceNotFoundException;
import com.company.internalmgmt.modules.contract.dto.ContractEmployeeDTO;
import com.company.internalmgmt.modules.contract.dto.mapper.ContractMapper;
import com.company.internalmgmt.modules.contract.model.Contract;
import com.company.internalmgmt.modules.contract.model.ContractEmployee;
import com.company.internalmgmt.modules.contract.repository.ContractEmployeeRepository;
import com.company.internalmgmt.modules.contract.repository.ContractRepository;
import com.company.internalmgmt.modules.contract.service.ContractEmployeeService;
import com.company.internalmgmt.modules.hrm.model.Employee;
import com.company.internalmgmt.modules.hrm.repository.EmployeeRepository;

/**
 * Implementation of the ContractEmployeeService interface
 */
@Service
public class ContractEmployeeServiceImpl implements ContractEmployeeService {

    @Autowired
    private ContractEmployeeRepository contractEmployeeRepository;
    
    @Autowired
    private ContractRepository contractRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public List<ContractEmployeeDTO> getContractEmployeesByContractId(Long contractId) {
        List<ContractEmployee> contractEmployees = contractEmployeeRepository.findByContractId(contractId);
        
        return contractEmployees.stream()
                .map(ContractMapper::toContractEmployeeDto)
                .collect(Collectors.toList());
    }

    @Override
    public ContractEmployeeDTO getContractEmployeeById(Long id) {
        ContractEmployee contractEmployee = contractEmployeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract employee not found with id: " + id));
        
        return ContractMapper.toContractEmployeeDto(contractEmployee);
    }

    @Override
    public List<ContractEmployeeDTO> getContractsByEmployeeId(Long employeeId) {
        List<ContractEmployee> contractEmployees = contractEmployeeRepository.findByEmployeeId(employeeId);
        
        return contractEmployees.stream()
                .map(ContractMapper::toContractEmployeeDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ContractEmployeeDTO assignEmployeeToContract(Long contractId, Long employeeId, String role, 
            LocalDate startDate, LocalDate endDate, BigDecimal allocationPercentage, BigDecimal billRate, Long currentUserId) {
        
        // Validate contract
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + contractId));
        
        // Validate employee
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        
        // Check if assignment already exists
        Optional<ContractEmployee> existingAssignment = 
                contractEmployeeRepository.findByContractIdAndEmployeeId(contractId, employeeId);
        
        if (existingAssignment.isPresent()) {
            throw new BadRequestException("Employee with id " + employeeId + 
                    " is already assigned to contract with id " + contractId);
        }
        
        // Validate dates
        if (endDate != null && startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date cannot be after end date");
        }
        
        if (allocationPercentage != null && (allocationPercentage.compareTo(BigDecimal.ZERO) <= 0 || 
                allocationPercentage.compareTo(new BigDecimal("100")) > 0)) {
            throw new BadRequestException("Allocation percentage must be between 0 and 100");
        }
        
        // Create new assignment
        ContractEmployee contractEmployee = new ContractEmployee();
        contractEmployee.setContract(contract);
        contractEmployee.setEmployee(employee);
        contractEmployee.setRole(role);
        contractEmployee.setStartDate(startDate);
        contractEmployee.setEndDate(endDate);
        contractEmployee.setAllocationPercentage(allocationPercentage != null ? allocationPercentage : new BigDecimal("100"));
        contractEmployee.setBillRate(billRate);
        
        contractEmployeeRepository.save(contractEmployee);
        
        return ContractMapper.toContractEmployeeDto(contractEmployee);
    }

    @Override
    @Transactional
    public ContractEmployeeDTO updateContractEmployeeAssignment(Long id, String role, LocalDate startDate, 
            LocalDate endDate, BigDecimal allocationPercentage, BigDecimal billRate, Long currentUserId) {
        
        // Get existing assignment
        ContractEmployee contractEmployee = contractEmployeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract employee assignment not found with id: " + id));
        
        // Update fields if provided
        if (StringUtils.hasText(role)) {
            contractEmployee.setRole(role);
        }
        
        if (startDate != null) {
            contractEmployee.setStartDate(startDate);
        }
        
        if (endDate != null) {
            if (contractEmployee.getStartDate().isAfter(endDate)) {
                throw new BadRequestException("Start date cannot be after end date");
            }
            contractEmployee.setEndDate(endDate);
        }
        
        if (allocationPercentage != null) {
            if (allocationPercentage.compareTo(BigDecimal.ZERO) <= 0 || 
                    allocationPercentage.compareTo(new BigDecimal("100")) > 0) {
                throw new BadRequestException("Allocation percentage must be between 0 and 100");
            }
            contractEmployee.setAllocationPercentage(allocationPercentage);
        }
        
        if (billRate != null) {
            contractEmployee.setBillRate(billRate);
        }
        
        contractEmployeeRepository.save(contractEmployee);
        
        return ContractMapper.toContractEmployeeDto(contractEmployee);
    }

    @Override
    @Transactional
    public void removeEmployeeFromContract(Long id, Long currentUserId) {
        // Check if assignment exists
        if (!contractEmployeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Contract employee assignment not found with id: " + id);
        }
        
        // Delete assignment
        contractEmployeeRepository.deleteById(id);
    }

    @Override
    public List<ContractEmployeeDTO> findActiveContractEmployeesForDate(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        
        List<ContractEmployee> activeAssignments = contractEmployeeRepository.findActiveByDate(date);
        
        return activeAssignments.stream()
                .map(ContractMapper::toContractEmployeeDto)
                .collect(Collectors.toList());
    }

    /**
     * Method to find active contract assignments for an employee on a specific date
     * 
     * @param employeeId the employee ID
     * @param date the date
     * @return list of active contract employee DTOs
     */
    @Override
    public List<ContractEmployeeDTO> findActiveContractEmployeesForEmployeeAndDate(Long employeeId, LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        
        List<ContractEmployee> activeAssignments = contractEmployeeRepository.findActiveByEmployeeIdAndDate(employeeId, date);
        
        return activeAssignments.stream()
                .map(ContractMapper::toContractEmployeeDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ContractEmployeeDTO> findActiveContractEmployeesForTeamAndDate(Long teamId, LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        
        List<ContractEmployee> activeAssignments = contractEmployeeRepository.findActiveByTeamIdAndDate(teamId, date);
        
        return activeAssignments.stream()
                .map(ContractMapper::toContractEmployeeDto)
                .collect(Collectors.toList());
    }

    @Override
    public Long countActiveEmployeesForContractAndDate(Long contractId, LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        
        return contractEmployeeRepository.countActiveByContractIdAndDate(contractId, date);
    }

    @Override
    public List<ContractEmployeeDTO> getContractEmployeesByEmployeeId(Long employeeId) {
        List<ContractEmployee> contractEmployees = contractEmployeeRepository.findByEmployeeId(employeeId);
        
        return contractEmployees.stream()
                .map(ContractMapper::toContractEmployeeDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ContractEmployeeDTO> getContractsByEmployeeIdPaged(Long employeeId, Pageable pageable) {
        // Check if employee exists
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }
        
        // Get paged contract employees
        Page<ContractEmployee> contractEmployeesPage = contractEmployeeRepository.findByEmployeeId(employeeId, pageable);
        
        // Convert to DTOs
        return contractEmployeesPage.map(ContractMapper::toContractEmployeeDto);
    }
} 