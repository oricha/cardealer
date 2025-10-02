package com.crashedcarsales.controller;

import com.crashedcarsales.dto.ImageUploadRequest;
import com.crashedcarsales.service.ImageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.imageio.ImageIO;

@WebMvcTest(ImageController.class)
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageService imageService;

    @Autowired
    private ObjectMapper objectMapper;

    @TempDir
    Path tempDir;

    private String testStorageDirectory;

    @BeforeEach
    void setUp() {
        testStorageDirectory = tempDir.toString();
    }

    @Test
    void uploadImage_WithValidData_ShouldReturnSuccess() throws Exception {
        // Given
        MockMultipartFile file = createTestImageFile("test-image.jpg");
        Map<String, String> mockResponse = Map.of(
            "original", "http://localhost:8080/api/images/car/123/original/test-image.jpg",
            "thumbnail", "http://localhost:8080/api/images/car/123/thumbnail/test-image.jpg"
        );

        when(imageService.uploadImage(any(org.springframework.web.multipart.MultipartFile.class), anyString()))
            .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(multipart("/api/images/upload")
                .file(file)
                .param("entityType", "CAR")
                .param("entityId", "123e4567-e89b-12d3-a456-426614174000")
                .param("altText", "Test car image")
                .param("isPrimary", "true")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.original").value("http://localhost:8080/api/images/car/123/original/test-image.jpg"))
                .andExpect(jsonPath("$.thumbnail").value("http://localhost:8080/api/images/car/123/thumbnail/test-image.jpg"));
    }

    @Test
    void uploadImage_WithEmptyFile_ShouldReturnBadRequest() throws Exception {
        // Given
        MockMultipartFile emptyFile = new MockMultipartFile(
            "file",
            "empty.jpg",
            "image/jpeg",
            new byte[0]
        );

        // When & Then
        mockMvc.perform(multipart("/api/images/upload")
                .file(emptyFile)
                .param("entityType", "CAR")
                .param("entityId", "123e4567-e89b-12d3-a456-426614174000")
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("File is empty"));
    }

    @Test
    void uploadImage_WithInvalidEntityId_ShouldReturnBadRequest() throws Exception {
        // Given
        MockMultipartFile file = createTestImageFile("test-image.jpg");

        // When & Then
        mockMvc.perform(multipart("/api/images/upload")
                .file(file)
                .param("entityType", "CAR")
                .param("entityId", "invalid-uuid")
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid entity ID format"));
    }

    @Test
    void uploadImage_WithServiceException_ShouldReturnBadRequest() throws Exception {
        // Given
        MockMultipartFile file = createTestImageFile("test-image.jpg");

        when(imageService.uploadImage(any(org.springframework.web.multipart.MultipartFile.class), anyString()))
            .thenThrow(new RuntimeException("Upload failed"));

        // When & Then
        mockMvc.perform(multipart("/api/images/upload")
                .file(file)
                .param("entityType", "CAR")
                .param("entityId", "123e4567-e89b-12d3-a456-426614174000")
                .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal server error"));
    }

    @Test
    void deleteImage_WithValidPath_ShouldReturnSuccess() throws Exception {
        // Given
        String relativePath = "car/123/original/test-image.jpg";

        // When & Then
        mockMvc.perform(delete("/api/images/{relativePath}", relativePath)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Image deleted successfully"));
    }

    @Test
    void getImageMetadata_WithValidPath_ShouldReturnMetadata() throws Exception {
        // Given
        String relativePath = "car/123/original/test-image.jpg";
        Map<String, Object> mockMetadata = Map.of(
            "fileName", "test-image.jpg",
            "fileSize", 1024000L,
            "isReadable", true
        );

        when(imageService.getImageMetadata(relativePath)).thenReturn(mockMetadata);

        // When & Then
        mockMvc.perform(get("/api/images/{relativePath}/metadata", relativePath)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("test-image.jpg"))
                .andExpect(jsonPath("$.fileSize").value(1024000))
                .andExpect(jsonPath("$.isReadable").value(true));
    }

    @Test
    void getImageMetadata_WithInvalidPath_ShouldReturnNotFound() throws Exception {
        // Given
        String relativePath = "nonexistent/path/image.jpg";

        when(imageService.getImageMetadata(relativePath)).thenReturn(Map.of());

        // When & Then
        mockMvc.perform(get("/api/images/{relativePath}/metadata", relativePath)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void checkImageExists_WithExistingImage_ShouldReturnTrue() throws Exception {
        // Given
        String relativePath = "car/123/original/test-image.jpg";

        when(imageService.imageExists(relativePath)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/images/{relativePath}/exists", relativePath)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(true));
    }

    @Test
    void checkImageExists_WithNonExistentImage_ShouldReturnFalse() throws Exception {
        // Given
        String relativePath = "nonexistent/path/image.jpg";

        when(imageService.imageExists(relativePath)).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/images/{relativePath}/exists", relativePath)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(false));
    }

    @Test
    void getResponsiveImageUrls_WithValidPath_ShouldReturnUrls() throws Exception {
        // Given
        String relativePath = "car/123/original/test-image.jpg";
        Map<String, String> mockUrls = Map.of(
            "original", "http://localhost:8080/api/images/car/123/original/test-image.jpg",
            "thumbnail", "http://localhost:8080/api/images/car/123/thumbnail/test-image.jpg",
            "small", "http://localhost:8080/api/images/car/123/small/test-image.jpg"
        );

        when(imageService.generateResponsiveImageUrls(relativePath)).thenReturn(mockUrls);

        // When & Then
        mockMvc.perform(get("/api/images/responsive/{relativePath}", relativePath)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.original").value("http://localhost:8080/api/images/car/123/original/test-image.jpg"))
                .andExpect(jsonPath("$.thumbnail").value("http://localhost:8080/api/images/car/123/thumbnail/test-image.jpg"))
                .andExpect(jsonPath("$.small").value("http://localhost:8080/api/images/car/123/small/test-image.jpg"));
    }

    // Helper method to create a test image file
    private MockMultipartFile createTestImageFile(String filename) throws Exception {
        // Create a simple test image
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);

        return new MockMultipartFile(
            "file",
            filename,
            "image/jpeg",
            baos.toByteArray()
        );
    }
}