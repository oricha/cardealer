package com.crashedcarsales.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ImageUploadRequest {

    @NotNull(message = "Entity type is mandatory")
    private EntityType entityType;

    @NotBlank(message = "Entity ID is mandatory")
    @Size(max = 36, message = "Entity ID cannot exceed 36 characters")
    private String entityId;

    @Size(max = 255, message = "Alt text cannot exceed 255 characters")
    private String altText;

    @NotNull(message = "Primary image flag is mandatory")
    private Boolean isPrimary = false;

    // Constructors
    public ImageUploadRequest() {}

    public ImageUploadRequest(EntityType entityType, String entityId) {
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public ImageUploadRequest(EntityType entityType, String entityId, String altText, Boolean isPrimary) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.altText = altText;
        this.isPrimary = isPrimary;
    }

    // Getters and Setters
    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    // Enum for entity types that can have images
    public enum EntityType {
        CAR,
        DEALER,
        USER
    }

    @Override
    public String toString() {
        return "ImageUploadRequest{" +
                "entityType=" + entityType +
                ", entityId='" + entityId + '\'' +
                ", altText='" + altText + '\'' +
                ", isPrimary=" + isPrimary +
                '}';
    }
}