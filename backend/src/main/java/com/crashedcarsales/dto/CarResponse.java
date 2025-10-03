package com.crashedcarsales.dto;

import com.crashedcarsales.entity.Car;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CarResponse {

    private UUID id;
    private String make;
    private String model;
    private Integer year;
    private Car.FuelType fuelType;
    private Car.Transmission transmission;
    private Car.VehicleType vehicleType;
    private Car.Condition condition;
    private BigDecimal price;
    private Integer mileage;
    private String description;
    private Boolean isFeatured;
    private Boolean isActive;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // Dealer information
    private UUID dealerId;
    private String dealerName;

    // Features
    private Boolean airbags;
    private Boolean absBrakes;
    private Boolean airConditioning;
    private Boolean powerSteering;
    private Boolean centralLocking;
    private Boolean electricWindows;

    // Images
    private List<CarImageResponse> images;

    // Constructors
    public CarResponse() {}

    // Static factory method for easy creation from entity
    public static CarResponse fromEntity(Car car) {
        CarResponse response = new CarResponse();
        response.setId(car.getId());
        response.setMake(car.getMake());
        response.setModel(car.getModel());
        response.setYear(car.getYear());
        response.setFuelType(car.getFuelType());
        response.setTransmission(car.getTransmission());
        response.setVehicleType(car.getVehicleType());
        response.setCondition(car.getCondition());
        response.setPrice(car.getPrice());
        response.setMileage(car.getMileage());
        response.setDescription(car.getDescription());
        response.setIsFeatured(car.getIsFeatured());
        response.setIsActive(car.getIsActive());
        response.setCreatedAt(car.getCreatedAt());
        response.setUpdatedAt(car.getUpdatedAt());

        // Set dealer information
        if (car.getDealer() != null) {
            response.setDealerId(car.getDealer().getId());
            response.setDealerName(car.getDealer().getName());
        }

        // Set features
        if (car.getFeatures() != null) {
            response.setAirbags(car.getFeatures().getAirbags());
            response.setAbsBrakes(car.getFeatures().getAbsBrakes());
            response.setAirConditioning(car.getFeatures().getAirConditioning());
            response.setPowerSteering(car.getFeatures().getPowerSteering());
            response.setCentralLocking(car.getFeatures().getCentralLocking());
            response.setElectricWindows(car.getFeatures().getElectricWindows());
        }

        // Set images
        if (car.getImages() != null) {
            response.setImages(car.getImages().stream()
                .map(CarImageResponse::fromEntity)
                .collect(Collectors.toList()));
        }

        return response;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Car.FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(Car.FuelType fuelType) {
        this.fuelType = fuelType;
    }

    public Car.Transmission getTransmission() {
        return transmission;
    }

    public void setTransmission(Car.Transmission transmission) {
        this.transmission = transmission;
    }

    public Car.VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(Car.VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Car.Condition getCondition() {
        return condition;
    }

    public void setCondition(Car.Condition condition) {
        this.condition = condition;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UUID getDealerId() {
        return dealerId;
    }

    public void setDealerId(UUID dealerId) {
        this.dealerId = dealerId;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public Boolean getAirbags() {
        return airbags;
    }

    public void setAirbags(Boolean airbags) {
        this.airbags = airbags;
    }

    public Boolean getAbsBrakes() {
        return absBrakes;
    }

    public void setAbsBrakes(Boolean absBrakes) {
        this.absBrakes = absBrakes;
    }

    public Boolean getAirConditioning() {
        return airConditioning;
    }

    public void setAirConditioning(Boolean airConditioning) {
        this.airConditioning = airConditioning;
    }

    public Boolean getPowerSteering() {
        return powerSteering;
    }

    public void setPowerSteering(Boolean powerSteering) {
        this.powerSteering = powerSteering;
    }

    public Boolean getCentralLocking() {
        return centralLocking;
    }

    public void setCentralLocking(Boolean centralLocking) {
        this.centralLocking = centralLocking;
    }

    public Boolean getElectricWindows() {
        return electricWindows;
    }

    public void setElectricWindows(Boolean electricWindows) {
        this.electricWindows = electricWindows;
    }

    public List<CarImageResponse> getImages() {
        return images;
    }

    public void setImages(List<CarImageResponse> images) {
        this.images = images;
    }

    // Inner class for car images
    public static class CarImageResponse {
        private UUID id;
        private String imageUrl;
        private String altText;
        private Integer displayOrder;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;

        public static CarImageResponse fromEntity(com.crashedcarsales.entity.CarImage carImage) {
            CarImageResponse response = new CarImageResponse();
            response.setId(carImage.getId());
            response.setImageUrl(carImage.getImageUrl());
            response.setAltText(carImage.getAltText());
            response.setDisplayOrder(carImage.getDisplayOrder());
            response.setCreatedAt(carImage.getCreatedAt());
            return response;
        }

        // Getters and Setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public String getAltText() { return altText; }
        public void setAltText(String altText) { this.altText = altText; }
        public Integer getDisplayOrder() { return displayOrder; }
        public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }

    @Override
    public String toString() {
        return "CarResponse{" +
                "id=" + id +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", fuelType=" + fuelType +
                ", transmission=" + transmission +
                ", vehicleType=" + vehicleType +
                ", condition=" + condition +
                ", price=" + price +
                ", mileage=" + mileage +
                ", description='" + description + '\'' +
                ", isFeatured=" + isFeatured +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", dealerId=" + dealerId +
                ", dealerName='" + dealerName + '\'' +
                ", airbags=" + airbags +
                ", absBrakes=" + absBrakes +
                ", airConditioning=" + airConditioning +
                ", powerSteering=" + powerSteering +
                ", centralLocking=" + centralLocking +
                ", electricWindows=" + electricWindows +
                ", images=" + images +
                '}';
    }
}