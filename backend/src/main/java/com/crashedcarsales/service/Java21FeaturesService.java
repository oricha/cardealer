package com.crashedcarsales.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Service demonstrating Java 21 features including:
 * - Virtual Threads
 * - Pattern Matching
 * - Switch Expressions
 * - Records
 */
@Service
public class Java21FeaturesService {
    
    private static final Logger logger = LoggerFactory.getLogger(Java21FeaturesService.class);
    
    private final AsyncTaskExecutor virtualThreadExecutor;
    
    public Java21FeaturesService(@Qualifier("virtualThreadExecutor") AsyncTaskExecutor virtualThreadExecutor) {
        this.virtualThreadExecutor = virtualThreadExecutor;
    }
    
    /**
     * Record for car search criteria - Java 14+ feature
     */
    public record CarSearchCriteria(
        String make,
        String model,
        Double minPrice,
        Double maxPrice,
        Integer year
    ) {
        // Compact constructor with validation
        public CarSearchCriteria {
            if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
                throw new IllegalArgumentException("Min price cannot be greater than max price");
            }
        }
        
        // Method in record
        public boolean hasPrice() {
            return minPrice != null || maxPrice != null;
        }
    }
    
    /**
     * Process car search using virtual threads - Java 21 feature
     */
    public Future<List<String>> processCarSearchAsync(CarSearchCriteria criteria) {
        return CompletableFuture.supplyAsync(() -> {
            logger.info("Processing car search on thread: {}", Thread.currentThread().getName());
            
            // Simulate processing time
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            
            return List.of("Car 1", "Car 2", "Car 3");
        }, virtualThreadExecutor);
    }
    
    /**
     * Pattern matching for instanceof - Java 16+ feature
     */
    public String processObject(Object obj) {
        if (obj instanceof String str) {
            return "String: " + str.toUpperCase();
        } else if (obj instanceof Integer num) {
            return "Number: " + (num * 2);
        } else if (obj instanceof List<?> list) {
            return "List with " + list.size() + " elements";
        } else {
            return "Unknown type: " + obj.getClass().getSimpleName();
        }
    }
    
    /**
     * Switch expressions - Java 14+ feature
     */
    public String categorizeCarByYear(int year) {
        return switch (year) {
            case 2024, 2025 -> "Brand New";
            case 2020, 2021, 2022, 2023 -> "Recent";
            case 2015, 2016, 2017, 2018, 2019 -> "Used";
            case 2010, 2011, 2012, 2013, 2014 -> "Older";
            default -> {
                if (year > 2025) {
                    yield "Future Model";
                } else {
                    yield "Classic";
                }
            }
        };
    }
    
    /**
     * Text blocks - Java 15+ feature
     */
    public String generateCarListingHtml(String make, String model, double price) {
        return """
            <div class="car-listing">
                <h3>%s %s</h3>
                <p class="price">$%.2f</p>
                <button onclick="viewDetails()">View Details</button>
            </div>
            """.formatted(make, model, price);
    }
    
    /**
     * Sealed classes example (if we had them) - Java 17+ feature
     * This would be defined as separate classes:
     * 
     * public sealed interface CarCondition permits New, Used, Damaged {}
     * public record New() implements CarCondition {}
     * public record Used(int mileage) implements CarCondition {}
     * public record Damaged(String description) implements CarCondition {}
     */
    
    /**
     * Process multiple searches concurrently using virtual threads
     */
    public Future<List<String>> processMultipleSearches(List<CarSearchCriteria> criteriaList) {
        return CompletableFuture.supplyAsync(() -> {
            List<Future<List<String>>> futures = criteriaList.stream()
                .map(this::processCarSearchAsync)
                .toList();
            
            return futures.stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (Exception e) {
                        logger.error("Error processing search", e);
                        return List.<String>of();
                    }
                })
                .flatMap(List::stream)
                .toList();
        }, virtualThreadExecutor);
    }
}