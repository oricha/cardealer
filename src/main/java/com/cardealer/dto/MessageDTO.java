package com.cardealer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    
    private Long receiverId;
    private Long carId;
    private String subject;
    
    @NotBlank(message = "El mensaje no puede estar vacío")
    private String content;
}