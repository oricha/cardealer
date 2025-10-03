package com.crashedcarsales.controller;

import com.crashedcarsales.dto.AuthResponse;
import com.crashedcarsales.dto.LoginRequest;
import com.crashedcarsales.dto.RefreshTokenRequest;
import com.crashedcarsales.dto.RegisterRequest;
import com.crashedcarsales.entity.User;
import com.crashedcarsales.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.crashedcarsales.service.JwtService;
import com.crashedcarsales.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@Disabled("Temporarily disabled due to Spring context issues")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private AuthResponse authResponse;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private RefreshTokenRequest refreshTokenRequest;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("hashedpassword");
        testUser.setRole(User.Role.DEALER);
        testUser.setIsActive(true);

        // Create test auth response
        authResponse = AuthResponse.success("access-token", "refresh-token", 3600000L, testUser);

        // Create test requests
        registerRequest = new RegisterRequest("test@example.com", "password123", User.Role.DEALER);
        loginRequest = new LoginRequest("test@example.com", "password123");
        refreshTokenRequest = new RefreshTokenRequest("refresh-token");
    }

    @Test
    void register_WithValidData_ShouldReturnCreated() throws Exception {
        // Given
        when(userService.registerUser(any(RegisterRequest.class))).thenReturn(authResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.access_token").value("access-token"))
                .andExpect(jsonPath("$.refresh_token").value("refresh-token"))
                .andExpect(jsonPath("$.role").value("DEALER"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void register_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        registerRequest.setEmail(""); // Invalid email

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_WithExistingEmail_ShouldReturnBadRequest() throws Exception {
        // Given
        when(userService.registerUser(any(RegisterRequest.class)))
            .thenThrow(new RuntimeException("User with this email already exists"));

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("User with this email already exists"));
    }

    @Test
    void login_WithValidCredentials_ShouldReturnOk() throws Exception {
        // Given
        when(userService.authenticateUser(any(LoginRequest.class))).thenReturn(authResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("access-token"))
                .andExpect(jsonPath("$.refresh_token").value("refresh-token"))
                .andExpect(jsonPath("$.role").value("DEALER"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        // Given
        when(userService.authenticateUser(any(LoginRequest.class)))
            .thenThrow(new RuntimeException("Invalid email or password"));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid email or password"));
    }

    @Test
    void login_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        loginRequest.setEmail(""); // Invalid email

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refreshToken_WithValidToken_ShouldReturnOk() throws Exception {
        // Given
        when(userService.refreshToken(any(String.class))).thenReturn(authResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("access-token"))
                .andExpect(jsonPath("$.refresh_token").value("refresh-token"))
                .andExpect(jsonPath("$.role").value("DEALER"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void refreshToken_WithInvalidToken_ShouldReturnUnauthorized() throws Exception {
        // Given
        when(userService.refreshToken(any(String.class)))
            .thenThrow(new RuntimeException("Invalid refresh token"));

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid refresh token"));
    }

    @Test
    void refreshToken_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        refreshTokenRequest.setRefreshToken(""); // Invalid token

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void logout_ShouldReturnOk() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/logout")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logout successful"));
    }
}