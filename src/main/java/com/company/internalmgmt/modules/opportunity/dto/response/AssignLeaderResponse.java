package com.company.internalmgmt.modules.opportunity.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for the response after assigning an employee to an opportunity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignLeaderResponse {
    
    /**
     * Opportunity information after assignment.
     */
    private OpportunityAssignmentDTO opportunity;
    
    /**
     * Notification status.
     */
    private NotificationDTO notification;
    
    /**
     * Whether the activity was logged.
     */
    private Boolean activityLogged;
    
    /**
     * DTO for opportunity assignment information.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OpportunityAssignmentDTO {
        private Long id;
        private String code;
        private String name;
        private EmployeeDTO assignedTo;
        private EmployeeDTO previouslyAssignedTo;
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        private LocalDateTime assignedAt;
        
        private UserSummaryDTO assignedBy;
    }
    
    /**
     * DTO for employee information.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmployeeDTO {
        private Long id;
        private String name;
        private String email;
        private String position;
        private String phone;
    }
    
    /**
     * DTO for summarized user information.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummaryDTO {
        private Long id;
        private String name;
    }
    
    /**
     * DTO for notification status.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationDTO {
        private Boolean leaderNotified;
        private Boolean salesNotified;
    }
} 