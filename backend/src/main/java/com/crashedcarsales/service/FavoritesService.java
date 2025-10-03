package com.crashedcarsales.service;

import com.crashedcarsales.dto.FavoritesResponse;
import com.crashedcarsales.entity.Car;
import com.crashedcarsales.entity.Favorites;
import com.crashedcarsales.entity.User;
import com.crashedcarsales.repository.CarRepository;
import com.crashedcarsales.repository.FavoritesRepository;
import com.crashedcarsales.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class FavoritesService {

    private static final Logger logger = LoggerFactory.getLogger(FavoritesService.class);

    private final FavoritesRepository favoritesRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;

    @Autowired
    public FavoritesService(FavoritesRepository favoritesRepository,
                           UserRepository userRepository,
                           CarRepository carRepository) {
        this.favoritesRepository = favoritesRepository;
        this.userRepository = userRepository;
        this.carRepository = carRepository;
    }

    /**
     * Add car to user's favorites
     */
    public FavoritesResponse addToFavorites(UUID userId, UUID carId) {
        logger.info("Adding car {} to favorites for user {}", carId, userId);

        // Validate user exists
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Validate car exists and is active
        Car car = carRepository.findById(carId)
            .filter(Car::getIsActive)
            .orElseThrow(() -> new RuntimeException("Car not found or inactive with ID: " + carId));

        // Check if already in favorites
        if (favoritesRepository.existsByUserAndCar(user, car)) {
            logger.info("Car {} already in favorites for user {}", carId, userId);
            throw new RuntimeException("Car is already in favorites");
        }

        // Create new favorite
        Favorites favorite = new Favorites(user, car);
        Favorites savedFavorite = favoritesRepository.save(favorite);

        logger.info("Car {} added to favorites for user {}", carId, userId);
        return FavoritesResponse.fromEntity(savedFavorite);
    }

    /**
     * Remove car from user's favorites
     */
    public void removeFromFavorites(UUID userId, UUID carId) {
        logger.info("Removing car {} from favorites for user {}", carId, userId);

        // Validate user exists
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Validate car exists
        Car car = carRepository.findById(carId)
            .orElseThrow(() -> new RuntimeException("Car not found with ID: " + carId));

        // Check if favorite exists
        Favorites favorite = favoritesRepository.findByUserAndCar(user, car)
            .orElseThrow(() -> new RuntimeException("Car is not in favorites"));

        // Remove favorite
        favoritesRepository.delete(favorite);

        logger.info("Car {} removed from favorites for user {}", carId, userId);
    }

    /**
     * Toggle favorite status (add if not exists, remove if exists)
     */
    public FavoritesResponse toggleFavorite(UUID userId, UUID carId) {
        logger.info("Toggling favorite status for car {} and user {}", carId, userId);

        // Check if already in favorites
        if (isInFavorites(userId, carId)) {
            removeFromFavorites(userId, carId);
            return null; // Indicates removed from favorites
        } else {
            return addToFavorites(userId, carId);
        }
    }

    /**
     * Check if car is in user's favorites
     */
    @Transactional(readOnly = true)
    public boolean isInFavorites(UUID userId, UUID carId) {
        return favoritesRepository.existsByUserIdAndCarId(userId, carId);
    }

    /**
     * Get user's favorites list
     */
    @Transactional(readOnly = true)
    public List<FavoritesResponse> getUserFavorites(UUID userId) {
        logger.debug("Getting favorites list for user: {}", userId);

        List<Favorites> favorites = favoritesRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return FavoritesResponse.fromEntities(favorites);
    }

    /**
     * Get user's favorites count
     */
    @Transactional(readOnly = true)
    public long getUserFavoritesCount(UUID userId) {
        return favoritesRepository.countByUserId(userId);
    }

    /**
     * Get car favorites count
     */
    @Transactional(readOnly = true)
    public long getCarFavoritesCount(UUID carId) {
        return favoritesRepository.countByCarId(carId);
    }

    /**
     * Get popular cars (cars with most favorites)
     */
    @Transactional(readOnly = true)
    public List<UUID> getPopularCars(int limit) {
        logger.debug("Getting popular cars, limit: {}", limit);

        return favoritesRepository.findPopularCars(limit)
            .stream()
            .map(data -> (UUID) data[0]) // Extract car ID from Object array
            .toList();
    }

    /**
     * Get users who favorited a specific car
     */
    @Transactional(readOnly = true)
    public List<User> getUsersWhoFavoritedCar(UUID carId) {
        logger.debug("Getting users who favorited car: {}", carId);

        return favoritesRepository.findUsersWhoFavoritedCar(carId);
    }

    /**
     * Get recently added favorites
     */
    @Transactional(readOnly = true)
    public List<FavoritesResponse> getRecentlyAddedFavorites(int limit) {
        logger.debug("Getting recently added favorites, limit: {}", limit);

        List<Favorites> recentFavorites = favoritesRepository.findRecentlyAddedFavorites(limit);
        return FavoritesResponse.fromEntities(recentFavorites);
    }

    /**
     * Remove all favorites for a user (when user is deleted)
     */
    public void removeAllFavoritesForUser(UUID userId) {
        logger.info("Removing all favorites for user: {}", userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        List<Favorites> userFavorites = favoritesRepository.findByUser(user);
        favoritesRepository.deleteAll(userFavorites);

        logger.info("Removed {} favorites for user: {}", userFavorites.size(), userId);
    }

    /**
     * Remove all favorites for a car (when car is deleted)
     */
    public void removeAllFavoritesForCar(UUID carId) {
        logger.info("Removing all favorites for car: {}", carId);

        Car car = carRepository.findById(carId)
            .orElseThrow(() -> new RuntimeException("Car not found with ID: " + carId));

        List<Favorites> carFavorites = favoritesRepository.findByCarIdOrderByCreatedAtDesc(carId);
        favoritesRepository.deleteAll(carFavorites);

        logger.info("Removed {} favorites for car: {}", carFavorites.size(), carId);
    }

    /**
     * Get favorite by ID
     */
    @Transactional(readOnly = true)
    public Optional<Favorites> getFavoriteById(UUID favoriteId) {
        return favoritesRepository.findById(favoriteId);
    }

    /**
     * Check if user can add car to favorites (car exists and is active)
     */
    @Transactional(readOnly = true)
    public boolean canAddToFavorites(UUID userId, UUID carId) {
        // Check if user exists
        if (!userRepository.existsById(userId)) {
            return false;
        }

        // Check if car exists and is active
        return carRepository.findById(carId)
            .map(car -> car.getIsActive())
            .orElse(false);
    }
}