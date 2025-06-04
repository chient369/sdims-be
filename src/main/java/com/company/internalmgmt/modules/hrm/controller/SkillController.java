package com.company.internalmgmt.modules.hrm.controller;

import com.company.internalmgmt.modules.hrm.dto.SkillDto;
import com.company.internalmgmt.modules.hrm.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST controller for managing skills
 */
@RestController
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Skill Management", description = "APIs for managing skills")
public class SkillController {

    private final SkillService skillService;

    /**
     * GET /api/v1/skills : Get all skills
     *
     * @param categoryId the category ID (optional)
     * @param search the search term (optional)
     * @param page the page number
     * @param size the page size
     * @param sort the sort field
     * @param direction the sort direction
     * @return the ResponseEntity with status 200 (OK) and the list of skills in body
     */
    @GetMapping
    @Operation(summary = "Get all skills", description = "Get all skills with filtering options")
    public ResponseEntity<Page<SkillDto>> getAllSkills(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        log.debug("REST request to get skills with pagination and filters");
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<SkillDto> skills = skillService.findAll(categoryId, search, pageable);
        return ResponseEntity.ok(skills);
    }

    /**
     * GET /api/v1/skills/{id} : Get a skill by ID
     *
     * @param id the ID of the skill to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the skill, or with status 404 (Not Found)
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a skill by ID", description = "Get detailed information about a skill by ID")
    public ResponseEntity<SkillDto> getSkillById(@PathVariable Long id) {
        log.debug("REST request to get Skill : {}", id);
        SkillDto skill = skillService.findById(id);
        return ResponseEntity.ok(skill);
    }

    /**
     * GET /api/v1/skills/category/{categoryId} : Get all skills in a category
     *
     * @param categoryId the ID of the category
     * @return the ResponseEntity with status 200 (OK) and the list of skills in body
     */
    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get all skills in a category", description = "Get all skills in a specific category")
    public ResponseEntity<List<SkillDto>> getSkillsByCategory(@PathVariable Long categoryId) {
        log.debug("REST request to get skills by category : {}", categoryId);
        List<SkillDto> skills = skillService.findByCategoryId(categoryId);
        return ResponseEntity.ok(skills);
    }

    /**
     * POST /api/v1/skills : Create a new skill
     *
     * @param skillDto the skill to create
     * @return the ResponseEntity with status 201 (Created) and with body the new skill
     */
    @PostMapping
    @Operation(summary = "Create a new skill", description = "Create a new skill")
    public ResponseEntity<SkillDto> createSkill(@Valid @RequestBody SkillDto skillDto) {
        log.debug("REST request to save Skill : {}", skillDto);
        
        if (skillDto.getId() != null) {
            return ResponseEntity.badRequest().header("error", "A new skill cannot already have an ID").build();
        }
        
        SkillDto result = skillService.create(skillDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * PUT /api/v1/skills/{id} : Update an existing skill
     *
     * @param id the ID of the skill to update
     * @param skillDto the skill to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated skill
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing skill", description = "Update an existing skill")
    public ResponseEntity<SkillDto> updateSkill(
            @PathVariable Long id,
            @Valid @RequestBody SkillDto skillDto) {
        log.debug("REST request to update Skill : {}, {}", id, skillDto);
        
        if (skillDto.getId() == null) {
            skillDto.setId(id);
        } else if (!id.equals(skillDto.getId())) {
            return ResponseEntity.badRequest().header("error", "ID in path and in body must match").build();
        }
        
        SkillDto result = skillService.update(id, skillDto);
        return ResponseEntity.ok(result);
    }

    /**
     * DELETE /api/v1/skills/{id} : Delete a skill
     *
     * @param id the ID of the skill to delete
     * @return the ResponseEntity with status 204 (NO_CONTENT)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a skill", description = "Delete a skill")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id) {
        log.debug("REST request to delete Skill : {}", id);
        skillService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/skills/search : Search skills by name
     *
     * @param query the search term
     * @param page the page number
     * @param size the page size
     * @return the ResponseEntity with status 200 (OK) and the list of skills in body
     */
    @GetMapping("/search")
    @Operation(summary = "Search skills by name", description = "Search skills by name")
    public ResponseEntity<Page<SkillDto>> searchSkills(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.debug("REST request to search skills by name : {}", query);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<SkillDto> skills = skillService.searchByName(query, pageable);
        return ResponseEntity.ok(skills);
    }
} 
