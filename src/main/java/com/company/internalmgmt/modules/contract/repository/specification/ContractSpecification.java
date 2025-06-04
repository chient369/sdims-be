package com.company.internalmgmt.modules.contract.repository.specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.company.internalmgmt.modules.contract.model.Contract;
import com.company.internalmgmt.modules.contract.model.ContractEmployee;
import com.company.internalmgmt.modules.hrm.model.Employee;
import com.company.internalmgmt.modules.opportunity.model.Opportunity;
import com.company.internalmgmt.modules.admin.model.User;

/**
 * Specification class for Contract search
 */
public class ContractSpecification {

    /**
     * Create specification for searching contracts with various criteria
     * 
     * @param customerName the customer name
     * @param contractCode the contract code
     * @param status the status
     * @param contractType the contract type
     * @param salesId the sales ID
     * @param minAmount the minimum amount
     * @param maxAmount the maximum amount
     * @param fromDate the from date
     * @param toDate the to date
     * @param paymentStatus the payment status
     * @return specification for contract search
     */
    public static Specification<Contract> searchContracts(
            String customerName, 
            String contractCode, 
            String status, 
            String contractType, 
            Long salesId,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            LocalDate fromDate,
            LocalDate toDate,
            String paymentStatus) {
        
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Customer name filter
            if (StringUtils.hasText(customerName)) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("clientName")), 
                    "%" + customerName.toLowerCase() + "%"
                ));
            }
            
            // Contract code filter
            if (StringUtils.hasText(contractCode)) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("contractCode")), 
                    "%" + contractCode.toLowerCase() + "%"
                ));
            }
            
            // Status filter
            if (StringUtils.hasText(status)) {
                predicates.add(criteriaBuilder.equal(
                    root.get("status"), status
                ));
            }
            
            // Contract type filter
            if (StringUtils.hasText(contractType)) {
                predicates.add(criteriaBuilder.equal(
                    root.get("contractType"), contractType
                ));
            }
            
            // Sales ID filter
            if (salesId != null) {
                predicates.add(criteriaBuilder.equal(
                    root.get("assignedSales").get("id"), salesId
                ));
            }
            
            // Amount range filter
            if (minAmount != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("totalValue"), minAmount
                ));
            }
            
            if (maxAmount != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("totalValue"), maxAmount
                ));
            }
            
            // Date range filter
            if (fromDate != null && toDate != null) {
                predicates.add(criteriaBuilder.between(
                    root.get("effectiveDate"), fromDate, toDate
                ));
            } else if (fromDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("effectiveDate"), fromDate
                ));
            } else if (toDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("effectiveDate"), toDate
                ));
            }
            
            // Payment status filter - requires joining with payment terms
            // This is a simplification - actual implementation would depend on how payment status is determined
            if (StringUtils.hasText(paymentStatus)) {
                // This is just a placeholder - the actual implementation would need more work
                // based on how payment status is calculated in the system
                Join<Contract, ContractEmployee> paymentTermsJoin = root.join("paymentTerms", JoinType.LEFT);
                predicates.add(criteriaBuilder.equal(
                    paymentTermsJoin.get("paymentStatus"), paymentStatus
                ));
            }
            
            // Soft delete filter - Only return non-deleted contracts
            predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    /**
     * Create specification for contracts by team ID
     * 
     * @param teamId the team ID
     * @return specification for contracts by team ID
     */
    public static Specification<Contract> findByTeamId(Long teamId) {
        return (root, query, criteriaBuilder) -> {
            Join<Contract, ContractEmployee> contractEmployeeJoin = root.join("contractEmployees", JoinType.INNER);
            Join<ContractEmployee, Employee> employeeJoin = contractEmployeeJoin.join("employee", JoinType.INNER);
            
            return criteriaBuilder.equal(employeeJoin.get("team").get("id"), teamId);
        };
    }
    
    /**
     * Create specification for contracts by employee ID
     * 
     * @param employeeId the employee ID
     * @return specification for contracts by employee ID
     */
    public static Specification<Contract> findByEmployeeId(Long employeeId) {
        return (root, query, criteriaBuilder) -> {
            Join<Contract, ContractEmployee> contractEmployeeJoin = root.join("contractEmployees", JoinType.INNER);
            
            return criteriaBuilder.equal(contractEmployeeJoin.get("employee").get("id"), employeeId);
        };
    }
    
    /**
     * Create specification for contracts by opportunity ID
     * 
     * @param opportunityId the opportunity ID
     * @return specification for contracts by opportunity ID
     */
    public static Specification<Contract> findByOpportunityId(Long opportunityId) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("opportunity").get("id"), opportunityId);
        };
    }
} 