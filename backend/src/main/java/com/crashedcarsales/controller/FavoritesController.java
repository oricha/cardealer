package com.crashedcarsales.controller;

import com.crashedcarsales.dto.FavoritesRequest;
import com.crashedcarsales.dto.FavoritesResponse;
import com.crashedcarsales.service.FavoritesService;
import com.crashedcarsales.service.JwtService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/favorites")
@Tag(name = "Favorites Management", description = "User favorites management APIs")
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class FavoritesController {

    private static final Logger logger = LoggerFactory.getLogger(FavoritesController.class);

    private final FavoritesService favoritesService;
    private final JwtService jwtService;

    @Autowired
    public FavoritesController(FavoritesService favoritesService, JwtService jwtService) {
        this.favoritesService = favoritesService;
        this.jwtService = jwtService;
    }

    @PostMapping
    @Operation(
        summary = "Add car to favorites",
        description = "Add a car to the authenticated user's favorites"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Car added to favorites successfully",
            content = @Content(schema = @Schema(implementation = FavoritesResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request or car already in favorites"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User or car not found"
        )
    })
    public ResponseEntity<?> addToFavorites(
            Authentication authentication,
            @Valid @RequestBody FavoritesRequest request) {
        try {
            String email = authentication.getName();
            logger.info("Adding car {} to favorites for user: {}", request.getCarId(), email);

            // Extract user ID from authentication (this would need to be implemented
            // based on how user information is stored in the JWT token)
            UUID userId = extractUserIdFromAuthentication(authentication);

            FavoritesResponse response = favoritesService.addToFavorites(userId, request.getCarId());

            logger.info("Car {} added to favorites successfully for user: {}", request.getCarId(), email);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            logger.error("Failed to add car to favorites: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error adding car to favorites", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal server error"));
        }
    }

    @DeleteMapping("/{carId}")
    @Operation(
        summary = "Remove car from favorites",
        description = "Remove a car from the authenticated user's favorites"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Car removed from favorites successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User, car, or favorite not found"
        )
    })
    public ResponseEntity<?> removeFromFavorites(
            Authentication authentication,
            @Parameter(description = "Car ID") @PathVariable UUID carId) {
        try {
            String email = authentication.getName();
            logger.info("Removing car {} from favorites for user: {}", carId, email);

            UUID userId = extractUserIdFromAuthentication(authentication);

            favoritesService.removeFromFavorites(userId, carId);

            logger.info("Car {} removed from favorites successfully for user: {}", carId, email);
            return ResponseEntity.ok(new SuccessResponse("Car removed from favorites successfully"));

        } catch (RuntimeException e) {
            logger.error("Failed to remove car from favorites: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error removing car from favorites", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal server error"));
        }
    }

    @PostMapping("/{carId}/toggle")
    @Operation(
        summary = "Toggle favorite status",
        description = "Toggle a car's favorite status (add if not favorited, remove if favorited)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Favorite status toggled successfully",
            content = @Content(schema = @Schema(implementation = FavoritesResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User or car not found"
        )
    })
    public ResponseEntity<?> toggleFavorite(
            Authentication authentication,
            @Parameter(description = "Car ID") @PathVariable UUID carId) {
        try {
            String email = authentication.getName();
            logger.info("Toggling favorite status for car {} and user: {}", carId, email);

            UUID userId = extractUserIdFromAuthentication(authentication);

            FavoritesResponse response = favoritesService.toggleFavorite(userId, carId);

            if (response != null) {
                logger.info("Car {} added to favorites for user: {}", carId, email);
                return ResponseEntity.ok(response);
            } else {
                logger.info("Car {} removed from favorites for user: {}", carId, email);
                return ResponseEntity.ok(new SuccessResponse("Car removed from favorites"));
            }

        } catch (RuntimeException e) {
            logger.error("Failed to toggle favorite status: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error toggling favorite status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping
    @Operation(
        summary = "Get user's favorites",
        description = "Get all cars favorited by the authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Favorites retrieved successfully",
            content = @Content(schema = @Schema(implementation = FavoritesResponse.class))
        )
    })
    public ResponseEntity<List<FavoritesResponse>> getUserFavorites(Authentication authentication) {
        try {
            String email = authentication.getName();
            logger.info("Getting favorites list for user: {}", email);

            UUID userId = extractUserIdFromAuthentication(authentication);

            List<FavoritesResponse> response = favoritesService.getUserFavorites(userId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting user favorites", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/check/{carId}")
    @Operation(
        summary = "Check if car is in favorites",
        description = "Check if a specific car is in the authenticated user's favorites"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Check completed successfully"
        )
    })
    public ResponseEntity<java.util.Map<String, Boolean>> checkFavorite(
            Authentication authentication,
            @Parameter(description = "Car ID") @PathVariable UUID carId) {
        try {
            String email = authentication.getName();
            logger.debug("Checking favorite status for car {} and user: {}", carId, email);

            UUID userId = extractUserIdFromAuthentication(authentication);

            boolean isFavorite = favoritesService.isInFavorites(userId, carId);
            java.util.Map<String, Boolean> response = java.util.Map.of("isFavorite", isFavorite);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error checking favorite status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/count")
    @Operation(
        summary = "Get user's favorites count",
        description = "Get the count of cars favorited by the authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Count retrieved successfully"
        )
    })
    public ResponseEntity<java.util.Map<String, Long>> getFavoritesCount(Authentication authentication) {
        try {
            String email = authentication.getName();
            logger.debug("Getting favorites count for user: {}", email);

            UUID userId = extractUserIdFromAuthentication(authentication);

            long count = favoritesService.getUserFavoritesCount(userId);
            java.util.Map<String, Long> response = java.util.Map.of("count", count);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting favorites count", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/car/{carId}/count")
    @Operation(
        summary = "Get car's favorites count",
        description = "Get the count of users who favorited a specific car"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Count retrieved successfully"
        )
    })
    public ResponseEntity<java.util.Map<String, Long>> getCarFavoritesCount(
            @Parameter(description = "Car ID") @PathVariable UUID carId) {
        try {
            logger.debug("Getting favorites count for car: {}", carId);

            long count = favoritesService.getCarFavoritesCount(carId);
            java.util.Map<String, Long> response = java.util.Map.of("count", count);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting car favorites count", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/popular")
    @Operation(
        summary = "Get popular cars",
        description = "Get cars with the most favorites"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Popular cars retrieved successfully"
        )
    })
    public ResponseEntity<List<UUID>> getPopularCars(
            @Parameter(description = "Maximum number of cars to return")
            @RequestParam(defaultValue = "10") int limit) {
        try {
            logger.debug("Getting popular cars, limit: {}", limit);

            List<UUID> response = favoritesService.getPopularCars(limit);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting popular cars", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/recent")
    @Operation(
        summary = "Get recently added favorites",
        description = "Get recently added favorites across all users"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Recent favorites retrieved successfully",
            content = @Content(schema = @Schema(implementation = FavoritesResponse.class))
        )
    })
    public ResponseEntity<List<FavoritesResponse>> getRecentFavorites(
            @Parameter(description = "Maximum number of favorites to return")
            @RequestParam(defaultValue = "20") int limit) {
        try {
            logger.debug("Getting recent favorites, limit: {}", limit);

            List<FavoritesResponse> response = favoritesService.getRecentlyAddedFavorites(limit);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting recent favorites", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Helper method to extract user ID from authentication
    private UUID extractUserIdFromAuthentication(Authentication authentication) {
        try {
            // Get the JWT token from the authentication
            String token = (String) authentication.getCredentials();
            if (token == null || token.isEmpty()) {
                throw new RuntimeException("No JWT token found in authentication");
            }

            // Extract user ID from the JWT token
            return jwtService.extractUserId(token);
        } catch (Exception e) {
            logger.error("Failed to extract user ID from authentication: {}", e.getMessage());
            throw new RuntimeException("Invalid authentication token", e);
        }
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