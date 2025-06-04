package com.company.internalmgmt.modules.opportunity.service.impl;

import com.company.internalmgmt.common.dto.PageableInfo;
import com.company.internalmgmt.common.exception.ResourceNotFoundException;
import com.company.internalmgmt.modules.admin.model.User;
import com.company.internalmgmt.modules.hrm.model.Employee;
import com.company.internalmgmt.modules.hrm.service.EmployeeService;
import com.company.internalmgmt.modules.opportunity.dto.OpportunityDTO;
import com.company.internalmgmt.modules.opportunity.dto.request.AssignLeaderRequest;
import com.company.internalmgmt.modules.opportunity.dto.request.ListOpportunitiesRequest;
import com.company.internalmgmt.modules.opportunity.dto.request.SyncHubspotRequest;
import com.company.internalmgmt.modules.opportunity.dto.response.AssignLeaderResponse;
import com.company.internalmgmt.modules.opportunity.dto.response.ListOpportunitiesResponse;
import com.company.internalmgmt.modules.opportunity.dto.response.OpportunitySummaryDTO;
import com.company.internalmgmt.modules.opportunity.dto.response.SyncHubspotResponse;
import com.company.internalmgmt.modules.opportunity.dto.response.SyncLogResponse;
import com.company.internalmgmt.modules.opportunity.model.Opportunity;
import com.company.internalmgmt.modules.opportunity.model.OpportunityActivityLog;
import com.company.internalmgmt.modules.opportunity.model.OpportunityAssignment;
import com.company.internalmgmt.modules.opportunity.repository.OpportunityActivityLogRepository;
import com.company.internalmgmt.modules.opportunity.repository.OpportunityAssignmentRepository;
import com.company.internalmgmt.modules.opportunity.repository.OpportunityRepository;
import com.company.internalmgmt.modules.opportunity.repository.specification.OpportunitySpecification;
import com.company.internalmgmt.modules.opportunity.service.OpportunityService;
import com.company.internalmgmt.security.AuthorizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of the OpportunityService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OpportunityServiceImpl implements OpportunityService {

    private final OpportunityRepository opportunityRepository;
    private final OpportunityAssignmentRepository opportunityAssignmentRepository;
    private final OpportunityActivityLogRepository opportunityActivityLogRepository;
    private final EmployeeService employeeService;
    private final AuthorizationService authorizationService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ListOpportunitiesResponse getOpportunities(ListOpportunitiesRequest request) {
        // Create pageable
        Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDir()), request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), sort);
        
        // Build specifications
        Specification<Opportunity> spec = buildFilterSpecification(request);
        
        // Get page of opportunities
        Page<Opportunity> opportunityPage = opportunityRepository.findAll(spec, pageable);
        
        // Map to DTOs
        List<OpportunityDTO> opportunityDTOs = opportunityPage.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        
        // Create summary statistics
        OpportunitySummaryDTO summary = createSummary(opportunityRepository.findAll(spec));
        
        // Create pageable DTO
        PageableInfo pageableDTO = PageableInfo.builder()
                .pageNumber(opportunityPage.getNumber() + 1)
                .pageSize(opportunityPage.getSize())
                .totalPages(opportunityPage.getTotalPages())
                .totalElements(opportunityPage.getTotalElements())
                .sort(request.getSortBy() + "," + request.getSortDir())
                .build();
        
        // Build and return response
        return ListOpportunitiesResponse.builder()
                .summary(summary)
                .content(opportunityDTOs)
                .pageable(pageableDTO)
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public OpportunityDTO getOpportunityById(Long id) {
        return mapToDTO(getOpportunityEntityById(id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Opportunity getOpportunityEntityById(Long id) {
        Opportunity opportunity = opportunityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with id: " + id));
        
        // Check permission based on roles and ownership
        if (!authorizationService.canAccessOpportunity(opportunity)) {
            throw new ResourceNotFoundException("Opportunity not found with id: " + id);
        }
        
        return opportunity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public OpportunityDTO updateOnsitePriority(Long id, Boolean priority, String note) {
        Opportunity opportunity = getOpportunityEntityById(id);
        
        // Update priority
        Boolean oldPriority = opportunity.getPriority();
        opportunity.setPriority(priority);
        
        // Save changes
        Opportunity updated = opportunityRepository.save(opportunity);
        
        // Log activity about priority change
        logPriorityChange(updated, oldPriority, priority, note);
        
        return mapToDTO(updated);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public AssignLeaderResponse assignLeader(Long opportunityId, AssignLeaderRequest request) {
        // Get opportunity and employee
        Opportunity opportunity = getOpportunityEntityById(opportunityId);
        Employee employee = employeeService.findEmployeeById(request.getLeaderId());
        
        // Get previous leader, if any
        Employee previousLeader = null;
        if (opportunity.getAssignments() != null && !opportunity.getAssignments().isEmpty()) {
            // For simplicity, we'll consider the first assignment as the previous leader
            // In a real implementation, you'd need a more sophisticated way to determine this
            previousLeader = opportunity.getAssignments().get(0).getEmployee();
        }
        
        // Check if assignment already exists
        OpportunityAssignment existingAssignment = opportunityAssignmentRepository
            .findByOpportunityAndEmployee(opportunity, employee)
            .orElse(null);
        
        if (existingAssignment != null) {
            // If assignment already exists, construct response showing current state
            AssignLeaderResponse.OpportunityAssignmentDTO opportunityDTO = AssignLeaderResponse.OpportunityAssignmentDTO.builder()
                .id(opportunity.getId())
                .code(opportunity.getCode())
                .name(opportunity.getName())
                .assignedTo(mapEmployeeToDTO(employee))
                .previouslyAssignedTo(null) // Was already assigned
                .assignedAt(existingAssignment.getAssignedAt())
                .assignedBy(mapUserToSummaryDTO(authorizationService.getCurrentUser()))
                .build();
                
            AssignLeaderResponse.NotificationDTO notificationDTO = AssignLeaderResponse.NotificationDTO.builder()
                .leaderNotified(false)
                .salesNotified(false)
                .build();
                
            return AssignLeaderResponse.builder()
                .opportunity(opportunityDTO)
                .notification(notificationDTO)
                .activityLogged(false)
                .build();
        }
        
        // Create new assignment
        LocalDateTime now = LocalDateTime.now();
        OpportunityAssignment assignment = OpportunityAssignment.builder()
            .opportunity(opportunity)
            .employee(employee)
            .assignedAt(now)
            .build();
        
        opportunityAssignmentRepository.save(assignment);
        
        // Handle notifications
        boolean leaderNotified = false;
        boolean salesNotified = false;
        
        if (Boolean.TRUE.equals(request.getNotifyLeader())) {
            // sendLeaderAssignmentNotification(employee, opportunity);
            log.info("Notification would be sent to leader {} for opportunity {}", employee.getId(), opportunityId);
            leaderNotified = true;
        }
        
        if (Boolean.TRUE.equals(request.getNotifySales()) && opportunity.getCreatedBy() != null) {
            // sendSalesAssignmentNotification(opportunity.getCreatedBy(), opportunity, employee);
            log.info("Notification would be sent to sales {} for opportunity {}", opportunity.getCreatedBy().getId(), opportunityId);
            salesNotified = true;
        }
        
        // Log activity
        logAssignment(opportunity, previousLeader, employee, request.getNote());
        
        // Construct and return response
        AssignLeaderResponse.OpportunityAssignmentDTO opportunityDTO = AssignLeaderResponse.OpportunityAssignmentDTO.builder()
            .id(opportunity.getId())
            .code(opportunity.getCode())
            .name(opportunity.getName())
            .assignedTo(mapEmployeeToDTO(employee))
            .previouslyAssignedTo(mapEmployeeToDTO(previousLeader))
            .assignedAt(now)
            .assignedBy(mapUserToSummaryDTO(authorizationService.getCurrentUser()))
            .build();
            
        AssignLeaderResponse.NotificationDTO notificationDTO = AssignLeaderResponse.NotificationDTO.builder()
            .leaderNotified(leaderNotified)
            .salesNotified(salesNotified)
            .build();
            
        return AssignLeaderResponse.builder()
            .opportunity(opportunityDTO)
            .notification(notificationDTO)
            .activityLogged(true)
            .build();
    }
    
    /**
     * Maps an Employee entity to an EmployeeDTO.
     * 
     * @param employee the employee entity
     * @return the employee DTO, or null if employee is null
     */
    private AssignLeaderResponse.EmployeeDTO mapEmployeeToDTO(Employee employee) {
        if (employee == null) {
            return null;
        }
        
        return AssignLeaderResponse.EmployeeDTO.builder()
            .id(employee.getId())
            .name(employee.getFirstName() + " " + employee.getLastName())
            .email(employee.getCompanyEmail())
            .position(employee.getPosition())
            .phone(employee.getPhoneNumber())
            .build();
    }
    
    /**
     * Maps a User entity to a UserSummaryDTO.
     * 
     * @param user the user entity
     * @return the user summary DTO, or null if user is null
     */
    private AssignLeaderResponse.UserSummaryDTO mapUserToSummaryDTO(User user) {
        if (user == null) {
            return null;
        }
        
        return AssignLeaderResponse.UserSummaryDTO.builder()
            .id(user.getId())
            .name(user.getFullName())
            .build();
    }
    
    /**
     * Logs an assignment of a leader to an opportunity.
     * 
     * @param opportunity the opportunity
     * @param previousLeader the previous leader, if any
     * @param newLeader the new leader
     * @param note the note about the assignment
     */
    private void logAssignment(Opportunity opportunity, Employee previousLeader, Employee newLeader, String note) {
        try {
            User currentUser = authorizationService.getCurrentUser();
            
            String oldValue = previousLeader != null ? 
                    previousLeader.getFirstName() + " " + previousLeader.getLastName() : "None";
            String newValue = newLeader.getFirstName() + " " + newLeader.getLastName();
            
            String description = String.format("Leader assigned from %s to %s", oldValue, newValue);
            
            OpportunityActivityLog activityLog = OpportunityActivityLog.builder()
                    .opportunity(opportunity)
                    .user(currentUser)
                    .activityType("LEADER_ASSIGNMENT")
                    .activityDescription(description)
                    .oldValue(oldValue)
                    .newValue(newValue)
                    .note(note)
                    .activityTimestamp(LocalDateTime.now())
                    .build();
            
            opportunityActivityLogRepository.save(activityLog);
            
            log.info("Activity logged: Leader assigned for opportunity {}: from {} to {}. Note: {}",
                    opportunity.getId(), oldValue, newValue, note);
                    
        } catch (Exception e) {
            log.error("Failed to log assignment activity for opportunity {}: {}", 
                    opportunity.getId(), e.getMessage());
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public SyncHubspotResponse synchronizeHubspot(SyncHubspotRequest request) {
        // This is a placeholder implementation
        // In a real implementation, this would connect to Hubspot API
        // and synchronize opportunities
        
        String syncId = "SYNC-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "-" + UUID.randomUUID().toString().substring(0, 6);
        
        SyncHubspotResponse.SyncParamsDTO syncParams = SyncHubspotResponse.SyncParamsDTO.builder()
            .syncMode(request.getSyncMode())
            .fromDate(request.getFromDate())
            .toDate(request.getToDate())
            .dealStage(request.getDealStage())
            .overwriteExisting(request.getOverwriteExisting())
            .build();
            
        return SyncHubspotResponse.builder()
            .syncId(syncId)
            .status("queued")
            .message("Quá trình đồng bộ đã được khởi tạo")
            .estimatedTime(120)
            .syncParams(syncParams)
            .logsUrl("/api/v1/opportunities/sync/logs")
            .build();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public SyncLogResponse getSyncLogs(String syncId, String status, String fromDate, String toDate, 
                                 Integer page, Integer size, String sortBy, String sortDir) {
        // This is a placeholder implementation
        
        return SyncLogResponse.builder()
            .content(List.of())
            .pageable(PageableInfo.builder()
                .pageNumber(page)
                .pageSize(size)
                .totalPages(0)
                .totalElements(0L)
                .sort(sortBy + "," + sortDir)
                .build())
            .build();
    }
    
    /**
     * Builds a specification for filtering opportunities based on request parameters.
     * 
     * @param request the filter request
     * @return the specification
     */
    private Specification<Opportunity> buildFilterSpecification(ListOpportunitiesRequest request) {
        Specification<Opportunity> spec = Specification.where(null);
        
        // Keyword search
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            spec = spec.and(OpportunitySpecification.hasKeyword(request.getKeyword()));
        }
        
        // Status filter
        if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
            List<String> statuses = Arrays.asList(request.getStatus().split(","));
            spec = spec.and(OpportunitySpecification.hasStatus(statuses));
        }
        
        // Deal size filter
        if (request.getDealSize() != null && !request.getDealSize().trim().isEmpty()) {
            List<String> dealSizes = Arrays.asList(request.getDealSize().split(","));
            spec = spec.and(OpportunitySpecification.hasDealSize(dealSizes));
        }
        
        // Amount range filter
        if (request.getFromAmount() != null || request.getToAmount() != null) {
            spec = spec.and(OpportunitySpecification.hasValueBetween(request.getFromAmount(), request.getToAmount()));
        }
        
        // Priority filter
        if (request.getPriority() != null) {
            spec = spec.and(OpportunitySpecification.hasPriority(request.getPriority()));
        }
        
        // Assigned user filter
        if (request.getAssignedTo() != null) {
            spec = spec.and(OpportunitySpecification.hasAssignedTo(request.getAssignedTo()));
        }
        
        // Creator filter
        if (request.getCreatedBy() != null) {
            spec = spec.and(OpportunitySpecification.hasCreatedBy(request.getCreatedBy()));
        }
        
        // Employee assignment filter
        if (request.getEmployeeId() != null) {
            spec = spec.and(OpportunitySpecification.hasEmployee(request.getEmployeeId()));
        }
        
        // Date range filter
        LocalDateTime fromDate = null;
        LocalDateTime toDate = null;
        
        if (request.getFromDate() != null && !request.getFromDate().trim().isEmpty()) {
            fromDate = LocalDate.parse(request.getFromDate(), DateTimeFormatter.ISO_DATE).atStartOfDay();
        }
        
        if (request.getToDate() != null && !request.getToDate().trim().isEmpty()) {
            toDate = LocalDate.parse(request.getToDate(), DateTimeFormatter.ISO_DATE).atTime(23, 59, 59);
        }
        
        if (fromDate != null || toDate != null) {
            spec = spec.and(OpportunitySpecification.hasCreatedDateBetween(fromDate, toDate));
        }
        
        // Last interaction date range filter
        LocalDateTime fromLastInteractionDate = null;
        LocalDateTime toLastInteractionDate = null;
        
        if (request.getFromLastInteractionDate() != null && !request.getFromLastInteractionDate().trim().isEmpty()) {
            fromLastInteractionDate = LocalDate.parse(request.getFromLastInteractionDate(), DateTimeFormatter.ISO_DATE).atStartOfDay();
        }
        
        if (request.getToLastInteractionDate() != null && !request.getToLastInteractionDate().trim().isEmpty()) {
            toLastInteractionDate = LocalDate.parse(request.getToLastInteractionDate(), DateTimeFormatter.ISO_DATE).atTime(23, 59, 59);
        }
        
        if (fromLastInteractionDate != null || toLastInteractionDate != null) {
            spec = spec.and(OpportunitySpecification.hasLastInteractionDateBetween(fromLastInteractionDate, toLastInteractionDate));
        }
        
        // Apply access control filter
        if (!authorizationService.hasRole("ROLE_ADMIN")) {
            if (authorizationService.hasPermission("opportunity:read:own")) {
                Long currentUserId = authorizationService.getCurrentUserId();
                spec = spec.and(OpportunitySpecification.hasCreatedBy(currentUserId));
            } else if (authorizationService.hasPermission("opportunity:read:assigned")) {
                Long currentUserId = authorizationService.getCurrentUserId();
                spec = spec.and(OpportunitySpecification.hasAssignedTo(currentUserId));
            }
        }
        
        return spec;
    }
    
    /**
     * Creates a summary of statistics for a list of opportunities.
     * 
     * @param opportunities the list of opportunities
     * @return the summary DTO
     */
    private OpportunitySummaryDTO createSummary(List<Opportunity> opportunities) {
        OpportunitySummaryDTO summary = OpportunitySummaryDTO.builder()
                .totalCount(opportunities.size())
                .totalAmount(BigDecimal.ZERO)
                .build();
        
        // Initialize default distributions
        summary.initializeDefaultStatusDistribution();
        summary.initializeDefaultDealSizeDistribution();
        
        // Collect statistics
        BigDecimal totalAmount = BigDecimal.ZERO;
        Map<String, Integer> statusCounts = summary.getByStatus();
        Map<String, Integer> dealSizeCounts = summary.getByDealSize();
        
        for (Opportunity opp : opportunities) {
            // Sum total amount
            if (opp.getAmount() != null) {
                totalAmount = totalAmount.add(opp.getAmount());
            }
            
            // Count by status
            if (opp.getStatus() != null) {
                statusCounts.compute(opp.getStatus(), (k, v) -> (v == null) ? 1 : v + 1);
            }
            
            // Add logic to categorize by deal size based on amount ranges
            String dealSize = getDealSizeCategory(opp.getAmount());
            if (dealSize != null) {
                dealSizeCounts.compute(dealSize, (k, v) -> (v == null) ? 1 : v + 1);
            }
        }
        
        summary.setTotalAmount(totalAmount);
        
        return summary;
    }
    
    /**
     * Determines the deal size category based on the amount.
     * 
     * @param amount the amount
     * @return the deal size category
     */
    private String getDealSizeCategory(BigDecimal amount) {
        if (amount == null) {
            return "small";
        }
        
        // Example thresholds - these should be configurable in a real implementation
        if (amount.compareTo(new BigDecimal("1000000000")) >= 0) {
            return "extra_large";  // >= 1 billion
        } else if (amount.compareTo(new BigDecimal("500000000")) >= 0) {
            return "large";        // >= 500 million
        } else if (amount.compareTo(new BigDecimal("100000000")) >= 0) {
            return "medium";       // >= 100 million
        } else {
            return "small";        // < 100 million
        }
    }
    
    /**
     * Maps an Opportunity entity to a DTO.
     * 
     * @param opportunity the opportunity entity
     * @return the opportunity DTO
     */
    private OpportunityDTO mapToDTO(Opportunity opportunity) {
        if (opportunity == null) {
            return null;
        }
        
        OpportunityDTO.UserSummaryDTO assignedToDTO = null;
        if (opportunity.getAssignedTo() != null) {
            assignedToDTO = OpportunityDTO.UserSummaryDTO.builder()
                    .id(opportunity.getAssignedTo().getId())
                    .name(opportunity.getAssignedTo().getFullName())
                    .build();
        }
        
        OpportunityDTO.UserSummaryDTO createdByDTO = null;
        if (opportunity.getCreatedBy() != null) {
            createdByDTO = OpportunityDTO.UserSummaryDTO.builder()
                    .id(opportunity.getCreatedBy().getId())
                    .name(opportunity.getCreatedBy().getFullName())
                    .build();
        }
        
        // Map employee assignments
        List<OpportunityDTO.EmployeeAssignmentDTO> employeeAssignments = opportunity.getAssignments().stream()
            .map(assignment -> {
                Employee employee = assignment.getEmployee();
                return OpportunityDTO.EmployeeAssignmentDTO.builder()
                    .id(assignment.getId())
                    .employeeId(employee.getId())
                    .employeeName(employee.getFirstName() + " " + employee.getLastName())
                    .employeeCode(employee.getEmployeeCode())
                    .assignedAt(assignment.getAssignedAt())
                    .build();
            })
            .collect(Collectors.toList());
        
        return OpportunityDTO.builder()
                .id(opportunity.getId())
                .code(opportunity.getCode())
                .name(opportunity.getName())
                .description(opportunity.getDescription())
                .customerName(opportunity.getClientName())
                .customerContact(opportunity.getClientContact())
                .customerEmail(opportunity.getClientEmail())
                .customerPhone(opportunity.getClientPhone())
                .amount(opportunity.getAmount())
                .currency(opportunity.getCurrency())
                .status(opportunity.getStatus())
                .dealSize(opportunity.getDealSize())
                .source(opportunity.getSource())
                .externalId(opportunity.getExternalId())
                .createdBy(createdByDTO)
                .assignedTo(assignedToDTO)
                .employeeAssignments(employeeAssignments)
                .lastInteractionDate(opportunity.getLastInteractionDate())
                .priority(opportunity.getPriority())
                .createdAt(opportunity.getCreatedAt())
                .updatedAt(opportunity.getUpdatedAt())
                .tags(opportunity.getTags())
                .build();
    }
    
    /**
     * Logs a change to the priority of an opportunity.
     * 
     * @param opportunity the opportunity
     * @param oldPriority the old priority value
     * @param newPriority the new priority value
     * @param note the note about the change
     */
    private void logPriorityChange(Opportunity opportunity, Boolean oldPriority, Boolean newPriority, String note) {
        try {
            User currentUser = authorizationService.getCurrentUser();
            
            String oldValue = oldPriority != null ? (oldPriority ? "High Priority" : "Normal Priority") : "Normal Priority";
            String newValue = newPriority != null ? (newPriority ? "High Priority" : "Normal Priority") : "Normal Priority";
            
            String description = String.format("Priority changed from %s to %s", oldValue, newValue);
            
            OpportunityActivityLog activityLog = OpportunityActivityLog.builder()
                    .opportunity(opportunity)
                    .user(currentUser)
                    .activityType("PRIORITY_CHANGE")
                    .activityDescription(description)
                    .oldValue(oldValue)
                    .newValue(newValue)
                    .note(note)
                    .activityTimestamp(LocalDateTime.now())
                    .build();
            
            opportunityActivityLogRepository.save(activityLog);
            
            log.info("Activity logged: Priority changed for opportunity {} from {} to {}. Note: {}",
                    opportunity.getId(), oldValue, newValue, note);
                    
        } catch (Exception e) {
            log.error("Failed to log priority change activity for opportunity {}: {}", 
                    opportunity.getId(), e.getMessage());
        }
    }

    @Override
    @Transactional
    public OpportunityDTO createOpportunity(OpportunityDTO opportunityDTO) {
        log.info("Creating new opportunity: {}", opportunityDTO.getName());
        
        // Tạo mã code cho opportunity nếu chưa có
        if (opportunityDTO.getCode() == null || opportunityDTO.getCode().isEmpty()) {
            opportunityDTO.setCode(generateOpportunityCode());
        }
        
        // Map DTO thành entity
        Opportunity opportunity = new Opportunity();
        opportunity.setCode(opportunityDTO.getCode());
        opportunity.setName(opportunityDTO.getName());
        opportunity.setDescription(opportunityDTO.getDescription());
        opportunity.setClientName(opportunityDTO.getCustomerName());
        opportunity.setClientContact(opportunityDTO.getCustomerContact());
        opportunity.setClientEmail(opportunityDTO.getCustomerEmail());
        opportunity.setClientPhone(opportunityDTO.getCustomerPhone());
        opportunity.setAmount(opportunityDTO.getAmount());
        opportunity.setCurrency(opportunityDTO.getCurrency() != null ? opportunityDTO.getCurrency() : "VND");
        opportunity.setStatus(opportunityDTO.getStatus() != null ? opportunityDTO.getStatus() : "new");
        opportunity.setDealSize(getDealSizeCategory(opportunityDTO.getAmount()));
        opportunity.setPriority(opportunityDTO.getPriority() != null ? opportunityDTO.getPriority() : false);
        opportunity.setSource("manual");
        
        // Lưu opportunity
        Opportunity savedOpportunity = opportunityRepository.save(opportunity);
        log.info("Created opportunity with ID: {}", savedOpportunity.getId());
        
        return mapToDTO(savedOpportunity);
    }

    @Override
    @Transactional
    public OpportunityDTO updateOpportunity(Long id, OpportunityDTO opportunityDTO) {
        log.info("Updating opportunity with ID: {}", id);
        
        // Lấy opportunity hiện tại
        Opportunity opportunity = getOpportunityEntityById(id);
        
        // Cập nhật thông tin
        opportunity.setName(opportunityDTO.getName());
        opportunity.setDescription(opportunityDTO.getDescription());
        opportunity.setClientName(opportunityDTO.getCustomerName());
        opportunity.setClientContact(opportunityDTO.getCustomerContact());
        opportunity.setClientEmail(opportunityDTO.getCustomerEmail());
        opportunity.setClientPhone(opportunityDTO.getCustomerPhone());
        
        // Cập nhật amount và dealSize nếu amount thay đổi
        if (opportunityDTO.getAmount() != null && 
            !opportunityDTO.getAmount().equals(opportunity.getAmount())) {
            opportunity.setAmount(opportunityDTO.getAmount());
            opportunity.setDealSize(getDealSizeCategory(opportunityDTO.getAmount()));
        }
        
        opportunity.setCurrency(opportunityDTO.getCurrency());
        
        // Lưu opportunity
        Opportunity updatedOpportunity = opportunityRepository.save(opportunity);
        log.info("Updated opportunity with ID: {}", updatedOpportunity.getId());
        
        return mapToDTO(updatedOpportunity);
    }

    @Override
    @Transactional
    public void deleteOpportunity(Long id) {
        log.info("Deleting opportunity with ID: {}", id);
        
        // Lấy opportunity
        Opportunity opportunity = getOpportunityEntityById(id);
        
        // Soft delete
        opportunity.setDeletedAt(LocalDateTime.now());
        opportunityRepository.save(opportunity);
        
        log.info("Soft-deleted opportunity with ID: {}", id);
    }

    @Override
    @Transactional
    public OpportunityDTO updateStatus(Long id, String status, String note) {
        log.info("Updating status for opportunity with ID: {}", id);
        
        // Lấy opportunity
        Opportunity opportunity = getOpportunityEntityById(id);
        
        // Lưu status cũ để log
        String oldStatus = opportunity.getStatus();
        
        // Cập nhật status
        opportunity.setStatus(status);
        
        // Lưu opportunity
        Opportunity updatedOpportunity = opportunityRepository.save(opportunity);
        
        // Log activity
        logStatusChange(updatedOpportunity, oldStatus, status, note);
        
        log.info("Updated status for opportunity with ID: {}", id);
        
        return mapToDTO(updatedOpportunity);
    }

    @Override
    @Transactional
    public OpportunityDTO updateClosingInfo(Long id, String closingDate, Integer closingProbability) {
        log.info("Updating closing info for opportunity with ID: {}", id);
        
        // Lấy opportunity
        Opportunity opportunity = getOpportunityEntityById(id);
        
        // Cập nhật thông tin closing
        if (closingDate != null && !closingDate.isEmpty()) {
            opportunity.setClosingDate(LocalDate.parse(closingDate).atStartOfDay());
        }
        
        if (closingProbability != null) {
            opportunity.setClosingProbability(closingProbability);
        }
        
        // Lưu opportunity
        Opportunity updatedOpportunity = opportunityRepository.save(opportunity);
        
        log.info("Updated closing info for opportunity with ID: {}", id);
        
        return mapToDTO(updatedOpportunity);
    }

    /**
     * Log thay đổi trạng thái của opportunity.
     *
     * @param opportunity Opportunity đã được cập nhật
     * @param oldStatus trạng thái cũ
     * @param newStatus trạng thái mới
     * @param note ghi chú về thay đổi
     */
    private void logStatusChange(Opportunity opportunity, String oldStatus, String newStatus, String note) {
        // Implement logging logic here
        // This could involve creating a status change record or activity log
        log.info("Status changed for opportunity {}: {} -> {}", 
                 opportunity.getId(), oldStatus, newStatus);
    }

    /**
     * Tạo mã code cho opportunity mới.
     *
     * @return mã code dạng OPP-YYYYMMDD-XXXXX
     */
    private String generateOpportunityCode() {
        LocalDate today = LocalDate.now();
        String datePrefix = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // Tìm mã số tiếp theo
        String prefix = "OPP-" + datePrefix + "-";
        
        // Tìm code có tiền tố tương tự và lấy giá trị lớn nhất
        List<Opportunity> opportunities = opportunityRepository.findAll();
        
        long maxSequence = 0;
        for (Opportunity opportunity : opportunities) {
            String code = opportunity.getCode();
            if (code != null && code.startsWith(prefix)) {
                try {
                    String sequenceStr = code.substring(prefix.length());
                    long sequence = Long.parseLong(sequenceStr);
                    if (sequence > maxSequence) {
                        maxSequence = sequence;
                    }
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    // Bỏ qua nếu format không đúng
                    log.warn("Opportunity code format không hợp lệ: {}", code);
                }
            }
        }
        
        Long nextSequence = maxSequence + 1;
        
        // Format với độ dài cố định (5 chữ số)
        return prefix + String.format("%05d", nextSequence);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public OpportunityDTO updateLastInteractionDate(Long id, String interactionDate, String note) {
        log.debug("Request to update last interaction date of opportunity ID: {} to {}", id, interactionDate);
        
        Opportunity opportunity = getOpportunityEntityById(id);
        
        // Save old value for logging
        LocalDateTime oldInteractionDate = opportunity.getLastInteractionDate();
        
        // Parse the new date
        LocalDate parsedDate = LocalDate.parse(interactionDate, DateTimeFormatter.ISO_DATE);
        LocalDateTime newInteractionDate = parsedDate.atTime(12, 0); // Set to noon by default
        
        // Update interaction date
        opportunity.setLastInteractionDate(newInteractionDate);
        
        // Save changes
        Opportunity updated = opportunityRepository.save(opportunity);
        
        // Log activity about interaction date change
        logInteractionDateChange(updated, oldInteractionDate, newInteractionDate, note);
        
        return mapToDTO(updated);
    }
    
    /**
     * Log changes to the last interaction date.
     */
    private void logInteractionDateChange(Opportunity opportunity, LocalDateTime oldDate, LocalDateTime newDate, String note) {
        // Implementation would typically create an audit log entry
        // For now, we just log to console
        log.info("Updated last interaction date for opportunity ID: {} from {} to {}", 
                opportunity.getId(), 
                oldDate != null ? oldDate.toString() : "null", 
                newDate != null ? newDate.toString() : "null");
        
        if (note != null && !note.isEmpty()) {
            log.info("Note for interaction date change: {}", note);
        }
    }
} 