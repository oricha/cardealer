package com.crashedcarsales.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class FavoritesRequest {

    @NotNull(message = "Car ID is mandatory")
    private UUID carId;

    // Constructors
    public FavoritesRequest() {}

    public FavoritesRequest(UUID carId) {
        this.carId = carId;
    }

    // Getters and Setters
    public UUID getCarId() {
        return carId;
    }

    public void setCarId(UUID carId) {
        this.carId = carId;
    }

    @Override
    public String toString() {
        return "FavoritesRequest{" +
                "carId=" + carId +
                '}';
    }
}