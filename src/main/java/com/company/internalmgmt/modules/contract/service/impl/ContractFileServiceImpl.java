package com.company.internalmgmt.modules.contract.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.company.internalmgmt.common.exception.BadRequestException;
import com.company.internalmgmt.common.exception.ResourceNotFoundException;
import com.company.internalmgmt.common.exception.SystemException;
import com.company.internalmgmt.modules.admin.model.User;
import com.company.internalmgmt.modules.admin.repository.UserRepository;
import com.company.internalmgmt.modules.contract.dto.ContractFileDTO;
import com.company.internalmgmt.modules.contract.dto.mapper.ContractMapper;
import com.company.internalmgmt.modules.contract.model.Contract;
import com.company.internalmgmt.modules.contract.model.ContractFile;
import com.company.internalmgmt.modules.contract.repository.ContractFileRepository;
import com.company.internalmgmt.modules.contract.repository.ContractRepository;
import com.company.internalmgmt.modules.contract.service.ContractFileService;

/**
 * Implementation of the ContractFileService interface
 */
@Service
public class ContractFileServiceImpl implements ContractFileService {

    @Value("${app.file.upload-dir:uploads/contracts}")
    private String uploadDir;
    
    @Autowired
    private ContractFileRepository contractFileRepository;
    
    @Autowired
    private ContractRepository contractRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<ContractFileDTO> getContractFilesByContractId(Long contractId) {
        List<ContractFile> contractFiles = contractFileRepository.findByContractId(contractId);
        
        return contractFiles.stream()
                .map(ContractMapper::toContractFileDto)
                .collect(Collectors.toList());
    }

    @Override
    public ContractFileDTO getContractFileById(Long id) {
        ContractFile contractFile = contractFileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract file not found with id: " + id));
        
        return ContractMapper.toContractFileDto(contractFile);
    }

    @Override
    @Transactional
    public ContractFileDTO uploadContractFile(Long contractId, MultipartFile file, String description, Long currentUserId) {
        // Validate contract
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + contractId));
        
        // Validate user
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUserId));
        
        // Validate file
        if (file.isEmpty()) {
            throw new BadRequestException("File cannot be empty");
        }
        
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        if (originalFilename.contains("..")) {
            throw new BadRequestException("Filename contains invalid path sequence: " + originalFilename);
        }
        
        // Generate unique file name
        String fileExtension = getFileExtension(originalFilename);
        String newFilename = generateUniqueFilename(fileExtension);
        
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new SystemException("Failed to create directory: " + uploadPath, e);
            }
        }
        
        // Save file to disk
        Path filePath = uploadPath.resolve(newFilename);
        try {
            Files.copy(file.getInputStream(), filePath);
        } catch (IOException e) {
            throw new SystemException("Failed to store file: " + originalFilename, e);
        }
        
        // Save file record to database
        ContractFile contractFile = new ContractFile();
        contractFile.setContract(contract);
        contractFile.setFileName(originalFilename);
        contractFile.setStoredFileName(newFilename);
        contractFile.setFileType(file.getContentType());
        contractFile.setFileSize(file.getSize());
        contractFile.setFilePath(uploadDir + File.separator + newFilename);
        contractFile.setUploadedAt(LocalDateTime.now());
        contractFile.setUploadedBy(currentUser);
        contractFile.setDescription(description);
        
        contractFileRepository.save(contractFile);
        
        return ContractMapper.toContractFileDto(contractFile);
    }

    @Override
    @Transactional
    public void deleteContractFile(Long id, Long currentUserId) {
        // Validate user
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUserId));
        
        // Get file record
        ContractFile contractFile = contractFileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract file not found with id: " + id));
        
        // Delete file from disk
        String filePath = contractFile.getFilePath();
        if (filePath != null) {
            try {
                Path path = Paths.get(filePath);
                if (Files.exists(path)) {
                    Files.delete(path);
                }
            } catch (IOException e) {
                // Log error but continue to delete database record
                System.err.println("Failed to delete file: " + filePath + " - " + e.getMessage());
            }
        }
        
        // Delete record from database
        contractFileRepository.delete(contractFile);
    }

    @Override
    public List<ContractFileDTO> searchContractFilesByFileName(String fileName) {
        List<ContractFile> contractFiles = contractFileRepository.findByFileNameContainingIgnoreCase(fileName);
        
        return contractFiles.stream()
                .map(ContractMapper::toContractFileDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ContractFileDTO> getContractFilesByFileType(String fileType) {
        List<ContractFile> contractFiles = contractFileRepository.findByFileType(fileType);
        
        return contractFiles.stream()
                .map(ContractMapper::toContractFileDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ContractFileDTO> getContractFilesByUploadedById(Long uploadedById) {
        List<ContractFile> contractFiles = contractFileRepository.findByUploadedById(uploadedById);
        
        return contractFiles.stream()
                .map(ContractMapper::toContractFileDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ContractFileDTO> searchContractFilesByFileNamePaged(String fileName, Pageable pageable) {
        // Get contract files by file name
        Page<ContractFile> filesPage = contractFileRepository.findByFileNameContainingIgnoreCase(fileName, pageable);
        
        // Convert to DTOs
        return filesPage.map(ContractMapper::toContractFileDto);
    }

    @Override
    public Page<ContractFileDTO> getContractFilesByFileTypePaged(String fileType, Pageable pageable) {
        // Get contract files by file type
        Page<ContractFile> filesPage = contractFileRepository.findByFileType(fileType, pageable);
        
        // Convert to DTOs
        return filesPage.map(ContractMapper::toContractFileDto);
    }
    
    /**
     * Get file extension from filename
     * 
     * @param filename the filename
     * @return the file extension
     */
    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1);
        }
        return "";
    }
    
    /**
     * Generate a unique filename
     * 
     * @param extension the file extension
     * @return the generated filename
     */
    private String generateUniqueFilename(String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        
        return "contract_" + timestamp + "_" + uuid + (extension.isEmpty() ? "" : "." + extension);
    }
} 