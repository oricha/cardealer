package com.crashedcarsales.service;

import com.crashedcarsales.dto.DealerProfile;
import com.crashedcarsales.dto.DealerRegistrationRequest;
import com.crashedcarsales.dto.DealerStats;
import com.crashedcarsales.entity.Dealer;
import com.crashedcarsales.entity.User;
import com.crashedcarsales.repository.DealerRepository;
import com.crashedcarsales.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class DealerService {

    private static final Logger logger = LoggerFactory.getLogger(DealerService.class);

    private final DealerRepository dealerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DealerService(DealerRepository dealerRepository,
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder) {
        this.dealerRepository = dealerRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new dealer
     */
    public DealerProfile registerDealer(DealerRegistrationRequest request) {
        logger.info("Registering new dealer with email: {}", request.getEmail());

        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Registration failed: Email already exists: {}", request.getEmail());
            throw new RuntimeException("User with this email already exists");
        }

        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.DEALER);
        user.setIsActive(true);

        // Save user first
        User savedUser = userRepository.save(user);
        logger.info("User created for dealer with ID: {}", savedUser.getId());

        // Create dealer
        Dealer dealer = new Dealer();
        dealer.setUser(savedUser);
        dealer.setName(request.getName());
        dealer.setAddress(request.getAddress());
        dealer.setPhone(request.getPhone());
        dealer.setWebsite(request.getWebsite());

        // Save dealer
        Dealer savedDealer = dealerRepository.save(dealer);
        logger.info("Dealer registered successfully with ID: {}", savedDealer.getId());

        return DealerProfile.fromEntity(savedDealer);
    }

    /**
     * Get dealer profile by user ID
     */
    @Transactional(readOnly = true)
    public Optional<DealerProfile> getDealerProfile(UUID userId) {
        logger.debug("Getting dealer profile for user ID: {}", userId);

        return dealerRepository.findByUserId(userId)
            .map(DealerProfile::fromEntity);
    }

    /**
     * Get dealer profile by dealer ID
     */
    @Transactional(readOnly = true)
    public Optional<DealerProfile> getDealerProfileById(UUID dealerId) {
        logger.debug("Getting dealer profile for dealer ID: {}", dealerId);

        return dealerRepository.findById(dealerId)
            .map(DealerProfile::fromEntity);
    }

    /**
     * Update dealer profile
     */
    public DealerProfile updateDealerProfile(UUID userId, String name, String address, String phone, String website) {
        logger.info("Updating dealer profile for user ID: {}", userId);

        Dealer dealer = dealerRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Dealer not found for user ID: " + userId));

        // Update fields if provided
        if (name != null) dealer.setName(name);
        if (address != null) dealer.setAddress(address);
        if (phone != null) dealer.setPhone(phone);
        if (website != null) dealer.setWebsite(website);

        Dealer savedDealer = dealerRepository.save(dealer);
        logger.info("Dealer profile updated successfully for dealer ID: {}", savedDealer.getId());

        return DealerProfile.fromEntity(savedDealer);
    }

    /**
     * Get dealer statistics
     */
    @Transactional(readOnly = true)
    public DealerStats getDealerStatistics(UUID dealerId) {
        logger.debug("Getting dealer statistics for dealer ID: {}", dealerId);

        Object[] stats = dealerRepository.getDealerStatistics(dealerId);

        if (stats == null || stats.length < 3) {
            logger.warn("No statistics found for dealer ID: {}", dealerId);
            return new DealerStats(dealerId, null, 0L, 0L, BigDecimal.ZERO);
        }

        // Get dealer name for the stats
        String dealerName = dealerRepository.findById(dealerId)
            .map(Dealer::getName)
            .orElse("Unknown Dealer");

        return new DealerStats(
            dealerId,
            dealerName,
            stats[0] != null ? ((Number) stats[0]).longValue() : 0L, // carsListed
            stats[1] != null ? ((Number) stats[1]).longValue() : 0L, // carsSold
            stats[2] != null ? (BigDecimal) stats[2] : BigDecimal.ZERO // totalValue
        );
    }

    /**
     * Find dealer by user email
     */
    @Transactional(readOnly = true)
    public Optional<Dealer> findByUserEmail(String email) {
        return dealerRepository.findByUserEmail(email);
    }

    /**
     * Find dealer by user ID
     */
    @Transactional(readOnly = true)
    public Optional<Dealer> findByUserId(UUID userId) {
        return dealerRepository.findByUserId(userId);
    }

    /**
     * Search dealers by name pattern
     */
    @Transactional(readOnly = true)
    public List<DealerProfile> searchDealersByName(String namePattern) {
        logger.debug("Searching dealers by name pattern: {}", namePattern);

        return dealerRepository.findDealersByNamePattern(namePattern)
            .stream()
            .map(DealerProfile::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Get recently created dealers
     */
    @Transactional(readOnly = true)
    public List<DealerProfile> getRecentlyCreatedDealers(int limit) {
        logger.debug("Getting recently created dealers, limit: {}", limit);

        return dealerRepository.findRecentlyCreatedDealers(limit)
            .stream()
            .map(DealerProfile::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Get all dealers with their statistics
     */
    @Transactional(readOnly = true)
    public List<DealerStats> getAllDealerStatistics() {
        logger.debug("Getting statistics for all dealers");

        return dealerRepository.findAll().stream()
            .map(dealer -> getDealerStatistics(dealer.getId()))
            .collect(Collectors.toList());
    }

    /**
     * Check if dealer exists for user
     */
    @Transactional(readOnly = true)
    public boolean dealerExistsForUser(UUID userId) {
        return dealerRepository.existsByUserId(userId);
    }

    /**
     * Get total dealer count
     */
    @Transactional(readOnly = true)
    public long getDealerCount() {
        return dealerRepository.count();
    }
}