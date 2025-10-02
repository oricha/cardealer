package com.crashedcarsales.repository;

import com.crashedcarsales.entity.Car;
import com.crashedcarsales.entity.CarFeatures;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarFeaturesRepository extends JpaRepository<CarFeatures, UUID> {

    /**
     * Find features by car
     */
    Optional<CarFeatures> findByCar(Car car);

    /**
     * Find features by car ID
     */
    @Query("SELECT cf FROM CarFeatures cf WHERE cf.car.id = :carId")
    Optional<CarFeatures> findByCarId(@Param("carId") UUID carId);

    /**
     * Check if features exist for a car
     */
    boolean existsByCar(Car car);

    /**
     * Delete features by car
     */
    void deleteByCar(Car car);

    /**
     * Find cars with specific features
     */
    @Query("SELECT cf FROM CarFeatures cf WHERE " +
           "(:airbags IS NULL OR cf.airbags = :airbags) AND " +
           "(:absBrakes IS NULL OR cf.absBrakes = :absBrakes) AND " +
           "(:airConditioning IS NULL OR cf.airConditioning = :airConditioning) AND " +
           "(:powerSteering IS NULL OR cf.powerSteering = :powerSteering) AND " +
           "(:centralLocking IS NULL OR cf.centralLocking = :centralLocking) AND " +
           "(:electricWindows IS NULL OR cf.electricWindows = :electricWindows)")
    List<CarFeatures> findByFeatures(
        @Param("airbags") Boolean airbags,
        @Param("absBrakes") Boolean absBrakes,
        @Param("airConditioning") Boolean airConditioning,
        @Param("powerSteering") Boolean powerSteering,
        @Param("centralLocking") Boolean centralLocking,
        @Param("electricWindows") Boolean electricWindows);

    /**
     * Count cars with specific features
     */
    @Query("SELECT COUNT(cf) FROM CarFeatures cf WHERE " +
           "(:airbags IS NULL OR cf.airbags = :airbags) AND " +
           "(:absBrakes IS NULL OR cf.absBrakes = :absBrakes) AND " +
           "(:airConditioning IS NULL OR cf.airConditioning = :airConditioning) AND " +
           "(:powerSteering IS NULL OR cf.powerSteering = :powerSteering) AND " +
           "(:centralLocking IS NULL OR cf.centralLocking = :centralLocking) AND " +
           "(:electricWindows IS NULL OR cf.electricWindows = :electricWindows)")
    long countByFeatures(
        @Param("airbags") Boolean airbags,
        @Param("absBrakes") Boolean absBrakes,
        @Param("airConditioning") Boolean airConditioning,
        @Param("powerSteering") Boolean powerSteering,
        @Param("centralLocking") Boolean centralLocking,
        @Param("electricWindows") Boolean electricWindows);
}