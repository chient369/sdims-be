package com.company.internalmgmt.modules.hrm.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class for employee status log
 */
@Entity
@Table(name = "employee_status_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeStatusLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
    
    @Column(name = "status", nullable = false, length = 50)
    private String status;
    
    @Column(name = "project_name", length = 255)
    private String projectName;
    
    @Column(name = "client_name", length = 100)
    private String clientName;
    
    @Column(name = "note", length = 500)
    private String note;
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "expected_end_date")
    private LocalDate expectedEndDate;
    
    @Column(name = "allocation_percentage")
    private Integer allocationPercentage;
    
    @Column(name = "is_billable")
    private Boolean isBillable;
    
    @Column(name = "contract_id")
    private Long contractId;
    
    @Column(name = "log_timestamp")
    private LocalDateTime logTimestamp;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "updated_by")
    private Long updatedBy;
    
    /**
     * Method executed before persisting the entity
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (logTimestamp == null) {
            logTimestamp = LocalDateTime.now();
        }
    }
    
    /**
     * Method executed before updating the entity
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Set the log timestamp
     * 
     * @param timestamp the timestamp to set
     */
    public void setLogTimestamp(LocalDateTime timestamp) {
        this.logTimestamp = timestamp;
    }
} 
