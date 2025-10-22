package com.crashedcarsales.dto;

/**
 * Car information record using Java 14+ Record feature
 * Demonstrates immutable data transfer objects with reduced boilerplate
 */
public record CarInfo(
    String make,
    String model,
    int year,
    double price,
    String condition
) {
    // Compact constructor with validation
    public CarInfo {
        if (year < 1900 || year > 2030) {
            throw new IllegalArgumentException("Invalid year: " + year);
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        // Normalize condition
        condition = condition != null ? condition.toUpperCase() : "UNKNOWN";
    }
    
    // Additional methods
    public String getDisplayName() {
        return year + " " + make + " " + model;
    }
    
    public boolean isVintage() {
        return year < 1980;
    }
    
    public boolean isAffordable(double budget) {
        return price <= budget;
    }
}