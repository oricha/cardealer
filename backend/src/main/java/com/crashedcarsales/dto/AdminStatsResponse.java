package com.crashedcarsales.dto;

import java.time.LocalDateTime;

public class AdminStatsResponse {
    private Long totalUsers;
    private Long activeUsers;
    private Long totalDealers;
    private Long activeDealers;
    private Long totalCars;
    private Long activeCars;
    private Long totalFavorites;
    private Double averageCarsPerDealer;
    private LocalDateTime lastUpdated;

    public AdminStatsResponse() {
        this.lastUpdated = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(Long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public Long getTotalDealers() {
        return totalDealers;
    }

    public void setTotalDealers(Long totalDealers) {
        this.totalDealers = totalDealers;
    }

    public Long getActiveDealers() {
        return activeDealers;
    }

    public void setActiveDealers(Long activeDealers) {
        this.activeDealers = activeDealers;
    }

    public Long getTotalCars() {
        return totalCars;
    }

    public void setTotalCars(Long totalCars) {
        this.totalCars = totalCars;
    }

    public Long getActiveCars() {
        return activeCars;
    }

    public void setActiveCars(Long activeCars) {
        this.activeCars = activeCars;
    }

    public Long getTotalFavorites() {
        return totalFavorites;
    }

    public void setTotalFavorites(Long totalFavorites) {
        this.totalFavorites = totalFavorites;
    }

    public Double getAverageCarsPerDealer() {
        return averageCarsPerDealer;
    }

    public void setAverageCarsPerDealer(Double averageCarsPerDealer) {
        this.averageCarsPerDealer = averageCarsPerDealer;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}