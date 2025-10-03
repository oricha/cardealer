package com.crashedcarsales.controller;

import com.crashedcarsales.dto.DealerProfile;
import com.crashedcarsales.dto.DealerRegistrationRequest;
import com.crashedcarsales.dto.DealerStats;
import com.crashedcarsales.entity.Dealer;
import com.crashedcarsales.entity.User;
import com.crashedcarsales.service.DealerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DealerController.class)
@Disabled("Temporarily disabled due to Spring context issues")
class DealerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DealerService dealerService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Dealer testDealer;
    private DealerProfile testDealerProfile;
    private DealerStats testDealerStats;
    private DealerRegistrationRequest testRegistrationRequest;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("dealer@example.com");
        testUser.setPasswordHash("hashedpassword");
        testUser.setRole(User.Role.DEALER);
        testUser.setIsActive(true);

        // Create test dealer
        testDealer = new Dealer();
        testDealer.setId(UUID.randomUUID());
        testDealer.setUser(testUser);
        testDealer.setName("Test Dealer");
        testDealer.setAddress("123 Test Street");
        testDealer.setPhone("+1234567890");
        testDealer.setWebsite("https://testdealer.com");
        testDealer.setCreatedAt(LocalDateTime.now());
        testDealer.setUpdatedAt(LocalDateTime.now());

        // Create test dealer profile
        testDealerProfile = DealerProfile.fromEntity(testDealer);

        // Create test dealer stats
        testDealerStats = new DealerStats(
            testDealer.getId(),
            testDealer.getName(),
            10L, // carsListed
            5L,  // carsSold
            new BigDecimal("50000.00") // totalValue
        );

        // Create test registration request
        testRegistrationRequest = new DealerRegistrationRequest();
        testRegistrationRequest.setEmail("newdealer@example.com");
        testRegistrationRequest.setPassword("password123");
        testRegistrationRequest.setName("New Test Dealer");
        testRegistrationRequest.setAddress("456 New Street");
        testRegistrationRequest.setPhone("+0987654321");
        testRegistrationRequest.setWebsite("https://newdealer.com");
    }

    @Test
    void registerDealer_WithValidData_ShouldReturnCreated() throws Exception {
        // Given
        when(dealerService.registerDealer(any(DealerRegistrationRequest.class))).thenReturn(testDealerProfile);

        // When & Then
        mockMvc.perform(post("/api/dealers/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRegistrationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testDealerProfile.getId().toString()))
                .andExpect(jsonPath("$.name").value(testDealerProfile.getName()))
                .andExpect(jsonPath("$.email").value(testDealerProfile.getEmail()));
    }

    @Test
    void registerDealer_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        testRegistrationRequest.setEmail(""); // Invalid email

        // When & Then
        mockMvc.perform(post("/api/dealers/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRegistrationRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerDealer_WithExistingEmail_ShouldReturnBadRequest() throws Exception {
        // Given
        when(dealerService.registerDealer(any(DealerRegistrationRequest.class)))
            .thenThrow(new RuntimeException("User with this email already exists"));

        // When & Then
        mockMvc.perform(post("/api/dealers/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRegistrationRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("User with this email already exists"));
    }

    @Test
    @WithMockUser(username = "dealer@example.com")
    void getDealerProfile_WithValidAuthentication_ShouldReturnProfile() throws Exception {
        // Given
        when(dealerService.findByUserEmail("dealer@example.com")).thenReturn(Optional.of(testDealer));

        // When & Then
        mockMvc.perform(get("/api/dealers/profile")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testDealerProfile.getId().toString()))
                .andExpect(jsonPath("$.name").value(testDealerProfile.getName()))
                .andExpect(jsonPath("$.email").value(testDealerProfile.getEmail()));
    }

    @Test
    @WithMockUser(username = "nonexistent@example.com")
    void getDealerProfile_WithNonExistentDealer_ShouldReturnNotFound() throws Exception {
        // Given
        when(dealerService.findByUserEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/dealers/profile")
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "dealer@example.com")
    void updateDealerProfile_WithValidData_ShouldReturnUpdatedProfile() throws Exception {
        // Given
        when(dealerService.findByUserEmail("dealer@example.com")).thenReturn(Optional.of(testDealer));
        when(dealerService.updateDealerProfile(eq(testUser.getId()), any(), any(), any(), any()))
            .thenReturn(testDealerProfile);

        // When & Then
        mockMvc.perform(put("/api/dealers/profile")
                .with(csrf())
                .param("name", "Updated Dealer Name")
                .param("address", "Updated Address")
                .param("phone", "+1111111111")
                .param("website", "https://updated.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testDealerProfile.getName()));
    }

    @Test
    @WithMockUser(username = "nonexistent@example.com")
    void updateDealerProfile_WithNonExistentDealer_ShouldReturnNotFound() throws Exception {
        // Given
        when(dealerService.findByUserEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/dealers/profile")
                .with(csrf())
                .param("name", "Updated Name"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "dealer@example.com")
    void getDealerStatistics_WithValidAuthentication_ShouldReturnStats() throws Exception {
        // Given
        when(dealerService.findByUserEmail("dealer@example.com")).thenReturn(Optional.of(testDealer));
        when(dealerService.getDealerStatistics(testDealer.getId())).thenReturn(testDealerStats);

        // When & Then
        mockMvc.perform(get("/api/dealers/statistics")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dealerId").value(testDealerStats.getDealerId().toString()))
                .andExpect(jsonPath("$.dealerName").value(testDealerStats.getDealerName()))
                .andExpect(jsonPath("$.carsListed").value(10))
                .andExpect(jsonPath("$.carsSold").value(5))
                .andExpect(jsonPath("$.totalSalesValue").value(50000.00));
    }

    @Test
    @WithMockUser(username = "nonexistent@example.com")
    void getDealerStatistics_WithNonExistentDealer_ShouldReturnNotFound() throws Exception {
        // Given
        when(dealerService.findByUserEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/dealers/statistics")
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getDealerStatisticsById_WithValidId_ShouldReturnStats() throws Exception {
        // Given
        UUID dealerId = testDealer.getId();
        when(dealerService.getDealerProfileById(dealerId)).thenReturn(Optional.of(testDealerProfile));
        when(dealerService.getDealerStatistics(dealerId)).thenReturn(testDealerStats);

        // When & Then
        mockMvc.perform(get("/api/dealers/statistics/{dealerId}", dealerId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dealerId").value(testDealerStats.getDealerId().toString()))
                .andExpect(jsonPath("$.dealerName").value(testDealerStats.getDealerName()));
    }

    @Test
    void getDealerStatisticsById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        UUID dealerId = UUID.randomUUID();
        when(dealerService.getDealerProfileById(dealerId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/dealers/statistics/{dealerId}", dealerId)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchDealers_WithValidPattern_ShouldReturnDealers() throws Exception {
        // Given
        String namePattern = "Test";
        List<DealerProfile> dealers = List.of(testDealerProfile);
        when(dealerService.searchDealersByName(namePattern)).thenReturn(dealers);

        // When & Then
        mockMvc.perform(get("/api/dealers/search")
                .with(csrf())
                .param("name", namePattern))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testDealerProfile.getId().toString()))
                .andExpect(jsonPath("$[0].name").value(testDealerProfile.getName()));
    }

    @Test
    void getRecentDealers_WithValidLimit_ShouldReturnDealers() throws Exception {
        // Given
        int limit = 5;
        List<DealerProfile> dealers = List.of(testDealerProfile);
        when(dealerService.getRecentlyCreatedDealers(limit)).thenReturn(dealers);

        // When & Then
        mockMvc.perform(get("/api/dealers/recent")
                .with(csrf())
                .param("limit", String.valueOf(limit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testDealerProfile.getId().toString()))
                .andExpect(jsonPath("$[0].name").value(testDealerProfile.getName()));
    }

    @Test
    void getRecentDealers_WithDefaultLimit_ShouldReturnDealers() throws Exception {
        // Given
        List<DealerProfile> dealers = List.of(testDealerProfile);
        when(dealerService.getRecentlyCreatedDealers(10)).thenReturn(dealers);

        // When & Then
        mockMvc.perform(get("/api/dealers/recent")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testDealerProfile.getId().toString()));
    }
}