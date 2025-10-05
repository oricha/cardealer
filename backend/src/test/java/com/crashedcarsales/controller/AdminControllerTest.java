package com.crashedcarsales.controller;

import com.crashedcarsales.dto.AdminStatsResponse;
import com.crashedcarsales.dto.AdminUserResponse;
import com.crashedcarsales.dto.UserStatusUpdateRequest;
import com.crashedcarsales.entity.User;
import com.crashedcarsales.service.AdminService;
import com.crashedcarsales.security.JwtAuthenticationFilter;
import com.crashedcarsales.service.UserService;
import com.crashedcarsales.config.RateLimitInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@WebMvcTest(AdminController.class)
@ImportAutoConfiguration(exclude = {
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class,
    JpaRepositoriesAutoConfiguration.class
})
@Import(AdminControllerTest.TestSecurityConfig.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminService adminService;

    @MockBean
    private RateLimitInterceptor rateLimitInterceptor;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserService userService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;
    private User testUser;
    private AdminUserResponse adminUserResponse;
    private AdminStatsResponse adminStatsResponse;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();

        // Create test user
        testUser = new User();
        testUser.setId(testUserId);
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("hashedpassword");
        testUser.setRole(User.Role.BUYER);
        testUser.setIsActive(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        // Create admin user response
        adminUserResponse = new AdminUserResponse(testUser);

        // Create admin stats response
        adminStatsResponse = new AdminStatsResponse();
        adminStatsResponse.setTotalUsers(10L);
        adminStatsResponse.setActiveUsers(8L);
        adminStatsResponse.setTotalDealers(3L);
        adminStatsResponse.setActiveDealers(2L);
        adminStatsResponse.setTotalCars(15L);
        adminStatsResponse.setActiveCars(12L);
        adminStatsResponse.setTotalFavorites(25L);
        adminStatsResponse.setAverageCarsPerDealer(5.0);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_WithValidParameters_ShouldReturnUsersPage() throws Exception {
        // Given
        Page<AdminUserResponse> userPage = new PageImpl<>(
            List.of(adminUserResponse),
            PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt")),
            1
        );
        when(adminService.getAllUsers(any())).thenReturn(userPage);

        // When & Then
        mockMvc.perform(get("/api/admin/users")
                .param("page", "0")
                .param("size", "20")
                .param("sortBy", "createdAt")
                .param("sortDir", "desc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(testUserId.toString()))
                .andExpect(jsonPath("$.content[0].email").value("test@example.com"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(adminService).getAllUsers(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_WithDefaultParameters_ShouldReturnUsersPage() throws Exception {
        // Given
        Page<AdminUserResponse> userPage = new PageImpl<>(
            List.of(adminUserResponse),
            PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt")),
            1
        );
        when(adminService.getAllUsers(any())).thenReturn(userPage);

        // When & Then
        mockMvc.perform(get("/api/admin/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(testUserId.toString()));

        verify(adminService).getAllUsers(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_WithValidId_ShouldReturnUser() throws Exception {
        // Given
        when(adminService.getUserById(testUserId)).thenReturn(adminUserResponse);

        // When & Then
        mockMvc.perform(get("/api/admin/users/{userId}", testUserId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUserId.toString()))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("BUYER"))
                .andExpect(jsonPath("$.isActive").value(true));

        verify(adminService).getUserById(testUserId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserStatus_WithValidRequest_ShouldReturnUpdatedUser() throws Exception {
        // Given
        UserStatusUpdateRequest request = new UserStatusUpdateRequest(false);
        when(adminService.updateUserStatus(eq(testUserId), any(UserStatusUpdateRequest.class)))
            .thenReturn(adminUserResponse);

        // When & Then
        mockMvc.perform(patch("/api/admin/users/{userId}/status", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUserId.toString()))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(adminService).updateUserStatus(eq(testUserId), any(UserStatusUpdateRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserStatus_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Given
        String invalidJson = "{}"; // Missing required isActive field

        // When & Then
        mockMvc.perform(patch("/api/admin/users/{userId}/status", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(adminService, never()).updateUserStatus(any(), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_WithValidId_ShouldReturnOk() throws Exception {
        // Given
        doNothing().when(adminService).deleteUser(testUserId);

        // When & Then
        mockMvc.perform(delete("/api/admin/users/{userId}", testUserId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(adminService).deleteUser(testUserId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getSystemStats_ShouldReturnStatistics() throws Exception {
        // Given
        when(adminService.getSystemStats()).thenReturn(adminStatsResponse);

        // When & Then
        mockMvc.perform(get("/api/admin/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(10))
                .andExpect(jsonPath("$.activeUsers").value(8))
                .andExpect(jsonPath("$.totalDealers").value(3))
                .andExpect(jsonPath("$.activeDealers").value(2))
                .andExpect(jsonPath("$.totalCars").value(15))
                .andExpect(jsonPath("$.activeCars").value(12))
                .andExpect(jsonPath("$.totalFavorites").value(25))
                .andExpect(jsonPath("$.averageCarsPerDealer").value(5.0));

        verify(adminService).getSystemStats();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void searchUsersByEmail_WithValidEmail_ShouldReturnUsers() throws Exception {
        // Given
        List<AdminUserResponse> users = List.of(adminUserResponse);
        when(adminService.searchUsersByEmail("test@example.com")).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/admin/users/search")
                .param("email", "test@example.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testUserId.toString()))
                .andExpect(jsonPath("$[0].email").value("test@example.com"));

        verify(adminService).searchUsersByEmail("test@example.com");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void searchUsersByEmail_WithNoEmailParameter_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/admin/users/search")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(adminService, never()).searchUsersByEmail(anyString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUsersByRole_WithValidRole_ShouldReturnUsers() throws Exception {
        // Given
        List<AdminUserResponse> users = List.of(adminUserResponse);
        when(adminService.getUsersByRole(User.Role.BUYER)).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/admin/users/role/{role}", User.Role.BUYER)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testUserId.toString()))
                .andExpect(jsonPath("$[0].role").value("BUYER"));

        verify(adminService).getUsersByRole(User.Role.BUYER);
    }

    @Test
    @WithMockUser(roles = "USER")
    void accessAdminEndpoint_WithoutAdminRole_ShouldReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/admin/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(adminService, never()).getAllUsers(any());
    }

    @Test
    void accessAdminEndpoint_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        // When & Then - This would typically be handled by Spring Security
        // and return 401, but for this test we'll just verify the endpoint exists
        mockMvc.perform(get("/api/admin/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @TestConfiguration
    @EnableMethodSecurity(prePostEnabled = true)
    static class TestSecurityConfig {

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .anyRequest().permitAll()
                )
                .httpBasic(Customizer.withDefaults());
            return http.build();
        }

        @Bean
        UserDetailsService userDetailsService() {
            UserDetails admin = org.springframework.security.core.userdetails.User
                .withUsername("admin")
                .password("{noop}password")
                .roles("ADMIN")
                .build();
            UserDetails regular = org.springframework.security.core.userdetails.User
                .withUsername("user")
                .password("{noop}password")
                .roles("USER")
                .build();
            return new InMemoryUserDetailsManager(admin, regular);
        }
    }
}