package com.crashedcarsales.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Login request DTO using Java 14+ Record feature
 * Records provide immutability and reduce boilerplate code
 */
public record LoginRequestRecord(
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    String email,
    
    @NotBlank(message = "Password is mandatory")
    String password
) {
    // Custom toString to protect password
    @Override
    public String toString() {
        return "LoginRequest{" +
                "email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}