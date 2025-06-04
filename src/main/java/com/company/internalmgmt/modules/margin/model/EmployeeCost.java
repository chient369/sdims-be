package com.company.internalmgmt.modules.margin.model;

import com.company.internalmgmt.common.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing monthly cost of an employee
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employee_costs", uniqueConstraints = {
    @UniqueConstraint(name = "uq_employee_cost_period", columnNames = {"employee_id", "year", "month"})
})
@Where(clause = "deleted_at IS NULL")
public class EmployeeCost extends BaseEntity {

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "month", nullable = false)
    private Integer month;

    @Column(name = "cost_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal costAmount;

    @Column(name = "basic_salary", precision = 15, scale = 2)
    private BigDecimal basicSalary;

    @Column(name = "allowance", precision = 15, scale = 2)
    private BigDecimal allowance;

    @Column(name = "overtime", precision = 15, scale = 2)
    private BigDecimal overtime;

    @Column(name = "other_costs", precision = 15, scale = 2)
    private BigDecimal otherCosts;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @Column(name = "note")
    private String note;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
} 