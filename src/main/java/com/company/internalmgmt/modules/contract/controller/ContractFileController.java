package com.company.internalmgmt.modules.contract.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.company.internalmgmt.common.dto.ApiResponse;
import com.company.internalmgmt.common.util.SecurityUtils;
import com.company.internalmgmt.modules.contract.dto.ContractFileDTO;
import com.company.internalmgmt.modules.contract.service.ContractFileService;

/**
 * REST controller for managing contract files
 */
@RestController
@RequestMapping("/api/v1/contracts/files")
public class ContractFileController {

    @Autowired
    private ContractFileService contractFileService;
    
    /**
     * GET /api/v1/contracts/files/:id : Get a specific file by ID
     * 
     * @param id the ID of the file to retrieve
     * @return the ResponseEntity with status 200 (OK) and the file in body
     */
    @GetMapping("/{id}")
    //@PreAuthorize("hasAnyAuthority('contract-file:read:all', 'contract-file:read:own', 'contract-file:read:assigned')")
    public ResponseEntity<ApiResponse<ContractFileDTO>> getFileById(@PathVariable Long id) {
        ContractFileDTO file = contractFileService.getContractFileById(id);
        
        return ResponseEntity.ok(ApiResponse.success(file));
    }
    
    /**
     * POST /api/v1/contracts/:contractId/files : Upload a file for a contract
     * 
     * @param contractId the ID of the contract
     * @param file the file to upload
     * @param description file description
     * @param authentication the authentication object
     * @return the ResponseEntity with status 201 (Created) and the uploaded file in body
     */
    @PostMapping(value = "/{contractId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
   // @PreAuthorize("hasAnyAuthority('contract-file:create:all', 'contract-file:create:own')")
    public ResponseEntity<ApiResponse<ContractFileDTO>> uploadFile(
            @PathVariable Long contractId,
            @RequestPart MultipartFile file,
            @RequestParam(required = false) String description,
            Authentication authentication) {
        
        Long currentUserId = SecurityUtils.getCurrentUserId(authentication);
        ContractFileDTO result = contractFileService.uploadContractFile(contractId, file, description, currentUserId);
        
        return new ResponseEntity<>(ApiResponse.success(result), HttpStatus.CREATED);
    }
    
    /**
     * DELETE /api/v1/contracts/files/:id : Delete a file
     * 
     * @param id the ID of the file to delete
     * @param authentication the authentication object
     * @return the ResponseEntity with status 204 (NO_CONTENT)
     */
    @DeleteMapping("/{id}")
    //@PreAuthorize("hasAnyAuthority('contract-file:delete:all', 'contract-file:delete:own')")
    public ResponseEntity<Void> deleteFile(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long currentUserId = SecurityUtils.getCurrentUserId(authentication);
        contractFileService.deleteContractFile(id, currentUserId);
        
        return ResponseEntity.noContent().build();
    }
    
    /**
     * GET /api/v1/contracts/files/search : Search files by name
     * 
     * @param fileName the file name to search
     * @param page page number (1-based, will be converted to 0-based)
     * @param size page size
     * @param sortBy field to sort by
     * @param sortDirection sort direction (asc or desc)
     * @return the ResponseEntity with status 200 (OK) and the paged list of files in body
     */
    @GetMapping("/search")
    //@PreAuthorize("hasAnyAuthority('contract-file:read:all', 'contract-file:read:own', 'contract-file:read:assigned')")
    public ResponseEntity<ApiResponse<Page<ContractFileDTO>>> searchFilesByName(
            @RequestParam String fileName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        // Convert from 1-based page (client) to 0-based page (Spring)
        if (page > 0) {
            page = page - 1;
        }
        
        // Create pageable with sort
        Sort sort = sortDirection.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : 
                Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ContractFileDTO> files = contractFileService.searchContractFilesByFileNamePaged(fileName, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(files));
    }
    
    /**
     * GET /api/v1/contracts/files/type/:fileType : Get files by type
     * 
     * @param fileType the file type
     * @param page page number (1-based, will be converted to 0-based)
     * @param size page size
     * @param sortBy field to sort by
     * @param sortDirection sort direction (asc or desc)
     * @return the ResponseEntity with status 200 (OK) and the paged list of files in body
     */
    @GetMapping("/type/{fileType}")
    //@PreAuthorize("hasAnyAuthority('contract-file:read:all', 'contract-file:read:own', 'contract-file:read:assigned')")
    public ResponseEntity<ApiResponse<Page<ContractFileDTO>>> getFilesByType(
            @PathVariable String fileType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        // Convert from 1-based page (client) to 0-based page (Spring)
        if (page > 0) {
            page = page - 1;
        }
        
        // Create pageable with sort
        Sort sort = sortDirection.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : 
                Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ContractFileDTO> files = contractFileService.getContractFilesByFileTypePaged(fileType, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(files));
    }
} 