package com.crashedcarsales.dto;

import com.crashedcarsales.entity.Car;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;

public class CarSearchRequest {

    private String make;
    private String model;
    private Car.FuelType fuelType;
    private Car.Transmission transmission;
    private Car.VehicleType vehicleType;
    private Car.Condition condition;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer minYear;
    private Integer maxYear;
    private Integer maxMileage;
    private Boolean featuredOnly = false;

    // Pagination
    private Integer page = 0;
    private Integer size = 20;

    // Sorting
    private String sortBy = "createdAt";
    private String sortDirection = "DESC";

    // Constructors
    public CarSearchRequest() {}

    // Static factory method for creating Pageable
    public Pageable toPageable() {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        return PageRequest.of(page, size, sort);
    }

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

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Integer getMinYear() {
        return minYear;
    }

    public void setMinYear(Integer minYear) {
        this.minYear = minYear;
    }

    public Integer getMaxYear() {
        return maxYear;
    }

    public void setMaxYear(Integer maxYear) {
        this.maxYear = maxYear;
    }

    public Integer getMaxMileage() {
        return maxMileage;
    }

    public void setMaxMileage(Integer maxMileage) {
        this.maxMileage = maxMileage;
    }

    public Boolean getFeaturedOnly() {
        return featuredOnly;
    }

    public void setFeaturedOnly(Boolean featuredOnly) {
        this.featuredOnly = featuredOnly;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    @Override
    public String toString() {
        return "CarSearchRequest{" +
                "make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", fuelType=" + fuelType +
                ", transmission=" + transmission +
                ", vehicleType=" + vehicleType +
                ", condition=" + condition +
                ", minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                ", minYear=" + minYear +
                ", maxYear=" + maxYear +
                ", maxMileage=" + maxMileage +
                ", featuredOnly=" + featuredOnly +
                ", page=" + page +
                ", size=" + size +
                ", sortBy='" + sortBy + '\'' +
                ", sortDirection='" + sortDirection + '\'' +
                '}';
    }
}