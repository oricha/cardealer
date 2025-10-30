package com.cardealer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarFilterDTO {
    
    private List<String> brands;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String transmission;
    private String fuelType;
    private String bodyType;
    private String condition;
    private List<String> features;
    private String sortBy;  // price_asc, price_desc, date_desc, mileage_asc, year_desc
    private String searchText;  // For text search in brand, model, description
}