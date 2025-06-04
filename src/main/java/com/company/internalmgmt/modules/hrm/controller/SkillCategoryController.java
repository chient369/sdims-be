package com.company.internalmgmt.modules.hrm.controller;

import com.company.internalmgmt.modules.hrm.dto.SkillCategoryDto;
import com.company.internalmgmt.modules.hrm.dto.SkillCategoryRequest;
import com.company.internalmgmt.modules.hrm.service.SkillCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST controller for managing skill categories
 */
@RestController
@RequestMapping("/api/v1/skill-categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Skill Category Management", description = "APIs for managing skill categories")
public class SkillCategoryController {

    private final SkillCategoryService skillCategoryService;

    /**
     * GET /api/v1/skill-categories : Get all skill categories
     *
     * @return the ResponseEntity with status 200 (OK) and the list of skill categories in body
     */
    @GetMapping
    @Operation(summary = "Get all skill categories", description = "Get all skill categories")
    public ResponseEntity<List<SkillCategoryDto>> getAllSkillCategories() {
        log.debug("REST request to get all SkillCategories");
        List<SkillCategoryDto> categories = skillCategoryService.findAll();
        return ResponseEntity.ok(categories);
    }

    /**
     * GET /api/v1/skill-categories/{id} : Get the "id" skill category
     *
     * @param id the id of the skill category to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the skillCategoryDTO, or with status 404 (Not Found)
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a skill category by ID", description = "Get detailed information about a skill category by ID")
    public ResponseEntity<SkillCategoryDto> getSkillCategoryById(@PathVariable Long id) {
        log.debug("REST request to get SkillCategory : {}", id);
        SkillCategoryDto categoryDto = skillCategoryService.findById(id);
        return ResponseEntity.ok(categoryDto);
    }

    /**
     * POST /api/v1/skill-categories : Create a new skill category
     *
     * @param categoryRequest the skillCategoryRequest to create
     * @return the ResponseEntity with status 201 (Created) and with body the new skillCategoryDTO
     */
    @PostMapping
    @Operation(summary = "Create a new skill category", description = "Create a new skill category")
    public ResponseEntity<SkillCategoryDto> createSkillCategory(@Valid @RequestBody SkillCategoryRequest categoryRequest) {
        log.debug("REST request to save SkillCategory : {}", categoryRequest);
        SkillCategoryDto result = skillCategoryService.create(categoryRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * PUT /api/v1/skill-categories/{id} : Updates an existing skill category
     *
     * @param id the id of the skill category to update
     * @param categoryRequest the skillCategoryRequest to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated skillCategoryDTO
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing skill category", description = "Update an existing skill category")
    public ResponseEntity<SkillCategoryDto> updateSkillCategory(
            @PathVariable Long id,
            @Valid @RequestBody SkillCategoryRequest categoryRequest) {
        log.debug("REST request to update SkillCategory : {}, {}", id, categoryRequest);
        SkillCategoryDto result = skillCategoryService.update(id, categoryRequest);
        return ResponseEntity.ok(result);
    }

    /**
     * DELETE /api/v1/skill-categories/{id} : Delete the "id" skill category
     *
     * @param id the id of the skill category to delete
     * @return the ResponseEntity with status 204 (NO_CONTENT)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a skill category", description = "Delete a skill category")
    public ResponseEntity<Void> deleteSkillCategory(@PathVariable Long id) {
        log.debug("REST request to delete SkillCategory : {}", id);
        skillCategoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 
