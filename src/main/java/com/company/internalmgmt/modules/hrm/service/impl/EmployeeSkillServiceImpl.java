package com.company.internalmgmt.modules.hrm.service.impl;

import com.company.internalmgmt.common.exception.ResourceAlreadyExistsException;
import com.company.internalmgmt.common.exception.ResourceNotFoundException;
import com.company.internalmgmt.modules.hrm.dto.EmployeeSkillDto;
import com.company.internalmgmt.modules.hrm.dto.EmployeeSkillRequest;
import com.company.internalmgmt.modules.hrm.dto.mapper.EmployeeSkillMapper;
import com.company.internalmgmt.modules.hrm.model.Employee;
import com.company.internalmgmt.modules.hrm.model.EmployeeSkill;
import com.company.internalmgmt.modules.hrm.model.Skill;
import com.company.internalmgmt.modules.hrm.repository.EmployeeRepository;
import com.company.internalmgmt.modules.hrm.repository.EmployeeSkillRepository;
import com.company.internalmgmt.modules.hrm.repository.SkillRepository;
import com.company.internalmgmt.modules.hrm.service.EmployeeSkillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of EmployeeSkillService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeSkillServiceImpl implements EmployeeSkillService {

    private final EmployeeSkillRepository employeeSkillRepository;
    private final EmployeeRepository employeeRepository;
    private final SkillRepository skillRepository;
    private final EmployeeSkillMapper employeeSkillMapper;

    @Override
    public Page<EmployeeSkillDto> findByEmployeeId(Long employeeId, Pageable pageable) {
        log.debug("Request to get employee skills by employeeId: {}", employeeId);
        
        // Check if employee exists
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }
        
        Page<EmployeeSkill> page = employeeSkillRepository.findByEmployeeId(employeeId, pageable);
        return page.map(employeeSkillMapper::toDto);
    }

    @Override
    public List<EmployeeSkillDto> findAllByEmployeeId(Long employeeId) {
        log.debug("Request to get all employee skills by employeeId: {}", employeeId);
        
        // Check if employee exists
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }
        
        List<EmployeeSkill> employeeSkills = employeeSkillRepository.findByEmployeeId(employeeId);
        return employeeSkills.stream()
                .map(employeeSkillMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeSkillDto findByEmployeeIdAndSkillId(Long employeeId, Long skillId) {
        log.debug("Request to get employee skill by employeeId: {} and skillId: {}", employeeId, skillId);
        
        EmployeeSkill employeeSkill = employeeSkillRepository.findByEmployeeIdAndSkillId(employeeId, skillId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee skill not found for employee id: " + employeeId + " and skill id: " + skillId));
        
        return employeeSkillMapper.toDto(employeeSkill);
    }

    @Override
    public List<EmployeeSkill> findBySkillIds(List<Long> skillIds) {
        log.debug("Request to get employee skills by skillIds: {}", skillIds);
        return employeeSkillRepository.findBySkillIdIn(skillIds);
    }

    @Override
    public List<EmployeeSkill> findBySkillIdsAndMinExperience(List<Long> skillIds, Double minExperience) {
        log.debug("Request to get employee skills by skillIds: {} and minExperience: {}", skillIds, minExperience);
        
        if (minExperience != null) {
            return employeeSkillRepository.findBySkillIdInAndYearsOfExperienceGreaterThanEqual(skillIds, minExperience);
        } else {
            return findBySkillIds(skillIds);
        }
    }

    @Override
    @Transactional
    public EmployeeSkillDto addSkill(Long employeeId, EmployeeSkillRequest request) {
        log.debug("Request to add skill to employee: {}, request: {}", employeeId, request);
        
        // Check if employee exists
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        
        // Check if skill exists
        Skill skill = skillRepository.findById(request.getSkillId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + request.getSkillId()));
        
        // Check if employee already has this skill
        if (employeeSkillRepository.existsByEmployeeIdAndSkillId(employeeId, request.getSkillId())) {
            throw new ResourceAlreadyExistsException(
                    "Employee already has this skill assigned. Use update to modify it.");
        }
        
        EmployeeSkill employeeSkill = new EmployeeSkill();
        employeeSkill.setEmployee(employee);
        employeeSkill.setSkill(skill);
        employeeSkill.setSelfAssessmentLevel(request.getSelfAssessmentLevel());
        employeeSkill.setLeaderAssessmentLevel(request.getLeaderAssessmentLevel());
        employeeSkill.setYearsOfExperience(request.getYearsExperience());
        employeeSkill.setSelfComment(request.getSelfComment());
        employeeSkill.setLeaderComment(request.getLeaderComment());
        
        employeeSkill = employeeSkillRepository.save(employeeSkill);
        return employeeSkillMapper.toDto(employeeSkill);
    }

    @Override
    @Transactional
    public EmployeeSkillDto updateSkill(Long employeeId, Long skillId, EmployeeSkillRequest request) {
        log.debug("Request to update employee skill, employeeId: {}, skillId: {}, request: {}", 
                employeeId, skillId, request);
        
        // Check if the skill exists for this employee
        EmployeeSkill employeeSkill = employeeSkillRepository.findByEmployeeIdAndSkillId(employeeId, skillId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee skill not found for employee id: " + employeeId + " and skill id: " + skillId));
        
        // Update fields
        employeeSkill.setSelfAssessmentLevel(request.getSelfAssessmentLevel());
        if (request.getLeaderAssessmentLevel() != null) {
            employeeSkill.setLeaderAssessmentLevel(request.getLeaderAssessmentLevel());
        }
        employeeSkill.setYearsOfExperience(request.getYearsExperience());
        employeeSkill.setSelfComment(request.getSelfComment());
        if (request.getLeaderComment() != null) {
            employeeSkill.setLeaderComment(request.getLeaderComment());
        }
        
        employeeSkill = employeeSkillRepository.save(employeeSkill);
        return employeeSkillMapper.toDto(employeeSkill);
    }

    @Override
    @Transactional
    public void deleteSkill(Long employeeId, Long skillId) {
        log.debug("Request to delete employee skill, employeeId: {}, skillId: {}", employeeId, skillId);
        
        // Check if the skill exists for this employee
        if (!employeeSkillRepository.existsByEmployeeIdAndSkillId(employeeId, skillId)) {
            throw new ResourceNotFoundException(
                    "Employee skill not found for employee id: " + employeeId + " and skill id: " + skillId);
        }
        
        employeeSkillRepository.deleteByEmployeeIdAndSkillId(employeeId, skillId);
    }

    @Override
    @Transactional
    public EmployeeSkillDto evaluateSkill(Long employeeId, Long skillId, String level, String comment) {
        log.debug("Request to evaluate employee skill, employeeId: {}, skillId: {}, level: {}, comment: {}", 
                employeeId, skillId, level, comment);
        
        // Check if the skill exists for this employee
        EmployeeSkill employeeSkill = employeeSkillRepository.findByEmployeeIdAndSkillId(employeeId, skillId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee skill not found for employee id: " + employeeId + " and skill id: " + skillId));
        
        // Update leader assessment
        employeeSkill.setLeaderAssessmentLevel(level);
        employeeSkill.setLeaderComment(comment);
        
        employeeSkill = employeeSkillRepository.save(employeeSkill);
        return employeeSkillMapper.toDto(employeeSkill);
    }

    @Override
    public boolean hasSkill(Long employeeId, Long skillId) {
        log.debug("Request to check if employee has skill, employeeId: {}, skillId: {}", employeeId, skillId);
        return employeeSkillRepository.existsByEmployeeIdAndSkillId(employeeId, skillId);
    }

    @Override
    public List<EmployeeSkill> getEmployeeSkillsByEmployeeId(Long employeeId) {
        log.debug("Request to get employee skills by employeeId: {}", employeeId);
        
        // Check if employee exists
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }
        
        return employeeSkillRepository.findByEmployeeId(employeeId);
    }

    @Override
    public EmployeeSkill getEmployeeSkillById(Long id) {
        log.debug("Request to get employee skill by id: {}", id);
        return employeeSkillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee skill not found with id: " + id));
    }

    @Override
    public EmployeeSkill getEmployeeSkillByEmployeeIdAndSkillId(Long employeeId, Long skillId) {
        log.debug("Request to get employee skill by employeeId: {} and skillId: {}", employeeId, skillId);
        return employeeSkillRepository.findByEmployeeIdAndSkillId(employeeId, skillId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee skill not found for employee id: " + employeeId + " and skill id: " + skillId));
    }

    @Override
    @Transactional
    public EmployeeSkill addSkillToEmployee(Long employeeId, EmployeeSkill employeeSkill) {
        log.debug("Request to add skill to employee: {}, employeeSkill: {}", employeeId, employeeSkill);
        
        // Check if employee exists
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        
        // Check if skill exists
        if (employeeSkill.getSkill() == null || employeeSkill.getSkill().getId() == null) {
            throw new IllegalArgumentException("Skill ID is required");
        }
        
        Skill skill = skillRepository.findById(employeeSkill.getSkill().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + employeeSkill.getSkill().getId()));
        
        // Check if employee already has this skill
        if (employeeSkillRepository.existsByEmployeeIdAndSkillId(employeeId, skill.getId())) {
            throw new ResourceAlreadyExistsException(
                    "Employee already has this skill assigned. Use update to modify it.");
        }
        
        // Set employee and skill
        employeeSkill.setEmployee(employee);
        employeeSkill.setSkill(skill);
        
        return employeeSkillRepository.save(employeeSkill);
    }

    @Override
    @Transactional
    public EmployeeSkill updateEmployeeSkill(Long id, EmployeeSkill employeeSkill) {
        log.debug("Request to update employee skill: {}, id: {}", employeeSkill, id);
        
        EmployeeSkill existingEmployeeSkill = getEmployeeSkillById(id);
        
        // Update fields
        existingEmployeeSkill.setSelfAssessmentLevel(employeeSkill.getSelfAssessmentLevel());
        if (employeeSkill.getLeaderAssessmentLevel() != null) {
            existingEmployeeSkill.setLeaderAssessmentLevel(employeeSkill.getLeaderAssessmentLevel());
        }
        existingEmployeeSkill.setYearsOfExperience(employeeSkill.getYearsExperience());
        existingEmployeeSkill.setSelfComment(employeeSkill.getSelfComment());
        if (employeeSkill.getLeaderComment() != null) {
            existingEmployeeSkill.setLeaderComment(employeeSkill.getLeaderComment());
        }
        
        return employeeSkillRepository.save(existingEmployeeSkill);
    }

    @Override
    @Transactional
    public EmployeeSkill evaluateEmployeeSkill(Long id, String leaderAssessmentLevel, String leaderComment) {
        log.debug("Request to evaluate employee skill, id: {}, level: {}, comment: {}", 
                id, leaderAssessmentLevel, leaderComment);
        
        EmployeeSkill employeeSkill = getEmployeeSkillById(id);
        
        // Update leader assessment
        employeeSkill.setLeaderAssessmentLevel(leaderAssessmentLevel);
        employeeSkill.setLeaderComment(leaderComment);
        
        return employeeSkillRepository.save(employeeSkill);
    }

    @Override
    @Transactional
    public void removeEmployeeSkill(Long id) {
        log.debug("Request to remove employee skill: {}", id);
        
        EmployeeSkill employeeSkill = getEmployeeSkillById(id);
        employeeSkillRepository.delete(employeeSkill);
    }

    @Override
    @Transactional
    public void removeEmployeeSkillByEmployeeIdAndSkillId(Long employeeId, Long skillId) {
        log.debug("Request to remove employee skill by employeeId: {} and skillId: {}", employeeId, skillId);
        
        if (!employeeSkillRepository.existsByEmployeeIdAndSkillId(employeeId, skillId)) {
            throw new ResourceNotFoundException(
                    "Employee skill not found for employee id: " + employeeId + " and skill id: " + skillId);
        }
        
        employeeSkillRepository.deleteByEmployeeIdAndSkillId(employeeId, skillId);
    }

    @Override
    public List<EmployeeSkill> findEmployeesBySkillAndLevel(Long skillId, String level) {
        log.debug("Request to find employees by skill and level, skillId: {}, level: {}", skillId, level);
        return employeeSkillRepository.findBySkillIdAndLeaderAssessmentLevel(skillId, level);
    }

    @Override
    public List<EmployeeSkill> findEmployeesBySkillAndMinYears(Long skillId, double years) {
        log.debug("Request to find employees by skill and minimum years, skillId: {}, years: {}", skillId, years);
        return employeeSkillRepository.findBySkillIdAndYearsOfExperienceGreaterThanEqual(skillId, years);
    }
} 
