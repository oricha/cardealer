package com.crashedcarsales.service;

import com.crashedcarsales.dto.FavoritesResponse;
import com.crashedcarsales.entity.Car;
import com.crashedcarsales.entity.Dealer;
import com.crashedcarsales.entity.Favorites;
import com.crashedcarsales.entity.User;
import com.crashedcarsales.repository.CarRepository;
import com.crashedcarsales.repository.FavoritesRepository;
import com.crashedcarsales.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoritesServiceTest {

    @Mock
    private FavoritesRepository favoritesRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private FavoritesService favoritesService;

    private User testUser;
    private Car testCar;
    private Dealer testDealer;
    private Favorites testFavorite;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("buyer@example.com");
        testUser.setPasswordHash("hashedpassword");
        testUser.setRole(User.Role.BUYER);
        testUser.setIsActive(true);

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
        testCar.setIsActive(true);

        // Create test favorite
        testFavorite = new Favorites();
        testFavorite.setId(UUID.randomUUID());
        testFavorite.setUser(testUser);
        testFavorite.setCar(testCar);
        testFavorite.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void addToFavorites_WithValidData_ShouldCreateFavoriteSuccessfully() {
        // Given
        UUID userId = testUser.getId();
        UUID carId = testCar.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(carRepository.findById(carId)).thenReturn(Optional.of(testCar));
        when(favoritesRepository.existsByUserAndCar(testUser, testCar)).thenReturn(false);
        when(favoritesRepository.save(any(Favorites.class))).thenReturn(testFavorite);

        // When
        FavoritesResponse result = favoritesService.addToFavorites(userId, carId);

        // Then
        assertNotNull(result);
        assertEquals(testFavorite.getId(), result.getId());
        assertEquals(testCar.getId(), result.getCarId());
        assertEquals(testCar.getMake(), result.getCarMake());

        verify(userRepository).findById(userId);
        verify(carRepository).findById(carId);
        verify(favoritesRepository).existsByUserAndCar(testUser, testCar);
        verify(favoritesRepository).save(any(Favorites.class));
    }

    @Test
    void addToFavorites_WithNonExistentUser_ShouldThrowException() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID carId = testCar.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> favoritesService.addToFavorites(userId, carId));

        assertEquals("User not found with ID: " + userId, exception.getMessage());
        verify(userRepository).findById(userId);
        verify(carRepository, never()).findById(any());
        verify(favoritesRepository, never()).save(any());
    }

    @Test
    void addToFavorites_WithInactiveCar_ShouldThrowException() {
        // Given
        UUID userId = testUser.getId();
        UUID carId = testCar.getId();
        testCar.setIsActive(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(carRepository.findById(carId)).thenReturn(Optional.of(testCar));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> favoritesService.addToFavorites(userId, carId));

        assertEquals("Car not found or inactive with ID: " + carId, exception.getMessage());
        verify(userRepository).findById(userId);
        verify(carRepository).findById(carId);
        verify(favoritesRepository, never()).save(any());
    }

    @Test
    void addToFavorites_WithAlreadyFavoritedCar_ShouldThrowException() {
        // Given
        UUID userId = testUser.getId();
        UUID carId = testCar.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(carRepository.findById(carId)).thenReturn(Optional.of(testCar));
        when(favoritesRepository.existsByUserAndCar(testUser, testCar)).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> favoritesService.addToFavorites(userId, carId));

        assertEquals("Car is already in favorites", exception.getMessage());
        verify(favoritesRepository).existsByUserAndCar(testUser, testCar);
        verify(favoritesRepository, never()).save(any());
    }

    @Test
    void removeFromFavorites_WithValidData_ShouldRemoveSuccessfully() {
        // Given
        UUID userId = testUser.getId();
        UUID carId = testCar.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(carRepository.findById(carId)).thenReturn(Optional.of(testCar));
        when(favoritesRepository.findByUserAndCar(testUser, testCar)).thenReturn(Optional.of(testFavorite));

        // When
        favoritesService.removeFromFavorites(userId, carId);

        // Then
        verify(userRepository).findById(userId);
        verify(carRepository).findById(carId);
        verify(favoritesRepository).findByUserAndCar(testUser, testCar);
        verify(favoritesRepository).delete(testFavorite);
    }

    @Test
    void removeFromFavorites_WithNonExistentFavorite_ShouldThrowException() {
        // Given
        UUID userId = testUser.getId();
        UUID carId = testCar.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(carRepository.findById(carId)).thenReturn(Optional.of(testCar));
        when(favoritesRepository.findByUserAndCar(testUser, testCar)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> favoritesService.removeFromFavorites(userId, carId));

        assertEquals("Car is not in favorites", exception.getMessage());
        verify(favoritesRepository).findByUserAndCar(testUser, testCar);
        verify(favoritesRepository, never()).delete(any());
    }

    @Test
    void toggleFavorite_AddToFavorites_ShouldAddSuccessfully() {
        // Given
        UUID userId = testUser.getId();
        UUID carId = testCar.getId();

        when(favoritesRepository.existsByUserIdAndCarId(userId, carId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(carRepository.findById(carId)).thenReturn(Optional.of(testCar));
        when(favoritesRepository.save(any(Favorites.class))).thenReturn(testFavorite);

        // When
        FavoritesResponse result = favoritesService.toggleFavorite(userId, carId);

        // Then
        assertNotNull(result);
        verify(favoritesRepository).existsByUserIdAndCarId(userId, carId);
        verify(favoritesRepository).save(any(Favorites.class));
    }

    @Test
    void toggleFavorite_RemoveFromFavorites_ShouldRemoveSuccessfully() {
        // Given
        UUID userId = testUser.getId();
        UUID carId = testCar.getId();

        when(favoritesRepository.existsByUserIdAndCarId(userId, carId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(carRepository.findById(carId)).thenReturn(Optional.of(testCar));
        when(favoritesRepository.findByUserAndCar(testUser, testCar)).thenReturn(Optional.of(testFavorite));

        // When
        FavoritesResponse result = favoritesService.toggleFavorite(userId, carId);

        // Then
        assertNull(result); // Indicates removal
        verify(favoritesRepository).existsByUserIdAndCarId(userId, carId);
        verify(favoritesRepository).delete(testFavorite);
    }

    @Test
    void isInFavorites_WithFavoritedCar_ShouldReturnTrue() {
        // Given
        UUID userId = testUser.getId();
        UUID carId = testCar.getId();

        when(favoritesRepository.existsByUserIdAndCarId(userId, carId)).thenReturn(true);

        // When
        boolean result = favoritesService.isInFavorites(userId, carId);

        // Then
        assertTrue(result);
        verify(favoritesRepository).existsByUserIdAndCarId(userId, carId);
    }

    @Test
    void isInFavorites_WithNonFavoritedCar_ShouldReturnFalse() {
        // Given
        UUID userId = testUser.getId();
        UUID carId = testCar.getId();

        when(favoritesRepository.existsByUserIdAndCarId(userId, carId)).thenReturn(false);

        // When
        boolean result = favoritesService.isInFavorites(userId, carId);

        // Then
        assertFalse(result);
        verify(favoritesRepository).existsByUserIdAndCarId(userId, carId);
    }

    @Test
    void getUserFavorites_WithValidUser_ShouldReturnFavorites() {
        // Given
        UUID userId = testUser.getId();
        List<Favorites> favorites = List.of(testFavorite);

        when(favoritesRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(favorites);

        // When
        List<FavoritesResponse> result = favoritesService.getUserFavorites(userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testFavorite.getId(), result.get(0).getId());
        verify(favoritesRepository).findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Test
    void getUserFavoritesCount_WithValidUser_ShouldReturnCount() {
        // Given
        UUID userId = testUser.getId();
        long expectedCount = 5L;

        when(favoritesRepository.countByUserId(userId)).thenReturn(expectedCount);

        // When
        long result = favoritesService.getUserFavoritesCount(userId);

        // Then
        assertEquals(expectedCount, result);
        verify(favoritesRepository).countByUserId(userId);
    }

    @Test
    void getCarFavoritesCount_WithValidCar_ShouldReturnCount() {
        // Given
        UUID carId = testCar.getId();
        long expectedCount = 10L;

        when(favoritesRepository.countByCarId(carId)).thenReturn(expectedCount);

        // When
        long result = favoritesService.getCarFavoritesCount(carId);

        // Then
        assertEquals(expectedCount, result);
        verify(favoritesRepository).countByCarId(carId);
    }

    @Test
    void getPopularCars_WithValidLimit_ShouldReturnCarIds() {
        // Given
        int limit = 5;
        List<Object[]> popularCarsData = List.<Object[]>of(
            new Object[]{testCar.getId(), 10L}
        );

        when(favoritesRepository.findPopularCars(limit)).thenReturn(popularCarsData);

        // When
        List<UUID> result = favoritesService.getPopularCars(limit);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCar.getId(), result.get(0));
        verify(favoritesRepository).findPopularCars(limit);
    }

    @Test
    void removeAllFavoritesForUser_WithValidUser_ShouldRemoveAllFavorites() {
        // Given
        UUID userId = testUser.getId();
        List<Favorites> userFavorites = List.of(testFavorite);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(favoritesRepository.findByUser(testUser)).thenReturn(userFavorites);

        // When
        favoritesService.removeAllFavoritesForUser(userId);

        // Then
        verify(userRepository).findById(userId);
        verify(favoritesRepository).findByUser(testUser);
        verify(favoritesRepository).deleteAll(userFavorites);
    }

    @Test
    void removeAllFavoritesForCar_WithValidCar_ShouldRemoveAllFavorites() {
        // Given
        UUID carId = testCar.getId();
        List<Favorites> carFavorites = List.of(testFavorite);

        when(carRepository.findById(carId)).thenReturn(Optional.of(testCar));
        when(favoritesRepository.findByCarIdOrderByCreatedAtDesc(carId)).thenReturn(carFavorites);

        // When
        favoritesService.removeAllFavoritesForCar(carId);

        // Then
        verify(carRepository).findById(carId);
        verify(favoritesRepository).findByCarIdOrderByCreatedAtDesc(carId);
        verify(favoritesRepository).deleteAll(carFavorites);
    }

    @Test
    void canAddToFavorites_WithValidActiveCar_ShouldReturnTrue() {
        // Given
        UUID userId = testUser.getId();
        UUID carId = testCar.getId();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(carRepository.findById(carId)).thenReturn(Optional.of(testCar));

        // When
        boolean result = favoritesService.canAddToFavorites(userId, carId);

        // Then
        assertTrue(result);
        verify(userRepository).existsById(userId);
        verify(carRepository).findById(carId);
    }

    @Test
    void canAddToFavorites_WithInactiveCar_ShouldReturnFalse() {
        // Given
        UUID userId = testUser.getId();
        UUID carId = testCar.getId();
        testCar.setIsActive(false);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(carRepository.findById(carId)).thenReturn(Optional.of(testCar));

        // When
        boolean result = favoritesService.canAddToFavorites(userId, carId);

        // Then
        assertFalse(result);
        verify(userRepository).existsById(userId);
        verify(carRepository).findById(carId);
    }

    @Test
    void canAddToFavorites_WithNonExistentUser_ShouldReturnFalse() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID carId = testCar.getId();

        when(userRepository.existsById(userId)).thenReturn(false);

        // When
        boolean result = favoritesService.canAddToFavorites(userId, carId);

        // Then
        assertFalse(result);
        verify(userRepository).existsById(userId);
        verify(carRepository, never()).findById(any());
    }
}