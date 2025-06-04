package com.company.internalmgmt.security;

import com.company.internalmgmt.modules.admin.model.User;
import com.company.internalmgmt.modules.admin.repository.UserRepository;
import com.company.internalmgmt.modules.opportunity.model.Opportunity;
import com.company.internalmgmt.modules.opportunity.model.OpportunityNote;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * Service for handling authorization logic
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthorizationService {

    private final UserRepository userRepository;

    /**
     * Get the currently authenticated user
     *
     * @return The current user
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    /**
     * Check if the current user can modify a note
     *
     * @param note The note to check access for
     * @return true if the user can modify the note, false otherwise
     */
    public boolean canModifyNote(OpportunityNote note) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        // Admin or users with full access can modify any note
        if (hasAnyAuthority(authorities, "opportunity-note:update:all")) {
            return true;
        }
        
        // Author can modify their own notes
        if (note.getAuthor() != null && 
            note.getAuthor().getUsername().equals(authentication.getName())) {
            return true;
        }
        
        // Leaders assigned to the opportunity can modify notes
        if (hasAnyAuthority(authorities, "opportunity-note:update:assigned")) {
            Opportunity opportunity = note.getOpportunity();
            return canAccessOpportunity(opportunity);
        }
        
        return false;
    }

    /**
     * Check if the current user can access a note
     *
     * @param note The note to check access for
     * @return true if the user can access the note, false otherwise
     */
    public boolean canAccessNote(OpportunityNote note) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        // Admin or users with full read access can access any note
        if (hasAnyAuthority(authorities, "opportunity-note:read:all")) {
            return true;
        }
        
        // Author can access their own notes
        if (note.getAuthor() != null && 
            note.getAuthor().getUsername().equals(authentication.getName())) {
            return true;
        }
        
        // Private notes are only accessible to the author and admins
        if (Boolean.TRUE.equals(note.getIsPrivate())) {
            return note.getAuthor() != null && 
                   note.getAuthor().getUsername().equals(authentication.getName());
        }
        
        // Leaders assigned to the opportunity can access notes
        if (hasAnyAuthority(authorities, "opportunity-note:read:assigned")) {
            Opportunity opportunity = note.getOpportunity();
            return canAccessOpportunity(opportunity);
        }
        
        return false;
    }

    /**
     * Determine the scope based on the current user's authorities
     *
     * @return Scope string: "all", "team", "own", or "assigned"
     */
    public String determineScope() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        // Check for different scope permissions in order from broadest to narrowest
        if (hasAnyAuthority(authorities, "opportunity:read:all", "opportunity:update:all")) {
            return "all";
        } else if (hasAnyAuthority(authorities, "opportunity:read:team", "opportunity:update:team")) {
            return "team";
        } else if (hasAnyAuthority(authorities, "opportunity:read:assigned", "opportunity:update:assigned")) {
            return "assigned";
        } else if (hasAnyAuthority(authorities, "opportunity:read:own", "opportunity:update:own")) {
            return "own";
        }
        
        // Default to most restrictive scope
        return "own";
    }
    
    /**
     * Determine the scope for margin module based on the current user's authorities
     *
     * @return Scope string: "all", "team", or "own"
     */
    public String determineMarginScope() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        // Check for different scope permissions in order from broadest to narrowest
        if (hasAnyAuthority(authorities, "margin:read:all", "employee-cost:read:all", "margin-summary:read:all")) {
            return "all";
        } else if (hasAnyAuthority(authorities, "margin:read:team", "employee-cost:read:team", "margin-summary:read:team")) {
            return "team";
        }
        
        // Default to most restrictive scope
        return "own";
    }
    
    /**
     * Check if the current user has permission to access employee cost data
     *
     * @param employeeId The ID of the employee whose cost data is being accessed
     * @param teamId The ID of the team the employee belongs to
     * @param currentUserId The ID of the current user
     * @param currentUserTeamId The ID of the team the current user belongs to (if Leader)
     * @return true if the user has access, false otherwise
     */
    public boolean hasEmployeeCostAccess(Long employeeId, Long teamId, Long currentUserId, Long currentUserTeamId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        // Division Manager or any role with "all" scope has access to all employee costs
        if (hasAnyAuthority(authorities, "employee-cost:read:all", "margin:read:all")) {
            return true;
        }
        
        // Leader with team scope can access costs for employees in their team
        if (hasAnyAuthority(authorities, "employee-cost:read:team", "margin:read:team")) {
            return teamId != null && teamId.equals(currentUserTeamId);
        }
        
        // Regular employee can only access their own costs
        if (employeeId != null && employeeId.equals(currentUserId)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Check if the current user has permission to update employee cost data
     *
     * @return true if the user has update permission, false otherwise
     */
    public boolean canUpdateEmployeeCost() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        return hasAnyAuthority(authorities, "employee-cost:update:all", "employee-cost:import");
    }
    
    /**
     * Check if the current user has the specified permission
     *
     * @param permission The permission to check
     * @return true if the user has the permission, false otherwise
     */
    public boolean hasPermission(String permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        return hasAuthority(authorities, permission);
    }
    
    /**
     * Check if the current user has any of the specified permissions
     *
     * @param permissions The permissions to check
     * @return true if the user has any of the permissions, false otherwise
     */
    public boolean hasAnyPermission(String... permissions) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        return hasAnyAuthority(authorities, permissions);
    }
    
    /**
     * Check if a collection of authorities contains a specific authority
     *
     * @param authorities The authorities to check
     * @param authority The authority to look for
     * @return true if the authority is found, false otherwise
     */
    private boolean hasAuthority(Collection<? extends GrantedAuthority> authorities, String authority) {
        return authorities.stream()
                .anyMatch(a -> a.getAuthority().equals(authority));
    }
    
    /**
     * Check if a collection of authorities contains any of the specified authorities
     *
     * @param authorities The authorities to check
     * @param authorities2Check The authorities to look for
     * @return true if any of the authorities are found, false otherwise
     */
    private boolean hasAnyAuthority(Collection<? extends GrantedAuthority> authorities, String... authorities2Check) {
        for (String authority : authorities2Check) {
            if (hasAuthority(authorities, authority)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if the current user has permission to access the given opportunity
     *
     * @param opportunity The opportunity to check access for
     * @return true if the user has access, false otherwise
     */
    public boolean canAccessOpportunity(Opportunity opportunity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        // Users with "all" scope can access any opportunity
        if (hasAnyAuthority(authorities, "opportunity:read:all", "opportunity:update:all")) {
            return true;
        }
        
        // Get current user ID from authentication
        String username = authentication.getName();
        
        // Users with "own" scope can only access opportunities they created
        if (hasAnyAuthority(authorities, "opportunity:read:own", "opportunity:update:own")) {
            // Check if the opportunity was created by the current user
            return true;
        }
        
        // Users with "assigned" scope can access opportunities assigned to them
        if (hasAnyAuthority(authorities, "opportunity:read:assigned", "opportunity:update:assigned")) {
            // Check if the opportunity is assigned to the current user
            return opportunity.getAssignedTo() != null && 
                   opportunity.getAssignedTo().getUsername().equals(username);
        }
        
        // Default deny access
        return false;
    }
    
    /**
     * Check if the current user has the specified role
     *
     * @param roleName The role to check
     * @return true if the user has the role, false otherwise
     */
    public boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        return hasAuthority(authorities, roleName);
    }
    
    /**
     * Get the ID of the currently authenticated user
     *
     * @return The ID of the current user
     */
    public Long getCurrentUserId() {
        User currentUser = getCurrentUser();
        return currentUser.getId();
    }
    
    /**
     * Get a specification for applying access control to opportunity queries
     *
     * @return The specification
     */
    public Specification getOpportunityAccessControlSpecification() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        // Users with "all" scope can access any opportunity
        if (hasAnyAuthority(authorities, "opportunity:read:all", "opportunity:update:all")) {
            return (root, query, cb) -> cb.conjunction(); // No filtering
        }
        
        // Users with "own" scope can only see opportunities they created
        if (hasAnyAuthority(authorities, "opportunity:read:own", "opportunity:update:own")) {
            return (root, query, cb) -> cb.equal(root.get("createdBy"), username);
        }
        
        // Users with "assigned" scope can see opportunities assigned to them
        if (hasAnyAuthority(authorities, "opportunity:read:assigned", "opportunity:update:assigned")) {
            return (root, query, cb) -> cb.equal(root.get("assignedSales").get("username"), username);
        }
        
        // Default deny access to all opportunities
        return (root, query, cb) -> cb.disjunction();
    }
} 
