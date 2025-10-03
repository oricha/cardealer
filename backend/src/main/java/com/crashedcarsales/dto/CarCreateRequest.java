package com.crashedcarsales.dto;

import com.crashedcarsales.entity.Car;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public class CarCreateRequest {

    @NotBlank(message = "Make is mandatory")
    @Size(max = 100, message = "Make cannot exceed 100 characters")
    private String make;

    @NotBlank(message = "Model is mandatory")
    @Size(max = 100, message = "Model cannot exceed 100 characters")
    private String model;

    @NotNull(message = "Year is mandatory")
    @Min(value = 1886, message = "Year must be valid")
    @Max(value = 2030, message = "Year cannot be in the future")
    private Integer year;

    @NotNull(message = "Fuel type is mandatory")
    private Car.FuelType fuelType;

    @NotNull(message = "Transmission is mandatory")
    private Car.Transmission transmission;

    @NotNull(message = "Vehicle type is mandatory")
    private Car.VehicleType vehicleType;

    @NotNull(message = "Condition is mandatory")
    private Car.Condition condition;

    @NotNull(message = "Price is mandatory")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price must have at most 8 digits before decimal and 2 after")
    private BigDecimal price;

    @Min(value = 0, message = "Mileage cannot be negative")
    @Max(value = 1000000, message = "Mileage seems too high")
    private Integer mileage;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    private Boolean isFeatured = false;

    // Car features (optional)
    private Boolean airbags = false;
    private Boolean absBrakes = false;
    private Boolean airConditioning = false;
    private Boolean powerSteering = false;
    private Boolean centralLocking = false;
    private Boolean electricWindows = false;

    // Image URLs (optional)
    private List<String> imageUrls;

    // Constructors
    public CarCreateRequest() {}

    // Getters and Setters
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

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @Override
    public String toString() {
        return "CarCreateRequest{" +
                "make='" + make + '\'' +
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
                ", airbags=" + airbags +
                ", absBrakes=" + absBrakes +
                ", airConditioning=" + airConditioning +
                ", powerSteering=" + powerSteering +
                ", centralLocking=" + centralLocking +
                ", electricWindows=" + electricWindows +
                ", imageUrls=" + imageUrls +
                '}';
    }
}