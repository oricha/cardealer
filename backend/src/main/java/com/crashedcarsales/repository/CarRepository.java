package com.crashedcarsales.repository;

import com.crashedcarsales.entity.Car;
import com.crashedcarsales.entity.Dealer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface CarRepository extends JpaRepository<Car, UUID> {

    /**
     * Find cars by dealer
     */
    List<Car> findByDealer(Dealer dealer);

    /**
     * Find active cars by dealer
     */
    List<Car> findByDealerAndIsActive(Dealer dealer, Boolean isActive);

    /**
     * Find featured cars
     */
    List<Car> findByIsFeaturedAndIsActive(Boolean isFeatured, Boolean isActive);

    /**
     * Find cars by make (case-insensitive)
     */
    @Query("SELECT c FROM Car c WHERE LOWER(c.make) = LOWER(:make) AND c.isActive = true")
    List<Car> findByMakeIgnoreCase(@Param("make") String make);

    /**
     * Find cars by model (case-insensitive)
     */
    @Query("SELECT c FROM Car c WHERE LOWER(c.model) = LOWER(:model) AND c.isActive = true")
    List<Car> findByModelIgnoreCase(@Param("model") String model);

    /**
     * Find cars by make and model
     */
    @Query("SELECT c FROM Car c WHERE LOWER(c.make) = LOWER(:make) AND LOWER(c.model) = LOWER(:model) AND c.isActive = true")
    List<Car> findByMakeAndModelIgnoreCase(@Param("make") String make, @Param("model") String model);

    /**
     * Find cars by fuel type
     */
    List<Car> findByFuelTypeAndIsActive(Car.FuelType fuelType, Boolean isActive);

    /**
     * Find cars by transmission
     */
    List<Car> findByTransmissionAndIsActive(Car.Transmission transmission, Boolean isActive);

    /**
     * Find cars by vehicle type
     */
    List<Car> findByVehicleTypeAndIsActive(Car.VehicleType vehicleType, Boolean isActive);

    /**
     * Find cars by condition
     */
    List<Car> findByConditionAndIsActive(Car.Condition condition, Boolean isActive);

    /**
     * Find cars within price range
     */
    @Query("SELECT c FROM Car c WHERE c.price BETWEEN :minPrice AND :maxPrice AND c.isActive = true")
    List<Car> findByPriceBetween(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    /**
     * Find cars by year
     */
    List<Car> findByYearAndIsActive(Integer year, Boolean isActive);

    /**
     * Find cars within year range
     */
    @Query("SELECT c FROM Car c WHERE c.year BETWEEN :minYear AND :maxYear AND c.isActive = true")
    List<Car> findByYearBetween(@Param("minYear") Integer minYear, @Param("maxYear") Integer maxYear);

    /**
     * Find cars by mileage less than or equal to
     */
    @Query("SELECT c FROM Car c WHERE c.mileage <= :maxMileage AND c.isActive = true")
    List<Car> findByMileageLessThanEqual(@Param("maxMileage") Integer maxMileage);

    /**
     * Advanced search with multiple filters
     */
    @Query("SELECT c FROM Car c WHERE " +
           "(:make IS NULL OR LOWER(c.make) LIKE LOWER(CONCAT('%', :make, '%'))) AND " +
           "(:model IS NULL OR LOWER(c.model) LIKE LOWER(CONCAT('%', :model, '%'))) AND " +
           "(:fuelType IS NULL OR c.fuelType = :fuelType) AND " +
           "(:transmission IS NULL OR c.transmission = :transmission) AND " +
           "(:vehicleType IS NULL OR c.vehicleType = :vehicleType) AND " +
           "(:condition IS NULL OR c.condition = :condition) AND " +
           "(:minPrice IS NULL OR c.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR c.price <= :maxPrice) AND " +
           "(:minYear IS NULL OR c.year >= :minYear) AND " +
           "(:maxYear IS NULL OR c.year <= :maxYear) AND " +
           "(:maxMileage IS NULL OR c.mileage <= :maxMileage) AND " +
           "c.isActive = true")
    Page<Car> findCarsWithFilters(
        @Param("make") String make,
        @Param("model") String model,
        @Param("fuelType") Car.FuelType fuelType,
        @Param("transmission") Car.Transmission transmission,
        @Param("vehicleType") Car.VehicleType vehicleType,
        @Param("condition") Car.Condition condition,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("minYear") Integer minYear,
        @Param("maxYear") Integer maxYear,
        @Param("maxMileage") Integer maxMileage,
        Pageable pageable);

    /**
     * Find similar cars based on make, model, year, and price range
     */
    @Query("SELECT c FROM Car c WHERE " +
           "c.id != :carId AND " +
           "LOWER(c.make) = LOWER(:make) AND " +
           "c.year BETWEEN :minYear AND :maxYear AND " +
           "c.price BETWEEN :minPrice AND :maxPrice AND " +
           "c.isActive = true " +
           "ORDER BY " +
           "CASE WHEN LOWER(c.model) = LOWER(:model) THEN 1 ELSE 2 END, " +
           "ABS(c.year - :year), " +
           "ABS(c.price - :price)")
    List<Car> findSimilarCars(
        @Param("carId") UUID carId,
        @Param("make") String make,
        @Param("model") String model,
        @Param("year") Integer year,
        @Param("minYear") Integer minYear,
        @Param("maxYear") Integer maxYear,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("price") BigDecimal price,
        Pageable pageable);

    /**
     * Count cars by dealer
     */
    long countByDealerAndIsActive(Dealer dealer, Boolean isActive);

    /**
     * Count featured cars
     */
    long countByIsFeaturedAndIsActive(Boolean isFeatured, Boolean isActive);

    /**
     * Find recently added cars
     */
    @Query("SELECT c FROM Car c WHERE c.isActive = true ORDER BY c.createdAt DESC")
    List<Car> findRecentlyAddedCars(Pageable pageable);

    /**
     * Find cars by dealer with pagination
     */
    Page<Car> findByDealerAndIsActive(Dealer dealer, Boolean isActive, Pageable pageable);

    /**
     * Search cars by text in make, model, or description
     */
    @Query("SELECT c FROM Car c WHERE " +
           "(LOWER(c.make) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(c.model) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :searchText, '%'))) AND " +
           "c.isActive = true")
    List<Car> searchCarsByText(@Param("searchText") String searchText, Pageable pageable);

    /**
     * Count active cars
     */
    long countByIsActiveTrue();

    /**
     * Find active cars with pagination
     */
    Page<Car> findByIsActiveTrue(Pageable pageable);
}