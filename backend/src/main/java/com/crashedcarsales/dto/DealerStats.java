package com.crashedcarsales.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class DealerStats {

    private UUID dealerId;
    private String dealerName;
    private Long carsListed;
    private Long carsSold;
    private BigDecimal totalSalesValue;
    private BigDecimal averageSalePrice;

    // Constructors
    public DealerStats() {}

    public DealerStats(UUID dealerId, String dealerName, Long carsListed, Long carsSold, BigDecimal totalSalesValue) {
        this.dealerId = dealerId;
        this.dealerName = dealerName;
        this.carsListed = carsListed;
        this.carsSold = carsSold;
        this.totalSalesValue = totalSalesValue;
        this.averageSalePrice = carsSold != null && carsSold > 0 ?
            totalSalesValue.divide(BigDecimal.valueOf(carsSold), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
    }

    // Static factory method for easy creation from repository data
    public static DealerStats fromRepositoryData(Object[] data) {
        if (data == null || data.length < 5) {
            return new DealerStats();
        }

        return new DealerStats(
            (UUID) data[0],
            (String) data[1],
            data[2] != null ? ((Number) data[2]).longValue() : 0L,
            data[3] != null ? ((Number) data[3]).longValue() : 0L,
            data[4] != null ? (BigDecimal) data[4] : BigDecimal.ZERO
        );
    }

    // Getters and Setters
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

    public Long getCarsListed() {
        return carsListed;
    }

    public void setCarsListed(Long carsListed) {
        this.carsListed = carsListed;
    }

    public Long getCarsSold() {
        return carsSold;
    }

    public void setCarsSold(Long carsSold) {
        this.carsSold = carsSold;
    }

    public BigDecimal getTotalSalesValue() {
        return totalSalesValue;
    }

    public void setTotalSalesValue(BigDecimal totalSalesValue) {
        this.totalSalesValue = totalSalesValue;
    }

    public BigDecimal getAverageSalePrice() {
        return averageSalePrice;
    }

    public void setAverageSalePrice(BigDecimal averageSalePrice) {
        this.averageSalePrice = averageSalePrice;
    }

    @Override
    public String toString() {
        return "DealerStats{" +
                "dealerId=" + dealerId +
                ", dealerName='" + dealerName + '\'' +
                ", carsListed=" + carsListed +
                ", carsSold=" + carsSold +
                ", totalSalesValue=" + totalSalesValue +
                ", averageSalePrice=" + averageSalePrice +
                '}';
    }
}