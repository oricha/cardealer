package com.crashedcarsales.controller;

import com.crashedcarsales.dto.CarCreateRequest;
import com.crashedcarsales.dto.CarResponse;
import com.crashedcarsales.dto.CarSearchRequest;
import com.crashedcarsales.dto.CarUpdateRequest;
import com.crashedcarsales.entity.Car;
import com.crashedcarsales.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cars")
@Tag(name = "Car Management", description = "Car management APIs")
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class CarController {

    private static final Logger logger = LoggerFactory.getLogger(CarController.class);

    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @PostMapping
    @Operation(
        summary = "Create a new car",
        description = "Create a new car listing for the authenticated dealer"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Car created successfully",
            content = @Content(schema = @Schema(implementation = CarResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Dealer not found"
        )
    })
    public ResponseEntity<?> createCar(
            Authentication authentication,
            @Valid @RequestBody CarCreateRequest request) {
        try {
            String email = authentication.getName();
            logger.info("Car creation attempt by dealer: {}", email);

            // Extract dealer ID from the authenticated user (this would need to be implemented
            // based on how dealer information is stored in the JWT token or security context)
            UUID dealerId = extractDealerIdFromAuthentication(authentication);

            CarResponse response = carService.createCar(dealerId, request);

            logger.info("Car created successfully with ID: {}", response.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            logger.error("Car creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error during car creation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/{carId}")
    @Operation(
        summary = "Get car by ID",
        description = "Get detailed information about a specific car"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Car retrieved successfully",
            content = @Content(schema = @Schema(implementation = CarResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Car not found"
        )
    })
    public ResponseEntity<?> getCarById(
            @Parameter(description = "Car ID") @PathVariable UUID carId) {
        try {
            logger.info("Getting car by ID: {}", carId);

            return carService.getCarById(carId)
                .map(car -> ResponseEntity.ok(car))
                .orElse(ResponseEntity.notFound().build());

        } catch (Exception e) {
            logger.error("Error getting car by ID: {}", carId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal server error"));
        }
    }

    @PutMapping("/{carId}")
    @Operation(
        summary = "Update car",
        description = "Update car information for the authenticated dealer"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Car updated successfully",
            content = @Content(schema = @Schema(implementation = CarResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Car not found"
        )
    })
    public ResponseEntity<?> updateCar(
            @Parameter(description = "Car ID") @PathVariable UUID carId,
            @Valid @RequestBody CarUpdateRequest request) {
        try {
            logger.info("Updating car with ID: {}", carId);

            CarResponse response = carService.updateCar(carId, request);

            logger.info("Car updated successfully with ID: {}", carId);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            logger.error("Car update failed for ID: {} - {}", carId, e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error during car update for ID: {}", carId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal server error"));
        }
    }

    @DeleteMapping("/{carId}")
    @Operation(
        summary = "Delete car",
        description = "Soft delete a car (mark as inactive)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Car deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Car not found"
        )
    })
    public ResponseEntity<?> deleteCar(
            @Parameter(description = "Car ID") @PathVariable UUID carId) {
        try {
            logger.info("Deleting car with ID: {}", carId);

            carService.deleteCar(carId);

            logger.info("Car deleted successfully with ID: {}", carId);
            return ResponseEntity.ok(new SuccessResponse("Car deleted successfully"));

        } catch (RuntimeException e) {
            logger.error("Car deletion failed for ID: {} - {}", carId, e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error during car deletion for ID: {}", carId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal server error"));
        }
    }

    @PostMapping("/search")
    @Operation(
        summary = "Search cars with filters",
        description = "Search for cars using various filters and pagination"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Search completed successfully",
            content = @Content(schema = @Schema(implementation = CarResponse.class))
        )
    })
    public ResponseEntity<Page<CarResponse>> searchCars(@RequestBody CarSearchRequest searchRequest) {
        try {
            logger.info("Searching cars with filters: {}", searchRequest);

            Page<CarResponse> response = carService.searchCars(searchRequest);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error searching cars", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/dealer/{dealerId}")
    @Operation(
        summary = "Get cars by dealer",
        description = "Get all active cars for a specific dealer"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Cars retrieved successfully",
            content = @Content(schema = @Schema(implementation = CarResponse.class))
        )
    })
    public ResponseEntity<List<CarResponse>> getCarsByDealer(
            @Parameter(description = "Dealer ID") @PathVariable UUID dealerId) {
        try {
            logger.info("Getting cars for dealer ID: {}", dealerId);

            List<CarResponse> response = carService.getCarsByDealer(dealerId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting cars for dealer ID: {}", dealerId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/featured")
    @Operation(
        summary = "Get featured cars",
        description = "Get all featured cars"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Featured cars retrieved successfully",
            content = @Content(schema = @Schema(implementation = CarResponse.class))
        )
    })
    public ResponseEntity<List<CarResponse>> getFeaturedCars() {
        try {
            logger.info("Getting featured cars");

            List<CarResponse> response = carService.getFeaturedCars();
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting featured cars", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/recent")
    @Operation(
        summary = "Get recently added cars",
        description = "Get recently added cars"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Recent cars retrieved successfully",
            content = @Content(schema = @Schema(implementation = CarResponse.class))
        )
    })
    public ResponseEntity<List<CarResponse>> getRecentCars(
            @Parameter(description = "Maximum number of cars to return")
            @RequestParam(defaultValue = "10") int limit) {
        try {
            logger.info("Getting recently added cars, limit: {}", limit);

            List<CarResponse> response = carService.getRecentlyAddedCars(limit);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting recent cars", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{carId}/similar")
    @Operation(
        summary = "Find similar cars",
        description = "Find cars similar to the specified car"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Similar cars retrieved successfully",
            content = @Content(schema = @Schema(implementation = CarResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Car not found"
        )
    })
    public ResponseEntity<List<CarResponse>> findSimilarCars(
            @Parameter(description = "Car ID") @PathVariable UUID carId,
            @Parameter(description = "Maximum number of similar cars to return")
            @RequestParam(defaultValue = "5") int limit) {
        try {
            logger.info("Finding similar cars for car ID: {}, limit: {}", carId, limit);

            List<CarResponse> response = carService.findSimilarCars(carId, limit);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            logger.error("Error finding similar cars for ID: {} - {}", carId, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error finding similar cars for ID: {}", carId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/make/{make}")
    @Operation(
        summary = "Get cars by make",
        description = "Get all cars of a specific make"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Cars retrieved successfully",
            content = @Content(schema = @Schema(implementation = CarResponse.class))
        )
    })
    public ResponseEntity<List<CarResponse>> getCarsByMake(
            @Parameter(description = "Car make") @PathVariable String make) {
        try {
            logger.info("Getting cars by make: {}", make);

            List<CarResponse> response = carService.getCarsByMake(make);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting cars by make: {}", make, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/fuel-type/{fuelType}")
    @Operation(
        summary = "Get cars by fuel type",
        description = "Get all cars of a specific fuel type"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Cars retrieved successfully",
            content = @Content(schema = @Schema(implementation = CarResponse.class))
        )
    })
    public ResponseEntity<List<CarResponse>> getCarsByFuelType(
            @Parameter(description = "Fuel type") @PathVariable Car.FuelType fuelType) {
        try {
            logger.info("Getting cars by fuel type: {}", fuelType);

            List<CarResponse> response = carService.getCarsByFuelType(fuelType);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting cars by fuel type: {}", fuelType, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search-text")
    @Operation(
        summary = "Search cars by text",
        description = "Search cars by text in make, model, or description"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Search completed successfully",
            content = @Content(schema = @Schema(implementation = CarResponse.class))
        )
    })
    public ResponseEntity<List<CarResponse>> searchCarsByText(
            @Parameter(description = "Search text")
            @RequestParam String query,
            @Parameter(description = "Maximum number of results")
            @RequestParam(defaultValue = "20") int limit) {
        try {
            logger.info("Searching cars by text: {}, limit: {}", query, limit);

            List<CarResponse> response = carService.searchCarsByText(query, limit);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error searching cars by text: {}", query, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Helper method to extract dealer ID from authentication
    // This would need to be implemented based on your JWT token structure
    private UUID extractDealerIdFromAuthentication(Authentication authentication) {
        // TODO: Implement based on how dealer information is stored in JWT token
        // For now, return a placeholder - this needs to be properly implemented
        throw new UnsupportedOperationException("Dealer ID extraction from authentication not yet implemented");
    }

    // Inner classes for error and success responses
    public static class ErrorResponse {
        private String error;
        private Long timestamp;

        public ErrorResponse(String error) {
            this.error = error;
            this.timestamp = System.currentTimeMillis();
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }
    }

    public static class SuccessResponse {
        private String message;
        private Long timestamp;

        public SuccessResponse(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }
    }
}