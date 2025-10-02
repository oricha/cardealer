package com.crashedcarsales.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class ImageMetadata {

    private UUID id;
    private String filename;
    private String originalName;
    private String contentType;
    private Long fileSize;
    private Integer width;
    private Integer height;
    private String entityType;
    private UUID entityId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime uploadedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastModified;

    // Processing information
    private Map<String, String> processedSizes;
    private Boolean isOptimized;
    private String compressionRatio;

    // Storage information
    private String storagePath;
    private String cdnUrl;
    private Boolean isPublic;

    // Constructors
    public ImageMetadata() {}

    public ImageMetadata(UUID id, String filename, String contentType, Long fileSize,
                        Integer width, Integer height, String entityType, UUID entityId) {
        this.id = id;
        this.filename = filename;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.width = width;
        this.height = height;
        this.entityType = entityType;
        this.entityId = entityId;
        this.uploadedAt = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public Map<String, String> getProcessedSizes() {
        return processedSizes;
    }

    public void setProcessedSizes(Map<String, String> processedSizes) {
        this.processedSizes = processedSizes;
    }

    public Boolean getIsOptimized() {
        return isOptimized;
    }

    public void setIsOptimized(Boolean isOptimized) {
        this.isOptimized = isOptimized;
    }

    public String getCompressionRatio() {
        return compressionRatio;
    }

    public void setCompressionRatio(String compressionRatio) {
        this.compressionRatio = compressionRatio;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getCdnUrl() {
        return cdnUrl;
    }

    public void setCdnUrl(String cdnUrl) {
        this.cdnUrl = cdnUrl;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    @Override
    public String toString() {
        return "ImageMetadata{" +
                "id=" + id +
                ", filename='" + filename + '\'' +
                ", originalName='" + originalName + '\'' +
                ", contentType='" + contentType + '\'' +
                ", fileSize=" + fileSize +
                ", width=" + width +
                ", height=" + height +
                ", entityType='" + entityType + '\'' +
                ", entityId=" + entityId +
                ", uploadedAt=" + uploadedAt +
                ", lastModified=" + lastModified +
                ", processedSizes=" + processedSizes +
                ", isOptimized=" + isOptimized +
                ", compressionRatio='" + compressionRatio + '\'' +
                ", storagePath='" + storagePath + '\'' +
                ", cdnUrl='" + cdnUrl + '\'' +
                ", isPublic=" + isPublic +
                '}';
    }
}