package com.crashedcarsales.service;

import com.crashedcarsales.dto.CarCreateRequest;
import com.crashedcarsales.dto.CarResponse;
import com.crashedcarsales.dto.CarSearchRequest;
import com.crashedcarsales.dto.CarUpdateRequest;
import com.crashedcarsales.entity.*;
import com.crashedcarsales.repository.CarFeaturesRepository;
import com.crashedcarsales.repository.CarImageRepository;
import com.crashedcarsales.repository.CarRepository;
import com.crashedcarsales.repository.DealerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CarService {

    private static final Logger logger = LoggerFactory.getLogger(CarService.class);

    private final CarRepository carRepository;
    private final CarImageRepository carImageRepository;
    private final CarFeaturesRepository carFeaturesRepository;
    private final DealerRepository dealerRepository;
    // private final CacheService cacheService;

    @Autowired
    public CarService(CarRepository carRepository,
                     CarImageRepository carImageRepository,
                     CarFeaturesRepository carFeaturesRepository,
                     DealerRepository dealerRepository
                     // CacheService cacheService
                     ) {
        this.carRepository = carRepository;
        this.carImageRepository = carImageRepository;
        this.carFeaturesRepository = carFeaturesRepository;
        this.dealerRepository = dealerRepository;
        // this.cacheService = cacheService;
    }

    /**
     * Create a new car
     */
    public CarResponse createCar(UUID dealerId, CarCreateRequest request) {
        logger.info("Creating new car for dealer ID: {}", dealerId);

        // Get dealer
        Dealer dealer = dealerRepository.findById(dealerId)
            .orElseThrow(() -> new RuntimeException("Dealer not found with ID: " + dealerId));

        // Create car entity
        Car car = new Car();
        car.setDealer(dealer);
        car.setMake(request.getMake());
        car.setModel(request.getModel());
        car.setYear(request.getYear());
        car.setFuelType(request.getFuelType());
        car.setTransmission(request.getTransmission());
        car.setVehicleType(request.getVehicleType());
        car.setCondition(request.getCondition());
        car.setPrice(request.getPrice());
        car.setMileage(request.getMileage());
        car.setDescription(request.getDescription());
        car.setIsFeatured(request.getIsFeatured());
        car.setIsActive(true);

        // Save car first
        Car savedCar = carRepository.save(car);
        logger.info("Car created successfully with ID: {}", savedCar.getId());

        // Create and save features if provided
        if (hasAnyFeatureSet(request)) {
            CarFeatures features = new CarFeatures(savedCar);
            features.setAirbags(request.getAirbags());
            features.setAbsBrakes(request.getAbsBrakes());
            features.setAirConditioning(request.getAirConditioning());
            features.setPowerSteering(request.getPowerSteering());
            features.setCentralLocking(request.getCentralLocking());
            features.setElectricWindows(request.getElectricWindows());

            carFeaturesRepository.save(features);
            savedCar.setFeatures(features);
        }

        // Add images if provided
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                String imageUrl = request.getImageUrls().get(i);
                CarImage image = new CarImage(savedCar, imageUrl, null, i);
                carImageRepository.save(image);
                savedCar.getImages().add(image);
            }
        }

        // Invalidate search and featured caches since new car was added
        // cacheService.invalidateSearchCaches();
        // if (Boolean.TRUE.equals(savedCar.getIsFeatured())) {
        //     cacheService.evict("featured-cars", "all");
        // }

        return CarResponse.fromEntity(savedCar);
    }

    /**
     * Get car by ID
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "car-details", key = "#carId")
    public Optional<CarResponse> getCarById(UUID carId) {
        logger.debug("Getting car by ID: {}", carId);

        return carRepository.findById(carId)
            .filter(Car::getIsActive)
            .map(CarResponse::fromEntity);
    }

    /**
     * Update car
     */
    @CacheEvict(value = "car-details", key = "#carId")
    public CarResponse updateCar(UUID carId, CarUpdateRequest request) {
        logger.info("Updating car with ID: {}", carId);

        Car car = carRepository.findById(carId)
            .orElseThrow(() -> new RuntimeException("Car not found with ID: " + carId));

        // Update basic fields if provided
        if (request.getMake() != null) car.setMake(request.getMake());
        if (request.getModel() != null) car.setModel(request.getModel());
        if (request.getYear() != null) car.setYear(request.getYear());
        if (request.getFuelType() != null) car.setFuelType(request.getFuelType());
        if (request.getTransmission() != null) car.setTransmission(request.getTransmission());
        if (request.getVehicleType() != null) car.setVehicleType(request.getVehicleType());
        if (request.getCondition() != null) car.setCondition(request.getCondition());
        if (request.getPrice() != null) car.setPrice(request.getPrice());
        if (request.getMileage() != null) car.setMileage(request.getMileage());
        if (request.getDescription() != null) car.setDescription(request.getDescription());
        if (request.getIsFeatured() != null) car.setIsFeatured(request.getIsFeatured());
        if (request.getIsActive() != null) car.setIsActive(request.getIsActive());

        // Update features if provided
        if (car.getFeatures() != null && hasAnyFeatureUpdate(request)) {
            if (request.getAirbags() != null) car.getFeatures().setAirbags(request.getAirbags());
            if (request.getAbsBrakes() != null) car.getFeatures().setAbsBrakes(request.getAbsBrakes());
            if (request.getAirConditioning() != null) car.getFeatures().setAirConditioning(request.getAirConditioning());
            if (request.getPowerSteering() != null) car.getFeatures().setPowerSteering(request.getPowerSteering());
            if (request.getCentralLocking() != null) car.getFeatures().setCentralLocking(request.getCentralLocking());
            if (request.getElectricWindows() != null) car.getFeatures().setElectricWindows(request.getElectricWindows());
        }

        // Add new images if provided
        if (request.getImageUrlsToAdd() != null && !request.getImageUrlsToAdd().isEmpty()) {
            int currentMaxOrder = car.getImages().isEmpty() ? 0 :
                car.getImages().stream().mapToInt(CarImage::getDisplayOrder).max().orElse(0);

            for (String imageUrl : request.getImageUrlsToAdd()) {
                CarImage image = new CarImage(car, imageUrl, null, ++currentMaxOrder);
                carImageRepository.save(image);
                car.getImages().add(image);
            }
        }

        // Remove images if provided
        if (request.getImageIdsToRemove() != null && !request.getImageIdsToRemove().isEmpty()) {
            for (UUID imageId : request.getImageIdsToRemove()) {
                carImageRepository.findById(imageId)
                    .ifPresent(image -> {
                        if (image.getCar().getId().equals(carId)) {
                            car.getImages().remove(image);
                            carImageRepository.delete(image);
                        }
                    });
            }
        }

        Car savedCar = carRepository.save(car);

        // Invalidate related caches
        // cacheService.invalidateCarCaches(carId.toString());

        logger.info("Car updated successfully with ID: {}", savedCar.getId());

        return CarResponse.fromEntity(savedCar);
    }

    /**
     * Delete car (soft delete by setting isActive to false)
     */
    @CacheEvict(value = "car-details", key = "#carId")
    public void deleteCar(UUID carId) {
        logger.info("Deleting car with ID: {}", carId);

        Car car = carRepository.findById(carId)
            .orElseThrow(() -> new RuntimeException("Car not found with ID: " + carId));

        car.setIsActive(false);
        carRepository.save(car);

        // Invalidate related caches
        // cacheService.invalidateCarCaches(carId.toString());

        logger.info("Car deleted successfully with ID: {}", carId);
    }

    /**
     * Search cars with filters
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "car-search", key = "#searchRequest.toString()")
    public Page<CarResponse> searchCars(CarSearchRequest searchRequest) {
        logger.debug("Searching cars with filters: {}", searchRequest);

        Pageable pageable = searchRequest.toPageable();

        Page<Car> cars = carRepository.findCarsWithFilters(
            searchRequest.getMake(),
            searchRequest.getModel(),
            searchRequest.getFuelType(),
            searchRequest.getTransmission(),
            searchRequest.getVehicleType(),
            searchRequest.getCondition(),
            searchRequest.getMinPrice(),
            searchRequest.getMaxPrice(),
            searchRequest.getMinYear(),
            searchRequest.getMaxYear(),
            searchRequest.getMaxMileage(),
            pageable
        );

        return cars.map(CarResponse::fromEntity);
    }

    /**
     * Get cars by dealer
     */
    @Transactional(readOnly = true)
    public List<CarResponse> getCarsByDealer(UUID dealerId) {
        logger.debug("Getting cars for dealer ID: {}", dealerId);

        Dealer dealer = dealerRepository.findById(dealerId)
            .orElseThrow(() -> new RuntimeException("Dealer not found with ID: " + dealerId));

        return carRepository.findByDealerAndIsActive(dealer, true)
            .stream()
            .map(CarResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Get featured cars
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "featured-cars", key = "'all'")
    public List<CarResponse> getFeaturedCars() {
        logger.debug("Getting featured cars");

        return carRepository.findByIsFeaturedAndIsActive(true, true)
            .stream()
            .map(CarResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Get recently added cars
     */
    @Transactional(readOnly = true)
    public List<CarResponse> getRecentlyAddedCars(int limit) {
        logger.debug("Getting recently added cars, limit: {}", limit);

        return carRepository.findRecentlyAddedCars(PageRequest.of(0, limit))
            .stream()
            .map(CarResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Find similar cars
     */
    @Transactional(readOnly = true)
    public List<CarResponse> findSimilarCars(UUID carId, int limit) {
        logger.debug("Finding similar cars for car ID: {}, limit: {}", carId, limit);

        Car car = carRepository.findById(carId)
            .orElseThrow(() -> new RuntimeException("Car not found with ID: " + carId));

        // Calculate price range (±20%)
        BigDecimal price = car.getPrice();
        BigDecimal priceRange = price.multiply(BigDecimal.valueOf(0.2));
        BigDecimal minPrice = price.subtract(priceRange);
        BigDecimal maxPrice = price.add(priceRange);

        // Calculate year range (±2 years)
        Integer year = car.getYear();
        Integer minYear = year - 2;
        Integer maxYear = year + 2;

        List<Car> similarCars = carRepository.findSimilarCars(
            carId,
            car.getMake(),
            car.getModel(),
            car.getYear(),
            minYear,
            maxYear,
            minPrice,
            maxPrice,
            price,
            PageRequest.of(0, limit)
        );

        return similarCars.stream()
            .map(CarResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Get cars by make
     */
    @Transactional(readOnly = true)
    public List<CarResponse> getCarsByMake(String make) {
        logger.debug("Getting cars by make: {}", make);

        return carRepository.findByMakeIgnoreCase(make)
            .stream()
            .map(CarResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Get cars by fuel type
     */
    @Transactional(readOnly = true)
    public List<CarResponse> getCarsByFuelType(Car.FuelType fuelType) {
        logger.debug("Getting cars by fuel type: {}", fuelType);

        return carRepository.findByFuelTypeAndIsActive(fuelType, true)
            .stream()
            .map(CarResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Search cars by text
     */
    @Transactional(readOnly = true)
    public List<CarResponse> searchCarsByText(String searchText, int limit) {
        logger.debug("Searching cars by text: {}, limit: {}", searchText, limit);

        return carRepository.searchCarsByText(searchText, PageRequest.of(0, limit))
            .stream()
            .map(CarResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Get car count by dealer
     */
    @Transactional(readOnly = true)
    public long getCarCountByDealer(UUID dealerId) {
        Dealer dealer = dealerRepository.findById(dealerId)
            .orElseThrow(() -> new RuntimeException("Dealer not found with ID: " + dealerId));

        return carRepository.countByDealerAndIsActive(dealer, true);
    }

    /**
     * Get all active cars with pagination
     */
    @Transactional(readOnly = true)
    public Page<CarResponse> getAllActiveCars(Pageable pageable) {
        logger.debug("Getting all active cars with pagination");

        Page<Car> cars = carRepository.findByIsActiveTrue(pageable);

        return cars.map(CarResponse::fromEntity);
    }

    /**
     * Search cars with filters (public API version)
     */
    @Transactional(readOnly = true)
    public Page<CarResponse> searchCarsWithFilters(
            String make, String model, Car.FuelType fuelType, Car.Transmission transmission,
            Car.VehicleType vehicleType, Car.Condition condition,
            BigDecimal minPrice, BigDecimal maxPrice, Integer minYear, Integer maxYear,
            Integer maxMileage, Pageable pageable) {

        logger.debug("Searching cars with filters via public API");

        Page<Car> cars = carRepository.findCarsWithFilters(
            make, model, fuelType, transmission, vehicleType, condition,
            minPrice, maxPrice, minYear, maxYear, maxMileage, pageable);

        return cars.map(CarResponse::fromEntity);
    }

    /**
     * Get similar cars (public API version)
     */
    @Transactional(readOnly = true)
    public List<CarResponse> getSimilarCars(UUID carId) {
        return findSimilarCars(carId, 10); // Default limit of 10
    }

    // Helper methods
    private boolean hasAnyFeatureSet(CarCreateRequest request) {
        return request.getAirbags() != null && request.getAirbags() ||
               request.getAbsBrakes() != null && request.getAbsBrakes() ||
               request.getAirConditioning() != null && request.getAirConditioning() ||
               request.getPowerSteering() != null && request.getPowerSteering() ||
               request.getCentralLocking() != null && request.getCentralLocking() ||
               request.getElectricWindows() != null && request.getElectricWindows();
    }

    private boolean hasAnyFeatureUpdate(CarUpdateRequest request) {
        return request.getAirbags() != null ||
               request.getAbsBrakes() != null ||
               request.getAirConditioning() != null ||
               request.getPowerSteering() != null ||
               request.getCentralLocking() != null ||
               request.getElectricWindows() != null;
    }
}