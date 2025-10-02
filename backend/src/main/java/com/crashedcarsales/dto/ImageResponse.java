package com.crashedcarsales.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class ImageResponse {

    private UUID id;
    private String imageUrl;
    private String altText;
    private Integer displayOrder;
    private String entityType;
    private UUID entityId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    // Image URLs for different sizes
    private Map<String, String> responsiveUrls;

    // Image metadata
    private Map<String, Object> metadata;

    // Constructors
    public ImageResponse() {}

    public ImageResponse(UUID id, String imageUrl, String altText, Integer displayOrder,
                        String entityType, UUID entityId, LocalDateTime createdAt) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.altText = altText;
        this.displayOrder = displayOrder;
        this.entityType = entityType;
        this.entityId = entityId;
        this.createdAt = createdAt;
    }

    // Static factory method for easy creation
    public static ImageResponse fromEntity(com.crashedcarsales.entity.CarImage carImage) {
        ImageResponse response = new ImageResponse();
        response.setId(carImage.getId());
        response.setImageUrl(carImage.getImageUrl());
        response.setAltText(carImage.getAltText());
        response.setDisplayOrder(carImage.getDisplayOrder());
        response.setEntityType("CAR");
        response.setEntityId(carImage.getCar().getId());
        response.setCreatedAt(carImage.getCreatedAt());
        return response;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Map<String, String> getResponsiveUrls() {
        return responsiveUrls;
    }

    public void setResponsiveUrls(Map<String, String> responsiveUrls) {
        this.responsiveUrls = responsiveUrls;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "ImageResponse{" +
                "id=" + id +
                ", imageUrl='" + imageUrl + '\'' +
                ", altText='" + altText + '\'' +
                ", displayOrder=" + displayOrder +
                ", entityType='" + entityType + '\'' +
                ", entityId=" + entityId +
                ", createdAt=" + createdAt +
                ", responsiveUrls=" + responsiveUrls +
                ", metadata=" + metadata +
                '}';
    }
}