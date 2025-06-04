package com.company.internalmgmt.modules.contract.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.company.internalmgmt.modules.contract.dto.ContractFileDTO;

/**
 * Service interface for managing contract files
 */
public interface ContractFileService {
    
    /**
     * Get contract files by contract ID
     * 
     * @param contractId the contract ID
     * @return list of contract file DTOs
     */
    List<ContractFileDTO> getContractFilesByContractId(Long contractId);
    
    /**
     * Get contract file by ID
     * 
     * @param id the contract file ID
     * @return the contract file DTO
     */
    ContractFileDTO getContractFileById(Long id);
    
    /**
     * Upload a file for a contract
     * 
     * @param contractId the contract ID
     * @param file the file to upload
     * @param description file description
     * @param currentUserId the current user ID
     * @return the uploaded contract file DTO
     */
    ContractFileDTO uploadContractFile(Long contractId, MultipartFile file, String description, Long currentUserId);
    
    /**
     * Delete a contract file
     * 
     * @param id the contract file ID
     * @param currentUserId the current user ID
     */
    void deleteContractFile(Long id, Long currentUserId);
    
    /**
     * Search contract files by file name
     * 
     * @param fileName the file name to search
     * @return list of contract file DTOs
     */
    List<ContractFileDTO> searchContractFilesByFileName(String fileName);
    
    /**
     * Search contract files by file name with pagination
     * 
     * @param fileName the file name to search
     * @param pageable the pageable information
     * @return page of contract file DTOs
     */
    Page<ContractFileDTO> searchContractFilesByFileNamePaged(String fileName, Pageable pageable);
    
    /**
     * Get contract files by file type
     * 
     * @param fileType the file type
     * @return list of contract file DTOs
     */
    List<ContractFileDTO> getContractFilesByFileType(String fileType);
    
    /**
     * Get contract files by file type with pagination
     * 
     * @param fileType the file type
     * @param pageable the pageable information
     * @return page of contract file DTOs
     */
    Page<ContractFileDTO> getContractFilesByFileTypePaged(String fileType, Pageable pageable);
    
    /**
     * Get contract files uploaded by a specific user
     * 
     * @param uploadedById the uploaded by user ID
     * @return list of contract file DTOs
     */
    List<ContractFileDTO> getContractFilesByUploadedById(Long uploadedById);
} 