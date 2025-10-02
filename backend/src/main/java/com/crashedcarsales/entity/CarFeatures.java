package com.crashedcarsales.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "car_features")
public class CarFeatures {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false, unique = true)
    private Car car;

    @Column(name = "airbags")
    private Boolean airbags = false;

    @Column(name = "abs_brakes")
    private Boolean absBrakes = false;

    @Column(name = "air_conditioning")
    private Boolean airConditioning = false;

    @Column(name = "power_steering")
    private Boolean powerSteering = false;

    @Column(name = "central_locking")
    private Boolean centralLocking = false;

    @Column(name = "electric_windows")
    private Boolean electricWindows = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Constructors
    public CarFeatures() {
        this.createdAt = LocalDateTime.now();
    }

    public CarFeatures(Car car) {
        this();
        this.car = car;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}