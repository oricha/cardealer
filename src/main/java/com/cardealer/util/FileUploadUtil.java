package com.cardealer.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class FileUploadUtil {

    @Value("${file.upload-dir:uploads/cars}")
    private String uploadDir;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
        "image/jpeg",
        "image/png",
        "image/webp"
    );
    private static final List<String> ALLOWED_EXTENSIONS = List.of(
        ".jpg", ".jpeg", ".png", ".webp"
    );

    /**
     * Save a single file
     */
    public String saveFile(MultipartFile file) throws IOException {
        log.info("Saving file: {}", file.getOriginalFilename());
        
        // Validate file
        validateFile(file);
        
        // Generate unique filename
        String fileName = generateUniqueFileName(file.getOriginalFilename());
        
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            log.info("Created upload directory: {}", uploadPath);
        }
        
        // Save file
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        log.info("File saved successfully: {}", fileName);
        return fileName;
    }

    /**
     * Save multiple files
     */
    public List<String> saveFiles(List<MultipartFile> files) throws IOException {
        log.info("Saving {} files", files.size());
        
        List<String> savedFileNames = new ArrayList<>();
        
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String fileName = saveFile(file);
                savedFileNames.add(fileName);
            }
        }
        
        log.info("Saved {} files successfully", savedFileNames.size());
        return savedFileNames;
    }

    /**
     * Delete a file
     */
    public void deleteFile(String fileName) throws IOException {
        log.info("Deleting file: {}", fileName);
        
        if (fileName == null || fileName.isEmpty()) {
            log.warn("Attempted to delete file with null or empty filename");
            return;
        }
        
        Path filePath = Paths.get(uploadDir).resolve(fileName);
        
        if (Files.exists(filePath)) {
            Files.deleteIfExists(filePath);
            log.info("File deleted successfully: {}", fileName);
        } else {
            log.warn("File not found for deletion: {}", fileName);
        }
    }

    /**
     * Delete multiple files
     */
    public void deleteFiles(List<String> fileNames) {
        log.info("Deleting {} files", fileNames.size());
        
        for (String fileName : fileNames) {
            try {
                deleteFile(fileName);
            } catch (IOException e) {
                log.error("Error deleting file: {}", fileName, e);
            }
        }
    }

    /**
     * Validate file
     */
    private void validateFile(MultipartFile file) {
        // Check if file is empty
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }
        
        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                String.format("El archivo es demasiado grande. Tamaño máximo: %d MB", 
                    MAX_FILE_SIZE / (1024 * 1024))
            );
        }
        
        // Check content type
        String contentType = file.getContentType();
        if (!isValidContentType(contentType)) {
            throw new IllegalArgumentException(
                "Tipo de archivo no válido. Solo se permiten imágenes JPEG, PNG y WebP"
            );
        }
        
        // Check file extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isValidExtension(originalFilename)) {
            throw new IllegalArgumentException(
                "Extensión de archivo no válida. Solo se permiten: " + ALLOWED_EXTENSIONS
            );
        }
        
        log.debug("File validation passed for: {}", originalFilename);
    }

    /**
     * Check if content type is valid
     */
    private boolean isValidContentType(String contentType) {
        return contentType != null && ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase());
    }

    /**
     * Check if file extension is valid
     */
    private boolean isValidExtension(String filename) {
        String lowerFilename = filename.toLowerCase();
        return ALLOWED_EXTENSIONS.stream()
            .anyMatch(lowerFilename::endsWith);
    }

    /**
     * Generate unique filename
     */
    private String generateUniqueFileName(String originalFilename) {
        String cleanFilename = StringUtils.cleanPath(originalFilename);
        String extension = "";
        
        int lastDotIndex = cleanFilename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = cleanFilename.substring(lastDotIndex);
        }
        
        String uniqueId = UUID.randomUUID().toString();
        return uniqueId + extension;
    }

    /**
     * Get upload directory path
     */
    public String getUploadDir() {
        return uploadDir;
    }

    /**
     * Get full file path
     */
    public Path getFilePath(String fileName) {
        return Paths.get(uploadDir).resolve(fileName);
    }

    /**
     * Check if file exists
     */
    public boolean fileExists(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }
        Path filePath = getFilePath(fileName);
        return Files.exists(filePath);
    }
}