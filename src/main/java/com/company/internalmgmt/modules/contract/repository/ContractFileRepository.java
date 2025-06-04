package com.company.internalmgmt.modules.contract.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.company.internalmgmt.modules.contract.model.ContractFile;

/**
 * Repository for ContractFile entity
 */
@Repository
public interface ContractFileRepository extends JpaRepository<ContractFile, Long> {
    
    /**
     * Find contract files by contract ID
     * 
     * @param contractId the contract ID
     * @return list of contract files
     */
    List<ContractFile> findByContractId(Long contractId);
    
    /**
     * Find contract files by uploaded by ID
     * 
     * @param uploadedById the uploaded by ID
     * @return list of contract files
     */
    List<ContractFile> findByUploadedById(Long uploadedById);
    
    /**
     * Delete contract files by contract ID
     * 
     * @param contractId the contract ID
     */
    void deleteByContractId(Long contractId);
    
    /**
     * Find contract files by file name containing the search text
     * 
     * @param fileName the file name to search
     * @return list of contract files
     */
    List<ContractFile> findByFileNameContainingIgnoreCase(String fileName);
    
    /**
     * Find contract files by file name containing the search text with pagination
     * 
     * @param fileName the file name to search
     * @param pageable the pageable information
     * @return page of contract files
     */
    Page<ContractFile> findByFileNameContainingIgnoreCase(String fileName, Pageable pageable);
    
    /**
     * Find contract files by file type
     * 
     * @param fileType the file type
     * @return list of contract files
     */
    List<ContractFile> findByFileType(String fileType);
    
    /**
     * Find contract files by file type with pagination
     * 
     * @param fileType the file type
     * @param pageable the pageable information
     * @return page of contract files
     */
    Page<ContractFile> findByFileType(String fileType, Pageable pageable);
} 