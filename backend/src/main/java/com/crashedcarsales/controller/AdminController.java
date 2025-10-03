package com.crashedcarsales.controller;

import com.crashedcarsales.dto.AdminStatsResponse;
import com.crashedcarsales.dto.AdminUserResponse;
import com.crashedcarsales.dto.UserStatusUpdateRequest;
import com.crashedcarsales.entity.User;
import com.crashedcarsales.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin Management", description = "APIs for administrative operations")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Get all users with pagination
     */
    @GetMapping("/users")
    @Operation(summary = "Get all users", description = "Retrieve a paginated list of all users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved users"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<Page<AdminUserResponse>> getAllUsers(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc or desc)")
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<AdminUserResponse> users = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Get user by ID
     */
    @GetMapping("/users/{userId}")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<AdminUserResponse> getUserById(
            @Parameter(description = "User ID")
            @PathVariable UUID userId) {

        AdminUserResponse user = adminService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    /**
     * Update user status (activate/suspend)
     */
    @PatchMapping("/users/{userId}/status")
    @Operation(summary = "Update user status", description = "Activate or suspend a user account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated user status"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "400", description = "Invalid status update request"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<AdminUserResponse> updateUserStatus(
            @Parameter(description = "User ID")
            @PathVariable UUID userId,
            @Valid @RequestBody UserStatusUpdateRequest request) {

        AdminUserResponse updatedUser = adminService.updateUserStatus(userId, request);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Delete user (soft delete)
     */
    @DeleteMapping("/users/{userId}")
    @Operation(summary = "Delete user", description = "Soft delete a user by deactivating their account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully deleted user"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID")
            @PathVariable UUID userId) {

        adminService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Get system statistics
     */
    @GetMapping("/stats")
    @Operation(summary = "Get system statistics", description = "Retrieve comprehensive system statistics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved system statistics"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<AdminStatsResponse> getSystemStats() {
        AdminStatsResponse stats = adminService.getSystemStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Search users by email
     */
    @GetMapping("/users/search")
    @Operation(summary = "Search users by email", description = "Search for users by email pattern")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved matching users"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<List<AdminUserResponse>> searchUsersByEmail(
            @Parameter(description = "Email pattern to search for")
            @RequestParam String email) {

        List<AdminUserResponse> users = adminService.searchUsersByEmail(email);
        return ResponseEntity.ok(users);
    }

    /**
     * Get users by role
     */
    @GetMapping("/users/role/{role}")
    @Operation(summary = "Get users by role", description = "Retrieve all users with a specific role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved users by role"),
        @ApiResponse(responseCode = "400", description = "Invalid role specified"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<List<AdminUserResponse>> getUsersByRole(
            @Parameter(description = "User role (ADMIN, DEALER, BUYER)")
            @PathVariable User.Role role) {

        List<AdminUserResponse> users = adminService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }
}