package com.crashedcarsales.service;

import com.crashedcarsales.dto.AdminStatsResponse;
import com.crashedcarsales.dto.AdminUserResponse;
import com.crashedcarsales.dto.UserStatusUpdateRequest;
import com.crashedcarsales.entity.User;
import com.crashedcarsales.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final DealerRepository dealerRepository;
    private final CarRepository carRepository;
    private final FavoritesRepository favoritesRepository;

    @Autowired
    public AdminService(UserRepository userRepository,
                       DealerRepository dealerRepository,
                       CarRepository carRepository,
                       FavoritesRepository favoritesRepository) {
        this.userRepository = userRepository;
        this.dealerRepository = dealerRepository;
        this.carRepository = carRepository;
        this.favoritesRepository = favoritesRepository;
    }

    /**
     * Get all users with pagination
     */
    @Transactional(readOnly = true)
    public Page<AdminUserResponse> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(AdminUserResponse::new);
    }

    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public AdminUserResponse getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return new AdminUserResponse(user);
    }

    /**
     * Update user status (activate/suspend)
     */
    public AdminUserResponse updateUserStatus(UUID userId, UserStatusUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        user.setIsActive(request.getIsActive());
        User updatedUser = userRepository.save(user);

        return new AdminUserResponse(updatedUser);
    }

    /**
     * Delete user (soft delete by deactivating)
     */
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        user.setIsActive(false);
        userRepository.save(user);
    }

    /**
     * Get system statistics
     */
    @Transactional(readOnly = true)
    public AdminStatsResponse getSystemStats() {
        AdminStatsResponse stats = new AdminStatsResponse();

        // User statistics
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByIsActiveTrue();

        stats.setTotalUsers(totalUsers);
        stats.setActiveUsers(activeUsers);

        // Dealer statistics
        long totalDealers = userRepository.countByRole(User.Role.DEALER);
        long activeDealers = userRepository.countByRoleAndIsActiveTrue(User.Role.DEALER);

        stats.setTotalDealers(totalDealers);
        stats.setActiveDealers(activeDealers);

        // Car statistics
        long totalCars = carRepository.count();
        long activeCars = carRepository.countByIsActiveTrue();

        stats.setTotalCars(totalCars);
        stats.setActiveCars(activeCars);

        // Favorites statistics
        long totalFavorites = favoritesRepository.count();
        stats.setTotalFavorites(totalFavorites);

        // Calculate average cars per dealer
        if (totalDealers > 0) {
            double averageCarsPerDealer = (double) totalCars / totalDealers;
            stats.setAverageCarsPerDealer(averageCarsPerDealer);
        } else {
            stats.setAverageCarsPerDealer(0.0);
        }

        return stats;
    }

    /**
     * Search users by email
     */
    @Transactional(readOnly = true)
    public List<AdminUserResponse> searchUsersByEmail(String email) {
        List<User> users = userRepository.findByEmailContainingIgnoreCase(email);
        return users.stream()
                .map(AdminUserResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Get users by role
     */
    @Transactional(readOnly = true)
    public List<AdminUserResponse> getUsersByRole(User.Role role) {
        List<User> users = userRepository.findByRole(role);
        return users.stream()
                .map(AdminUserResponse::new)
                .collect(Collectors.toList());
    }
}