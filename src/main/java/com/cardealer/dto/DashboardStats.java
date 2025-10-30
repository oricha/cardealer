package com.cardealer.dto;

import com.cardealer.model.Car;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {
    
    private Long activeListings;
    private Long totalViews;
    private Long totalListings;
    private List<Car> recentListings;
}