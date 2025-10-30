package com.cardealer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDTO {
    
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    
    @Email(message = "Email inválido")
    @NotBlank(message = "El email es obligatorio")
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
    
    private String confirmPassword;
    
    private String phone;
    
    @NotBlank(message = "El rol es obligatorio")
    private String role;  // "COMPRADOR" o "VENDEDOR"
    
    // Campos adicionales si es vendedor
    private String dealerName;
    private String dealerAddress;
    private String dealerCity;
    private String dealerPostalCode;
    private String dealerDescription;
}