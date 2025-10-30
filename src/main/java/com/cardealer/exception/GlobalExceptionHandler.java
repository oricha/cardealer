package com.cardealer.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle ResourceNotFoundException
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFound(ResourceNotFoundException ex, Model model) {
        log.error("Resource not found: {}", ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("status", 404);
        model.addAttribute("message", "Recurso no encontrado");
        return "404";
    }

    /**
     * Handle UnauthorizedException
     */
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleUnauthorized(UnauthorizedException ex, Model model) {
        log.error("Unauthorized access: {}", ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("status", 403);
        model.addAttribute("message", "Acceso no autorizado");
        return "error";
    }

    /**
     * Handle AccessDeniedException (Spring Security)
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied(AccessDeniedException ex, Model model) {
        log.error("Access denied: {}", ex.getMessage());
        model.addAttribute("error", "No tienes permisos para acceder a este recurso");
        model.addAttribute("status", 403);
        model.addAttribute("message", "Acceso denegado");
        return "error";
    }

    /**
     * Handle DuplicateResourceException
     */
    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDuplicateResource(DuplicateResourceException ex, Model model) {
        log.error("Duplicate resource: {}", ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("status", 409);
        model.addAttribute("message", "Recurso duplicado");
        return "error";
    }

    /**
     * Handle validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationErrors(MethodArgumentNotValidException ex, Model model) {
        log.error("Validation error: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        model.addAttribute("errors", errors);
        model.addAttribute("status", 400);
        model.addAttribute("message", "Error de validación");
        return "error";
    }

    /**
     * Handle IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgument(IllegalArgumentException ex, Model model) {
        log.error("Illegal argument: {}", ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("status", 400);
        model.addAttribute("message", "Argumento inválido");
        return "error";
    }

    /**
     * Handle MaxUploadSizeExceededException
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex, Model model) {
        log.error("File size exceeded: {}", ex.getMessage());
        model.addAttribute("error", "El archivo es demasiado grande. Tamaño máximo permitido: 10MB");
        model.addAttribute("status", 400);
        model.addAttribute("message", "Archivo demasiado grande");
        return "error";
    }

    /**
     * Handle generic exceptions
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericError(Exception ex, Model model) {
        log.error("Unexpected error occurred", ex);
        model.addAttribute("error", "Ha ocurrido un error inesperado. Por favor, inténtelo de nuevo más tarde.");
        model.addAttribute("status", 500);
        model.addAttribute("message", "Error interno del servidor");
        return "error";
    }
}