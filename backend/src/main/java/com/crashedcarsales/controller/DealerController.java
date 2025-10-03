package com.crashedcarsales.controller;

import com.crashedcarsales.dto.DealerProfile;
import com.crashedcarsales.dto.DealerRegistrationRequest;
import com.crashedcarsales.dto.DealerStats;
import com.crashedcarsales.service.DealerService;
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
@RequestMapping("/api/dealers")
@Tag(name = "Dealer Management", description = "Dealer management APIs")
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class DealerController {

    private static final Logger logger = LoggerFactory.getLogger(DealerController.class);

    private final DealerService dealerService;

    @Autowired
    public DealerController(DealerService dealerService) {
        this.dealerService = dealerService;
    }

    @PostMapping("/register")
    @Operation(
        summary = "Register a new dealer",
        description = "Register a new dealer with business information"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Dealer registered successfully",
            content = @Content(schema = @Schema(implementation = DealerProfile.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "User with this email already exists"
        )
    })
    public ResponseEntity<?> registerDealer(@Valid @RequestBody DealerRegistrationRequest request) {
        try {
            logger.info("Dealer registration attempt for email: {}", request.getEmail());

            DealerProfile response = dealerService.registerDealer(request);

            logger.info("Dealer registered successfully: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            logger.error("Dealer registration failed for email: {} - {}", request.getEmail(), e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error during dealer registration for email: {}", request.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/profile")
    @Operation(
        summary = "Get current dealer profile",
        description = "Get the profile of the currently authenticated dealer"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Profile retrieved successfully",
            content = @Content(schema = @Schema(implementation = DealerProfile.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Dealer profile not found"
        )
    })
    public ResponseEntity<?> getDealerProfile(Authentication authentication) {
        try {
            String email = authentication.getName();
            logger.info("Getting dealer profile for authenticated user: {}", email);

            return dealerService.findByUserEmail(email)
                .map(dealer -> ResponseEntity.ok(DealerProfile.fromEntity(dealer)))
                .orElse(ResponseEntity.notFound().build());

        } catch (Exception e) {
            logger.error("Error getting dealer profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal server error"));
        }
    }

    @PutMapping("/profile")
    @Operation(
        summary = "Update dealer profile",
        description = "Update the profile information of the currently authenticated dealer"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Profile updated successfully",
            content = @Content(schema = @Schema(implementation = DealerProfile.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Dealer not found"
        )
    })
    public ResponseEntity<?> updateDealerProfile(
            Authentication authentication,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String website) {
        try {
            String email = authentication.getName();
            logger.info("Updating dealer profile for user: {}", email);

            return dealerService.findByUserEmail(email)
                .map(dealer -> {
                    DealerProfile updated = dealerService.updateDealerProfile(
                        dealer.getUser().getId(), name, address, phone, website);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());

        } catch (Exception e) {
            logger.error("Error updating dealer profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/statistics")
    @Operation(
        summary = "Get dealer statistics",
        description = "Get statistics for the currently authenticated dealer"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Statistics retrieved successfully",
            content = @Content(schema = @Schema(implementation = DealerStats.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Dealer not found"
        )
    })
    public ResponseEntity<?> getDealerStatistics(Authentication authentication) {
        try {
            String email = authentication.getName();
            logger.info("Getting dealer statistics for user: {}", email);

            return dealerService.findByUserEmail(email)
                .map(dealer -> {
                    DealerStats stats = dealerService.getDealerStatistics(dealer.getId());
                    return ResponseEntity.ok(stats);
                })
                .orElse(ResponseEntity.notFound().build());

        } catch (Exception e) {
            logger.error("Error getting dealer statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/statistics/{dealerId}")
    @Operation(
        summary = "Get dealer statistics by ID",
        description = "Get statistics for a specific dealer by ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Statistics retrieved successfully",
            content = @Content(schema = @Schema(implementation = DealerStats.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Dealer not found"
        )
    })
    public ResponseEntity<?> getDealerStatisticsById(
            @Parameter(description = "Dealer ID") @PathVariable UUID dealerId) {
        try {
            logger.info("Getting dealer statistics for dealer ID: {}", dealerId);

            return dealerService.getDealerProfileById(dealerId)
                .map(dealer -> {
                    DealerStats stats = dealerService.getDealerStatistics(dealerId);
                    return ResponseEntity.ok(stats);
                })
                .orElse(ResponseEntity.notFound().build());

        } catch (Exception e) {
            logger.error("Error getting dealer statistics for ID: {}", dealerId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/search")
    @Operation(
        summary = "Search dealers by name",
        description = "Search for dealers by name pattern"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Search completed successfully",
            content = @Content(schema = @Schema(implementation = DealerProfile.class))
        )
    })
    public ResponseEntity<List<DealerProfile>> searchDealers(
            @Parameter(description = "Name pattern to search for")
            @RequestParam String name) {
        try {
            logger.info("Searching dealers by name pattern: {}", name);

            List<DealerProfile> dealers = dealerService.searchDealersByName(name);
            return ResponseEntity.ok(dealers);

        } catch (Exception e) {
            logger.error("Error searching dealers by name: {}", name, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/recent")
    @Operation(
        summary = "Get recently created dealers",
        description = "Get a list of recently created dealers"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Recent dealers retrieved successfully",
            content = @Content(schema = @Schema(implementation = DealerProfile.class))
        )
    })
    public ResponseEntity<List<DealerProfile>> getRecentDealers(
            @Parameter(description = "Maximum number of dealers to return")
            @RequestParam(defaultValue = "10") int limit) {
        try {
            logger.info("Getting recently created dealers, limit: {}", limit);

            List<DealerProfile> dealers = dealerService.getRecentlyCreatedDealers(limit);
            return ResponseEntity.ok(dealers);

        } catch (Exception e) {
            logger.error("Error getting recent dealers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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