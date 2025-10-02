package com.crashedcarsales.repository;

import com.crashedcarsales.entity.Car;
import com.crashedcarsales.entity.Favorites;
import com.crashedcarsales.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavoritesRepository extends JpaRepository<Favorites, UUID> {

    /**
     * Find favorite by user and car
     */
    Optional<Favorites> findByUserAndCar(User user, Car car);

    /**
     * Find favorite by user ID and car ID
     */
    @Query("SELECT f FROM Favorites f WHERE f.user.id = :userId AND f.car.id = :carId")
    Optional<Favorites> findByUserIdAndCarId(@Param("userId") UUID userId, @Param("carId") UUID carId);

    /**
     * Check if favorite exists for user and car
     */
    boolean existsByUserAndCar(User user, Car car);

    /**
     * Check if favorite exists for user ID and car ID
     */
    @Query("SELECT COUNT(f) > 0 FROM Favorites f WHERE f.user.id = :userId AND f.car.id = :carId")
    boolean existsByUserIdAndCarId(@Param("userId") UUID userId, @Param("carId") UUID carId);

    /**
     * Find all favorites for a user
     */
    List<Favorites> findByUser(User user);

    /**
     * Find all favorites for a user with pagination
     */
    @Query("SELECT f FROM Favorites f WHERE f.user.id = :userId ORDER BY f.createdAt DESC")
    List<Favorites> findByUserIdOrderByCreatedAtDesc(@Param("userId") UUID userId);

    /**
     * Find all favorites for a car
     */
    @Query("SELECT f FROM Favorites f WHERE f.car.id = :carId ORDER BY f.createdAt DESC")
    List<Favorites> findByCarIdOrderByCreatedAtDesc(@Param("carId") UUID carId);

    /**
     * Count favorites for a user
     */
    long countByUser(User user);

    /**
     * Count favorites for a user by ID
     */
    @Query("SELECT COUNT(f) FROM Favorites f WHERE f.user.id = :userId")
    long countByUserId(@Param("userId") UUID userId);

    /**
     * Count favorites for a car
     */
    @Query("SELECT COUNT(f) FROM Favorites f WHERE f.car.id = :carId")
    long countByCarId(@Param("carId") UUID carId);

    /**
     * Delete favorite by user and car
     */
    void deleteByUserAndCar(User user, Car car);

    /**
     * Delete favorite by user ID and car ID
     */
    @Query("DELETE FROM Favorites f WHERE f.user.id = :userId AND f.car.id = :carId")
    void deleteByUserIdAndCarId(@Param("userId") UUID userId, @Param("carId") UUID carId);

    /**
     * Find recently added favorites
     */
    @Query("SELECT f FROM Favorites f ORDER BY f.createdAt DESC LIMIT :limit")
    List<Favorites> findRecentlyAddedFavorites(@Param("limit") int limit);

    /**
     * Find popular cars (cars with most favorites)
     */
    @Query("SELECT f.car.id, COUNT(f) as favoriteCount FROM Favorites f " +
           "WHERE f.car.isActive = true " +
           "GROUP BY f.car.id " +
           "ORDER BY favoriteCount DESC " +
           "LIMIT :limit")
    List<Object[]> findPopularCars(@Param("limit") int limit);

    /**
     * Find users who favorited a specific car
     */
    @Query("SELECT f.user FROM Favorites f WHERE f.car.id = :carId ORDER BY f.createdAt DESC")
    List<User> findUsersWhoFavoritedCar(@Param("carId") UUID carId);
}