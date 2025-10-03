package com.crashedcarsales.dto;

import com.crashedcarsales.entity.Favorites;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FavoritesResponse {

    private UUID id;
    private UUID carId;
    private String carMake;
    private String carModel;
    private Integer carYear;
    private String carCondition;
    private String carFuelType;
    private String carTransmission;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    // Constructors
    public FavoritesResponse() {}

    public FavoritesResponse(UUID id, UUID carId, String carMake, String carModel,
                           Integer carYear, String carCondition, String carFuelType,
                           String carTransmission, LocalDateTime createdAt) {
        this.id = id;
        this.carId = carId;
        this.carMake = carMake;
        this.carModel = carModel;
        this.carYear = carYear;
        this.carCondition = carCondition;
        this.carFuelType = carFuelType;
        this.carTransmission = carTransmission;
        this.createdAt = createdAt;
    }

    // Static factory method for easy creation from entity
    public static FavoritesResponse fromEntity(Favorites favorites) {
        return new FavoritesResponse(
            favorites.getId(),
            favorites.getCar().getId(),
            favorites.getCar().getMake(),
            favorites.getCar().getModel(),
            favorites.getCar().getYear(),
            favorites.getCar().getCondition() != null ? favorites.getCar().getCondition().toString() : null,
            favorites.getCar().getFuelType() != null ? favorites.getCar().getFuelType().toString() : null,
            favorites.getCar().getTransmission() != null ? favorites.getCar().getTransmission().toString() : null,
            favorites.getCreatedAt()
        );
    }

    // Static factory method for creating list from entities
    public static List<FavoritesResponse> fromEntities(List<Favorites> favorites) {
        return favorites.stream()
            .map(FavoritesResponse::fromEntity)
            .collect(Collectors.toList());
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCarId() {
        return carId;
    }

    public void setCarId(UUID carId) {
        this.carId = carId;
    }

    public String getCarMake() {
        return carMake;
    }

    public void setCarMake(String carMake) {
        this.carMake = carMake;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public Integer getCarYear() {
        return carYear;
    }

    public void setCarYear(Integer carYear) {
        this.carYear = carYear;
    }

    public String getCarCondition() {
        return carCondition;
    }

    public void setCarCondition(String carCondition) {
        this.carCondition = carCondition;
    }

    public String getCarFuelType() {
        return carFuelType;
    }

    public void setCarFuelType(String carFuelType) {
        this.carFuelType = carFuelType;
    }

    public String getCarTransmission() {
        return carTransmission;
    }

    public void setCarTransmission(String carTransmission) {
        this.carTransmission = carTransmission;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "FavoritesResponse{" +
                "id=" + id +
                ", carId=" + carId +
                ", carMake='" + carMake + '\'' +
                ", carModel='" + carModel + '\'' +
                ", carYear=" + carYear +
                ", carCondition='" + carCondition + '\'' +
                ", carFuelType='" + carFuelType + '\'' +
                ", carTransmission='" + carTransmission + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}