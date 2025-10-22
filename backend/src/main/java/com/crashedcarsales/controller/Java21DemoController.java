package com.crashedcarsales.controller;

import com.crashedcarsales.dto.CarInfo;
import com.crashedcarsales.service.Java21FeaturesService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Controller demonstrating Java 21 features
 * Including virtual threads, records, pattern matching, and switch expressions
 */
@RestController
@RequestMapping("/api/java21")
public class Java21DemoController {
    
    private final Java21FeaturesService featuresService;
    
    public Java21DemoController(Java21FeaturesService featuresService) {
        this.featuresService = featuresService;
    }
    
    /**
     * Endpoint demonstrating Records and virtual threads
     */
    @PostMapping("/search-async")
    public ResponseEntity<String> searchCarsAsync(@RequestBody Java21FeaturesService.CarSearchCriteria criteria) {
        Future<List<String>> future = featuresService.processCarSearchAsync(criteria);
        
        return ResponseEntity.ok("Search started asynchronously. Criteria: " + criteria);
    }
    
    /**
     * Endpoint demonstrating pattern matching
     */
    @PostMapping("/process-object")
    public ResponseEntity<String> processObject(@RequestBody Object obj) {
        String result = featuresService.processObject(obj);
        return ResponseEntity.ok(result);
    }
    
    /**
     * Endpoint demonstrating switch expressions
     */
    @GetMapping("/categorize-year/{year}")
    public ResponseEntity<String> categorizeCarByYear(@PathVariable int year) {
        String category = featuresService.categorizeCarByYear(year);
        return ResponseEntity.ok("Year " + year + " is categorized as: " + category);
    }
    
    /**
     * Endpoint demonstrating text blocks and records
     */
    @PostMapping("/generate-listing")
    public ResponseEntity<String> generateCarListing(@RequestBody CarInfo carInfo) {
        String html = featuresService.generateCarListingHtml(
            carInfo.make(), 
            carInfo.model(), 
            carInfo.price()
        );
        
        return ResponseEntity.ok()
            .header("Content-Type", "text/html")
            .body(html);
    }
    
    /**
     * Endpoint demonstrating multiple concurrent operations with virtual threads
     */
    @PostMapping("/search-multiple")
    public ResponseEntity<String> searchMultiple(@RequestBody List<Java21FeaturesService.CarSearchCriteria> criteriaList) {
        Future<List<String>> future = featuresService.processMultipleSearches(criteriaList);
        
        return ResponseEntity.ok("Processing " + criteriaList.size() + " searches concurrently with virtual threads");
    }
    
    /**
     * Health check endpoint with modern Java features
     */
    @GetMapping("/health")
    public ResponseEntity<Object> health() {
        // Using local variable type inference (var) - Java 10+
        var threadInfo = Thread.currentThread();
        var isVirtual = threadInfo.isVirtual();
        
        // Using switch expression - Java 14+
        var status = isVirtual 
            ? "Running on virtual thread: " + threadInfo.getName()
            : "Running on platform thread: " + threadInfo.getName();
        
        // Using text blocks - Java 15+
        var response = """
            {
                "status": "UP",
                "java_version": "%s",
                "thread_info": "%s",
                "features": ["Virtual Threads", "Records", "Pattern Matching", "Switch Expressions", "Text Blocks"]
            }
            """.formatted(System.getProperty("java.version"), status);
        
        return ResponseEntity.ok()
            .header("Content-Type", "application/json")
            .body(response);
    }
}