package com.crashedcarsales.controller;

import com.crashedcarsales.dto.ApiResponse;
import com.crashedcarsales.dto.CarResponse;
import com.crashedcarsales.dto.DealerProfile;
import com.crashedcarsales.entity.Car;
import com.crashedcarsales.entity.Dealer;
import com.crashedcarsales.service.CarService;
import com.crashedcarsales.service.DealerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public")
@Tag(name = "Public API", description = "Public API endpoints for external access")
public class PublicApiController {

    private final CarService carService;
    private final DealerService dealerService;

    @Autowired
    public PublicApiController(CarService carService, DealerService dealerService) {
        this.carService = carService;
        this.dealerService = dealerService;
    }

    /**
     * Get all active cars with pagination
     */
    @GetMapping("/cars")
    @Operation(summary = "Get all active cars", description = "Retrieve a paginated list of all active cars")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved cars")
    })
    public ResponseEntity<ApiResponse<Page<CarResponse>>> getAllCars(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc or desc)")
            @RequestParam(defaultValue = "desc") String sortDir) {

        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            Page<CarResponse> cars = carService.getAllActiveCars(pageable);
            return ResponseEntity.ok(ApiResponse.success("Cars retrieved successfully", cars));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve cars", "INTERNAL_ERROR"));
        }
    }

    /**
     * Get car by ID
     */
    @GetMapping("/cars/{carId}")
    @Operation(summary = "Get car by ID", description = "Retrieve a specific car by its ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved car"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Car not found")
    })
    public ResponseEntity<ApiResponse<CarResponse>> getCarById(
            @Parameter(description = "Car ID")
            @PathVariable UUID carId) {

        try {
            Optional<CarResponse> carOpt = carService.getCarById(carId);
            if (carOpt.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Car retrieved successfully", carOpt.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Car not found", "CAR_NOT_FOUND"));
            }
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Car not found", "CAR_NOT_FOUND"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve car", "INTERNAL_ERROR"));
        }
    }

    /**
     * Search cars with filters
     */
    @GetMapping("/cars/search")
    @Operation(summary = "Search cars with filters", description = "Search cars using various filter criteria")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved filtered cars")
    })
    public ResponseEntity<ApiResponse<Page<CarResponse>>> searchCars(
            @Parameter(description = "Car make")
            @RequestParam(required = false) String make,
            @Parameter(description = "Car model")
            @RequestParam(required = false) String model,
            @Parameter(description = "Fuel type")
            @RequestParam(required = false) Car.FuelType fuelType,
            @Parameter(description = "Transmission type")
            @RequestParam(required = false) Car.Transmission transmission,
            @Parameter(description = "Vehicle type")
            @RequestParam(required = false) Car.VehicleType vehicleType,
            @Parameter(description = "Condition")
            @RequestParam(required = false) Car.Condition condition,
            @Parameter(description = "Minimum price")
            @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum price")
            @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "Minimum year")
            @RequestParam(required = false) Integer minYear,
            @Parameter(description = "Maximum year")
            @RequestParam(required = false) Integer maxYear,
            @Parameter(description = "Maximum mileage")
            @RequestParam(required = false) Integer maxMileage,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<CarResponse> cars = carService.searchCarsWithFilters(
                    make, model, fuelType, transmission, vehicleType, condition,
                    minPrice, maxPrice, minYear, maxYear, maxMileage, pageable);

            return ResponseEntity.ok(ApiResponse.success("Search completed successfully", cars));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Search failed", "SEARCH_ERROR"));
        }
    }

    /**
     * Get featured cars
     */
    @GetMapping("/cars/featured")
    @Operation(summary = "Get featured cars", description = "Retrieve all featured cars")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved featured cars")
    })
    public ResponseEntity<ApiResponse<List<CarResponse>>> getFeaturedCars() {
        try {
            List<CarResponse> cars = carService.getFeaturedCars();
            return ResponseEntity.ok(ApiResponse.success("Featured cars retrieved successfully", cars));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve featured cars", "INTERNAL_ERROR"));
        }
    }

    /**
     * Get recently added cars
     */
    @GetMapping("/cars/recent")
    @Operation(summary = "Get recently added cars", description = "Retrieve recently added cars")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved recent cars")
    })
    public ResponseEntity<ApiResponse<List<CarResponse>>> getRecentCars(
            @Parameter(description = "Maximum number of cars to return")
            @RequestParam(defaultValue = "10") int limit) {

        try {
            List<CarResponse> cars = carService.getRecentlyAddedCars(limit);
            return ResponseEntity.ok(ApiResponse.success("Recent cars retrieved successfully", cars));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve recent cars", "INTERNAL_ERROR"));
        }
    }

    /**
     * Get dealer profile by ID
     */
    @GetMapping("/dealers/{dealerId}")
    @Operation(summary = "Get dealer profile", description = "Retrieve a dealer profile by ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved dealer profile"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Dealer not found")
    })
    public ResponseEntity<ApiResponse<DealerProfile>> getDealerProfile(
            @Parameter(description = "Dealer ID")
            @PathVariable UUID dealerId) {

        try {
            Optional<DealerProfile> profileOpt = dealerService.getDealerProfileById(dealerId);
            if (profileOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            DealerProfile profile = profileOpt.get();
            return ResponseEntity.ok(ApiResponse.success("Dealer profile retrieved successfully", profile));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Dealer not found", "DEALER_NOT_FOUND"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve dealer profile", "INTERNAL_ERROR"));
        }
    }

    /**
     * Get dealer statistics by ID
     */
    @GetMapping("/dealers/{dealerId}/stats")
    @Operation(summary = "Get dealer statistics", description = "Retrieve dealer statistics by ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved dealer statistics"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Dealer not found")
    })
    public ResponseEntity<ApiResponse<Object>> getDealerStats(
            @Parameter(description = "Dealer ID")
            @PathVariable UUID dealerId) {

        try {
            Object stats = dealerService.getDealerStatistics(dealerId);
            return ResponseEntity.ok(ApiResponse.success("Dealer statistics retrieved successfully", stats));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Dealer not found", "DEALER_NOT_FOUND"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve dealer statistics", "INTERNAL_ERROR"));
        }
    }

    /**
     * Search dealers by name or email
     */
    @GetMapping("/dealers/search")
    @Operation(summary = "Search dealers", description = "Search dealers by name or email pattern")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved dealers")
    })
    public ResponseEntity<ApiResponse<List<DealerProfile>>> searchDealers(
            @Parameter(description = "Search pattern")
            @RequestParam String pattern) {

        try {
            List<DealerProfile> dealers = dealerService.searchDealersByName(pattern);
            return ResponseEntity.ok(ApiResponse.success("Dealers retrieved successfully", dealers));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Search failed", "SEARCH_ERROR"));
        }
    }

    /**
     * Get cars by dealer ID
     */
    @GetMapping("/dealers/{dealerId}/cars")
    @Operation(summary = "Get dealer cars", description = "Retrieve all active cars from a specific dealer")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved dealer cars"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Dealer not found")
    })
    public ResponseEntity<ApiResponse<List<CarResponse>>> getDealerCars(
            @Parameter(description = "Dealer ID")
            @PathVariable UUID dealerId) {

        try {
            List<CarResponse> cars = carService.getCarsByDealer(dealerId);
            return ResponseEntity.ok(ApiResponse.success("Dealer cars retrieved successfully", cars));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Dealer not found", "DEALER_NOT_FOUND"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve dealer cars", "INTERNAL_ERROR"));
        }
    }

    /**
     * Get similar cars
     */
    @GetMapping("/cars/{carId}/similar")
    @Operation(summary = "Get similar cars", description = "Retrieve cars similar to the specified car")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved similar cars"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Car not found")
    })
    public ResponseEntity<ApiResponse<List<CarResponse>>> getSimilarCars(
            @Parameter(description = "Car ID")
            @PathVariable UUID carId) {

        try {
            List<CarResponse> cars = carService.getSimilarCars(carId);
            return ResponseEntity.ok(ApiResponse.success("Similar cars retrieved successfully", cars));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Car not found", "CAR_NOT_FOUND"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve similar cars", "INTERNAL_ERROR"));
        }
    }
}