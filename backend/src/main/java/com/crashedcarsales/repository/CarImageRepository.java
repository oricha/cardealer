package com.crashedcarsales.repository;

import com.crashedcarsales.entity.Car;
import com.crashedcarsales.entity.CarImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarImageRepository extends JpaRepository<CarImage, UUID> {

    /**
     * Find images by car
     */
    List<CarImage> findByCar(Car car);

    /**
     * Find images by car ordered by display order
     */
    @Query("SELECT ci FROM CarImage ci WHERE ci.car = :car ORDER BY ci.displayOrder ASC, ci.createdAt ASC")
    List<CarImage> findByCarOrderByDisplayOrderAsc(@Param("car") Car car);

    /**
     * Find first image for a car (primary image)
     */
    @Query("SELECT ci FROM CarImage ci WHERE ci.car = :car ORDER BY ci.displayOrder ASC, ci.createdAt ASC LIMIT 1")
    Optional<CarImage> findFirstImageByCar(@Param("car") Car car);

    /**
     * Find images by car ID
     */
    @Query("SELECT ci FROM CarImage ci WHERE ci.car.id = :carId ORDER BY ci.displayOrder ASC, ci.createdAt ASC")
    List<CarImage> findByCarIdOrderByDisplayOrderAsc(@Param("carId") UUID carId);

    /**
     * Count images for a car
     */
    long countByCar(Car car);

    /**
     * Delete images by car
     */
    void deleteByCar(Car car);

    /**
     * Check if image URL exists for a car
     */
    boolean existsByCarAndImageUrl(Car car, String imageUrl);
}