package com.crashedcarsales.controller;

import com.crashedcarsales.dto.ApiResponse;
import com.crashedcarsales.dto.CarResponse;
import com.crashedcarsales.dto.DealerProfile;
import com.crashedcarsales.dto.DealerStats;
import com.crashedcarsales.entity.Car;
import com.crashedcarsales.service.CarService;
import com.crashedcarsales.service.DealerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PublicApiControllerTest {

    @Mock
    private CarService carService;

    @Mock
    private DealerService dealerService;

    @InjectMocks
    private PublicApiController publicApiController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private CarResponse testCarResponse;
    private DealerProfile testDealerProfile;
    private UUID testCarId;
    private UUID testDealerId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(publicApiController).build();
        objectMapper = new ObjectMapper();
        testCarId = UUID.randomUUID();
        testDealerId = UUID.randomUUID();

        // Create test car response
        testCarResponse = new CarResponse();
        testCarResponse.setId(testCarId);
        testCarResponse.setMake("Toyota");
        testCarResponse.setModel("Camry");
        testCarResponse.setYear(2020);
        testCarResponse.setPrice(new BigDecimal("15000.00"));
        testCarResponse.setFuelType(Car.FuelType.GAS);
        testCarResponse.setTransmission(Car.Transmission.AUTOMATIC);
        testCarResponse.setVehicleType(Car.VehicleType.PASSENGER);
        testCarResponse.setCondition(Car.Condition.USED);
        testCarResponse.setMileage(50000);
        testCarResponse.setIsActive(true);
        testCarResponse.setIsFeatured(false);

        // Create test dealer profile
        testDealerProfile = new DealerProfile();
        testDealerProfile.setId(testDealerId);
        testDealerProfile.setName("Test Dealer");
        testDealerProfile.setEmail("dealer@test.com");
        testDealerProfile.setPhone("123-456-7890");
    }

    @Test
    void getAllCars_WithValidParameters_ShouldReturnCarsPage() throws Exception {
        // Given
        Page<CarResponse> carPage = new PageImpl<>(
            List.of(testCarResponse),
            PageRequest.of(0, 20),
            1
        );
        when(carService.getAllActiveCars(any())).thenReturn(carPage);

        // When & Then
        mockMvc.perform(get("/api/public/cars")
                .param("page", "0")
                .param("size", "20")
                .param("sortBy", "createdAt")
                .param("sortDir", "desc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cars retrieved successfully"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].id").value(testCarId.toString()))
                .andExpect(jsonPath("$.data.content[0].make").value("Toyota"))
                .andExpect(jsonPath("$.data.content[0].model").value("Camry"))
                .andExpect(jsonPath("$.data.totalElements").value(1));

        verify(carService).getAllActiveCars(any());
    }

    @Test
    void getAllCars_WithDefaultParameters_ShouldReturnCarsPage() throws Exception {
        // Given
        Page<CarResponse> carPage = new PageImpl<>(
            List.of(testCarResponse),
            PageRequest.of(0, 20),
            1
        );
        when(carService.getAllActiveCars(any())).thenReturn(carPage);

        // When & Then
        mockMvc.perform(get("/api/public/cars")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].id").value(testCarId.toString()));

        verify(carService).getAllActiveCars(any());
    }

    @Test
    void getCarById_WithValidId_ShouldReturnCar() throws Exception {
        // Given
        when(carService.getCarById(testCarId)).thenReturn(Optional.of(testCarResponse));

        // When & Then
        mockMvc.perform(get("/api/public/cars/{carId}", testCarId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Car retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value(testCarId.toString()))
                .andExpect(jsonPath("$.data.make").value("Toyota"))
                .andExpect(jsonPath("$.data.model").value("Camry"))
                .andExpect(jsonPath("$.data.price").value(15000.00));

        verify(carService).getCarById(testCarId);
    }

    @Test
    void getCarById_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(carService.getCarById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/public/cars/{carId}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("CAR_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Car not found"));

        verify(carService).getCarById(nonExistentId);
    }

    @Test
    void searchCars_WithMultipleFilters_ShouldReturnFilteredCars() throws Exception {
        // Given
        Page<CarResponse> carPage = new PageImpl<>(
            List.of(testCarResponse),
            PageRequest.of(0, 20),
            1
        );
        when(carService.searchCarsWithFilters(
                eq("Toyota"), eq("Camry"), eq(Car.FuelType.GAS),
                eq(Car.Transmission.AUTOMATIC), eq(Car.VehicleType.PASSENGER),
                eq(Car.Condition.USED), any(), any(), any(), any(), any(), any()))
                .thenReturn(carPage);

        // When & Then
        mockMvc.perform(get("/api/public/cars/search")
                .param("make", "Toyota")
                .param("model", "Camry")
                .param("fuelType", "GAS")
                .param("transmission", "AUTOMATIC")
                .param("vehicleType", "PASSENGER")
                .param("condition", "USED")
                .param("minPrice", "10000")
                .param("maxPrice", "20000")
                .param("minYear", "2018")
                .param("maxYear", "2022")
                .param("maxMileage", "100000")
                .param("page", "0")
                .param("size", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Search completed successfully"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].make").value("Toyota"));

        verify(carService).searchCarsWithFilters(
                eq("Toyota"), eq("Camry"), eq(Car.FuelType.GAS),
                eq(Car.Transmission.AUTOMATIC), eq(Car.VehicleType.PASSENGER),
                eq(Car.Condition.USED), any(), any(), any(), any(), any(), any());
    }

    @Test
    void searchCars_WithMinimalFilters_ShouldReturnFilteredCars() throws Exception {
        // Given
        Page<CarResponse> carPage = new PageImpl<>(
            List.of(testCarResponse),
            PageRequest.of(0, 20),
            1
        );
        when(carService.searchCarsWithFilters(
                eq("Toyota"), isNull(), isNull(), isNull(), isNull(),
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), any()))
                .thenReturn(carPage);

        // When & Then
        mockMvc.perform(get("/api/public/cars/search")
                .param("make", "Toyota")
                .param("page", "0")
                .param("size", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(carService).searchCarsWithFilters(
                eq("Toyota"), isNull(), isNull(), isNull(), isNull(),
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), any());
    }

    @Test
    void getFeaturedCars_ShouldReturnFeaturedCars() throws Exception {
        // Given
        List<CarResponse> featuredCars = List.of(testCarResponse);
        when(carService.getFeaturedCars()).thenReturn(featuredCars);

        // When & Then
        mockMvc.perform(get("/api/public/cars/featured")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Featured cars retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(testCarId.toString()));

        verify(carService).getFeaturedCars();
    }

    @Test
    void getRecentCars_WithValidLimit_ShouldReturnRecentCars() throws Exception {
        // Given
        List<CarResponse> recentCars = List.of(testCarResponse);
        when(carService.getRecentlyAddedCars(5)).thenReturn(recentCars);

        // When & Then
        mockMvc.perform(get("/api/public/cars/recent")
                .param("limit", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Recent cars retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(testCarId.toString()));

        verify(carService).getRecentlyAddedCars(5);
    }

    @Test
    void getRecentCars_WithDefaultLimit_ShouldReturnRecentCars() throws Exception {
        // Given
        List<CarResponse> recentCars = List.of(testCarResponse);
        when(carService.getRecentlyAddedCars(10)).thenReturn(recentCars);

        // When & Then
        mockMvc.perform(get("/api/public/cars/recent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(carService).getRecentlyAddedCars(10);
    }

    @Test
    void getDealerProfile_WithValidId_ShouldReturnDealerProfile() throws Exception {
        // Given
        when(dealerService.getDealerProfileById(testDealerId)).thenReturn(Optional.of(testDealerProfile));

        // When & Then
        mockMvc.perform(get("/api/public/dealers/{dealerId}", testDealerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Dealer profile retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value(testDealerId.toString()))
                .andExpect(jsonPath("$.data.name").value("Test Dealer"))
                .andExpect(jsonPath("$.data.email").value("dealer@test.com"));

        verify(dealerService).getDealerProfileById(testDealerId);
    }

    @Test
    void getDealerProfile_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(dealerService.getDealerProfileById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/public/dealers/{dealerId}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(dealerService).getDealerProfileById(nonExistentId);
    }

    @Test
    void getDealerStats_WithValidId_ShouldReturnDealerStats() throws Exception {
        // Given
        DealerStats mockStats = new DealerStats(testDealerId, "Test Dealer", 10L, 5L, new BigDecimal("50000.00"));
        when(dealerService.getDealerStatistics(testDealerId)).thenReturn(mockStats);

        // When & Then
        mockMvc.perform(get("/api/public/dealers/{dealerId}/stats", testDealerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Dealer statistics retrieved successfully"));

        verify(dealerService).getDealerStatistics(testDealerId);
    }

    @Test
    void searchDealers_WithValidPattern_ShouldReturnDealers() throws Exception {
        // Given
        List<DealerProfile> dealers = List.of(testDealerProfile);
        when(dealerService.searchDealersByName("Test")).thenReturn(dealers);

        // When & Then
        mockMvc.perform(get("/api/public/dealers/search")
                .param("pattern", "Test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Dealers retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("Test Dealer"));

        verify(dealerService).searchDealersByName("Test");
    }

    @Test
    void searchDealers_WithoutPattern_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/public/dealers/search")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(dealerService, never()).searchDealersByName(anyString());
    }

    @Test
    void getDealerCars_WithValidId_ShouldReturnDealerCars() throws Exception {
        // Given
        List<CarResponse> dealerCars = List.of(testCarResponse);
        when(carService.getCarsByDealer(testDealerId)).thenReturn(dealerCars);

        // When & Then
        mockMvc.perform(get("/api/public/dealers/{dealerId}/cars", testDealerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Dealer cars retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(testCarId.toString()));

        verify(carService).getCarsByDealer(testDealerId);
    }

    @Test
    void getSimilarCars_WithValidId_ShouldReturnSimilarCars() throws Exception {
        // Given
        List<CarResponse> similarCars = List.of(testCarResponse);
        when(carService.getSimilarCars(testCarId)).thenReturn(similarCars);

        // When & Then
        mockMvc.perform(get("/api/public/cars/{carId}/similar", testCarId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Similar cars retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(testCarId.toString()));

        verify(carService).getSimilarCars(testCarId);
    }

    @Test
    void getAllCars_WithServiceException_ShouldReturnInternalServerError() throws Exception {
        // Given
        when(carService.getAllActiveCars(any())).thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/api/public/cars")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.message").value("Failed to retrieve cars"));

        verify(carService).getAllActiveCars(any());
    }

    @Test
    void searchCars_WithServiceException_ShouldReturnInternalServerError() throws Exception {
        // Given
        when(carService.searchCarsWithFilters(any(), any(), any(), any(), any(), any(),
                any(), any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Search error"));

        // When & Then
        mockMvc.perform(get("/api/public/cars/search")
                .param("make", "Toyota")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("SEARCH_ERROR"))
                .andExpect(jsonPath("$.message").value("Search failed"));

        verify(carService).searchCarsWithFilters(any(), any(), any(), any(), any(), any(),
                any(), any(), any(), any(), any(), any());
    }
}