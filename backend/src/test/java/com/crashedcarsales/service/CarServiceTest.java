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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarImageRepository carImageRepository;

    @Mock
    private CarFeaturesRepository carFeaturesRepository;

    @Mock
    private DealerRepository dealerRepository;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private CarService carService;

    private Dealer testDealer;
    private Car testCar;
    private CarCreateRequest testCreateRequest;
    private CarUpdateRequest testUpdateRequest;

    @BeforeEach
    void setUp() {
        // Create test dealer
        testDealer = new Dealer();
        testDealer.setId(UUID.randomUUID());
        testDealer.setName("Test Dealer");
        testDealer.setUser(new User());

        // Create test car
        testCar = new Car();
        testCar.setId(UUID.randomUUID());
        testCar.setDealer(testDealer);
        testCar.setMake("Toyota");
        testCar.setModel("Camry");
        testCar.setYear(2020);
        testCar.setFuelType(Car.FuelType.GAS);
        testCar.setTransmission(Car.Transmission.AUTOMATIC);
        testCar.setVehicleType(Car.VehicleType.PASSENGER);
        testCar.setCondition(Car.Condition.USED);
        testCar.setPrice(new BigDecimal("25000.00"));
        testCar.setMileage(50000);
        testCar.setDescription("A reliable used car");
        testCar.setIsFeatured(false);
        testCar.setIsActive(true);
        testCar.setCreatedAt(LocalDateTime.now());
        testCar.setUpdatedAt(LocalDateTime.now());

        // Create test create request
        testCreateRequest = new CarCreateRequest();
        testCreateRequest.setMake("Honda");
        testCreateRequest.setModel("Civic");
        testCreateRequest.setYear(2021);
        testCreateRequest.setFuelType(Car.FuelType.GAS);
        testCreateRequest.setTransmission(Car.Transmission.MANUAL);
        testCreateRequest.setVehicleType(Car.VehicleType.PASSENGER);
        testCreateRequest.setCondition(Car.Condition.USED);
        testCreateRequest.setPrice(new BigDecimal("20000.00"));
        testCreateRequest.setMileage(30000);
        testCreateRequest.setDescription("A great economy car");
        testCreateRequest.setIsFeatured(true);
        testCreateRequest.setAirbags(true);
        testCreateRequest.setAbsBrakes(true);
        testCreateRequest.setAirConditioning(true);

        // Create test update request
        testUpdateRequest = new CarUpdateRequest();
        testUpdateRequest.setPrice(new BigDecimal("22000.00"));
        testUpdateRequest.setMileage(35000);
        testUpdateRequest.setDescription("Updated description");
        testUpdateRequest.setIsFeatured(true);
    }

    @Test
    void createCar_WithValidData_ShouldCreateCarSuccessfully() {
        // Given
        UUID dealerId = testDealer.getId();
        when(dealerRepository.findById(dealerId)).thenReturn(Optional.of(testDealer));
        when(carRepository.save(any(Car.class))).thenReturn(testCar);
        when(carFeaturesRepository.save(any(CarFeatures.class))).thenReturn(new CarFeatures());

        // When
        CarResponse result = carService.createCar(dealerId, testCreateRequest);

        // Then
        assertNotNull(result);
        verify(dealerRepository).findById(dealerId);
        verify(carRepository).save(any(Car.class));
        verify(carFeaturesRepository).save(any(CarFeatures.class));
    }

    @Test
    void createCar_WithNonExistentDealer_ShouldThrowException() {
        // Given
        UUID dealerId = UUID.randomUUID();
        when(dealerRepository.findById(dealerId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> carService.createCar(dealerId, testCreateRequest));

        assertEquals("Dealer not found with ID: " + dealerId, exception.getMessage());
        verify(dealerRepository).findById(dealerId);
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void getCarById_WithValidId_ShouldReturnCar() {
        // Given
        UUID carId = testCar.getId();
        when(carRepository.findById(carId)).thenReturn(Optional.of(testCar));

        // When
        Optional<CarResponse> result = carService.getCarById(carId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testCar.getId(), result.get().getId());
        assertEquals(testCar.getMake(), result.get().getMake());
        verify(carRepository).findById(carId);
    }

    @Test
    void getCarById_WithInactiveCar_ShouldReturnEmpty() {
        // Given
        UUID carId = testCar.getId();
        testCar.setIsActive(false);
        when(carRepository.findById(carId)).thenReturn(Optional.of(testCar));

        // When
        Optional<CarResponse> result = carService.getCarById(carId);

        // Then
        assertFalse(result.isPresent());
        verify(carRepository).findById(carId);
    }

    @Test
    void updateCar_WithValidData_ShouldUpdateSuccessfully() {
        // Given
        UUID carId = testCar.getId();
        when(carRepository.findById(carId)).thenReturn(Optional.of(testCar));
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        // When
        CarResponse result = carService.updateCar(carId, testUpdateRequest);

        // Then
        assertNotNull(result);
        verify(carRepository).findById(carId);
        verify(carRepository).save(testCar);
    }

    @Test
    void updateCar_WithNonExistentCar_ShouldThrowException() {
        // Given
        UUID carId = UUID.randomUUID();
        when(carRepository.findById(carId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> carService.updateCar(carId, testUpdateRequest));

        assertEquals("Car not found with ID: " + carId, exception.getMessage());
        verify(carRepository).findById(carId);
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void deleteCar_WithValidId_ShouldMarkAsInactive() {
        // Given
        UUID carId = testCar.getId();
        when(carRepository.findById(carId)).thenReturn(Optional.of(testCar));
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        // When
        carService.deleteCar(carId);

        // Then
        verify(carRepository).findById(carId);
        verify(carRepository).save(testCar);
        assertFalse(testCar.getIsActive());
    }

    @Test
    void searchCars_WithFilters_ShouldReturnPagedResults() {
        // Given
        CarSearchRequest searchRequest = new CarSearchRequest();
        searchRequest.setMake("Toyota");
        searchRequest.setMinPrice(new BigDecimal("20000"));
        searchRequest.setMaxPrice(new BigDecimal("30000"));

        List<Car> cars = List.of(testCar);
        Page<Car> carPage = new PageImpl<>(cars, PageRequest.of(0, 20), 1);
        when(carRepository.findCarsWithFilters(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
            .thenReturn(carPage);

        // When
        Page<CarResponse> result = carService.searchCars(searchRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(carRepository).findCarsWithFilters(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(Pageable.class));
    }

    @Test
    void getCarsByDealer_WithValidDealer_ShouldReturnCars() {
        // Given
        UUID dealerId = testDealer.getId();
        List<Car> cars = List.of(testCar);
        when(dealerRepository.findById(dealerId)).thenReturn(Optional.of(testDealer));
        when(carRepository.findByDealerAndIsActive(testDealer, true)).thenReturn(cars);

        // When
        List<CarResponse> result = carService.getCarsByDealer(dealerId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCar.getId(), result.get(0).getId());
        verify(dealerRepository).findById(dealerId);
        verify(carRepository).findByDealerAndIsActive(testDealer, true);
    }

    @Test
    void getFeaturedCars_ShouldReturnFeaturedCars() {
        // Given
        List<Car> featuredCars = List.of(testCar);
        when(carRepository.findByIsFeaturedAndIsActive(true, true)).thenReturn(featuredCars);

        // When
        List<CarResponse> result = carService.getFeaturedCars();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(carRepository).findByIsFeaturedAndIsActive(true, true);
    }

    @Test
    void getRecentlyAddedCars_WithValidLimit_ShouldReturnCars() {
        // Given
        int limit = 5;
        List<Car> recentCars = List.of(testCar);
        when(carRepository.findRecentlyAddedCars(any(PageRequest.class))).thenReturn(recentCars);

        // When
        List<CarResponse> result = carService.getRecentlyAddedCars(limit);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(carRepository).findRecentlyAddedCars(any(PageRequest.class));
    }

    @Test
    void findSimilarCars_WithValidCar_ShouldReturnSimilarCars() {
        // Given
        UUID carId = testCar.getId();
        List<Car> similarCars = List.of(testCar);
        when(carRepository.findById(carId)).thenReturn(Optional.of(testCar));
        when(carRepository.findSimilarCars(eq(carId), any(), any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
            .thenReturn(similarCars);

        // When
        List<CarResponse> result = carService.findSimilarCars(carId, 5);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(carRepository).findById(carId);
        verify(carRepository).findSimilarCars(eq(carId), any(), any(), any(), any(), any(), any(), any(), any(), any(Pageable.class));
    }

    @Test
    void findSimilarCars_WithNonExistentCar_ShouldThrowException() {
        // Given
        UUID carId = UUID.randomUUID();
        when(carRepository.findById(carId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> carService.findSimilarCars(carId, 5));

        assertEquals("Car not found with ID: " + carId, exception.getMessage());
        verify(carRepository).findById(carId);
        verify(carRepository, never()).findSimilarCars(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(Pageable.class));
    }

    @Test
    void getCarsByMake_WithValidMake_ShouldReturnCars() {
        // Given
        String make = "Toyota";
        List<Car> cars = List.of(testCar);
        when(carRepository.findByMakeIgnoreCase(make)).thenReturn(cars);

        // When
        List<CarResponse> result = carService.getCarsByMake(make);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(carRepository).findByMakeIgnoreCase(make);
    }

    @Test
    void getCarsByFuelType_WithValidFuelType_ShouldReturnCars() {
        // Given
        Car.FuelType fuelType = Car.FuelType.GAS;
        List<Car> cars = List.of(testCar);
        when(carRepository.findByFuelTypeAndIsActive(fuelType, true)).thenReturn(cars);

        // When
        List<CarResponse> result = carService.getCarsByFuelType(fuelType);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(carRepository).findByFuelTypeAndIsActive(fuelType, true);
    }

    @Test
    void searchCarsByText_WithValidText_ShouldReturnCars() {
        // Given
        String searchText = "Toyota Camry";
        List<Car> cars = List.of(testCar);
        when(carRepository.searchCarsByText(eq(searchText), any(PageRequest.class))).thenReturn(cars);

        // When
        List<CarResponse> result = carService.searchCarsByText(searchText, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(carRepository).searchCarsByText(eq(searchText), any(PageRequest.class));
    }

    @Test
    void getCarCountByDealer_WithValidDealer_ShouldReturnCount() {
        // Given
        UUID dealerId = testDealer.getId();
        long expectedCount = 5L;
        when(dealerRepository.findById(dealerId)).thenReturn(Optional.of(testDealer));
        when(carRepository.countByDealerAndIsActive(testDealer, true)).thenReturn(expectedCount);

        // When
        long result = carService.getCarCountByDealer(dealerId);

        // Then
        assertEquals(expectedCount, result);
        verify(dealerRepository).findById(dealerId);
        verify(carRepository).countByDealerAndIsActive(testDealer, true);
    }
}