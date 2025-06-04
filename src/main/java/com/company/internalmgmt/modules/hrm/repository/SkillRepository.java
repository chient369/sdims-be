package com.company.internalmgmt.modules.hrm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.company.internalmgmt.modules.hrm.model.Skill;

/**
 * Repository for managing Skill entities
 */
@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    /**
     * Find all skills belonging to a specific category
     *
     * @param categoryId the category ID
     * @return list of skills in the specified category
     */
    List<Skill> findByCategoryId(Long categoryId);

    /**
     * Find all skills belonging to a specific category with pagination
     *
     * @param categoryId the category ID
     * @param pageable pagination information
     * @return page of skills in the specified category
     */
    Page<Skill> findByCategoryId(Long categoryId, Pageable pageable);

    /**
     * Find a skill by its name within a specific category
     *
     * @param name the skill name
     * @param categoryId the category ID
     * @return the skill if found
     */
    Optional<Skill> findByNameIgnoreCaseAndCategoryId(String name, Long categoryId);

    /**
     * Check if a skill with the given name exists within a specific category
     *
     * @param name the skill name
     * @param categoryId the category ID
     * @return true if a skill with the given name exists in the specified category
     */
    boolean existsByNameIgnoreCaseAndCategoryId(String name, Long categoryId);

    /**
     * Search skills by name
     *
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return page of skills matching the search term
     */
    @Query("SELECT s FROM Skill s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Skill> searchByName(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Đếm số lượng skill theo category
     * @param categoryId id của category
     * @return số lượng skill
     */
    long countByCategoryId(Long categoryId);

    /**
     * Tìm kiếm skill theo categoryId và tên (không phân biệt hoa thường, có phân trang)
     */
    Page<Skill> findByCategoryIdAndNameContainingIgnoreCase(Long categoryId, String name, Pageable pageable);

    /**
     * Tìm kiếm skill theo tên (không phân biệt hoa thường, có phân trang)
     */
    Page<Skill> findByNameContainingIgnoreCase(String name, Pageable pageable);
} 
