package com.crashedcarsales.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @InjectMocks
    private ImageService imageService;

    @TempDir
    Path tempDir;

    private String testStorageDirectory;
    private String testCdnBaseUrl;

    @BeforeEach
    void setUp() {
        testStorageDirectory = tempDir.toString();
        testCdnBaseUrl = "http://localhost:8080/api/images";

        // Set test values using reflection
        ReflectionTestUtils.setField(imageService, "localStorageDirectory", testStorageDirectory);
        ReflectionTestUtils.setField(imageService, "cdnBaseUrl", testCdnBaseUrl);
    }

    @Test
    void uploadImage_WithValidImage_ShouldUploadSuccessfully() throws IOException {
        // Given
        String folder = "test-entity/123e4567-e89b-12d3-a456-426614174000";
        MultipartFile file = createTestImageFile("test-image.jpg");

        // When
        Map<String, String> result = imageService.uploadImage(file, folder);

        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("original"));
        assertTrue(result.containsKey("thumbnail"));
        assertTrue(result.containsKey("small"));
        assertTrue(result.containsKey("medium"));
        assertTrue(result.containsKey("large"));

        // Verify files were created
        assertTrue(Files.exists(Path.of(testStorageDirectory, folder, "original", "test-image.jpg")));
        assertTrue(Files.exists(Path.of(testStorageDirectory, folder, "thumbnail", "test-image.jpg")));
    }

    @Test
    void uploadImage_WithInvalidFileType_ShouldThrowException() {
        // Given
        String folder = "test-entity/123e4567-e89b-12d3-a456-426614174000";
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.txt",
            "text/plain",
            "This is not an image".getBytes()
        );

        // When & Then
        IOException exception = assertThrows(IOException.class,
            () -> imageService.uploadImage(file, folder));

        assertTrue(exception.getMessage().contains("Invalid file type"));
    }

    @Test
    void uploadImage_WithFileTooLarge_ShouldThrowException() {
        // Given
        String folder = "test-entity/123e4567-e89b-12d3-a456-426614174000";
        // Create a file larger than 10MB
        byte[] largeContent = new byte[11 * 1024 * 1024]; // 11MB
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "large-image.jpg",
            "image/jpeg",
            largeContent
        );

        // When & Then
        IOException exception = assertThrows(IOException.class,
            () -> imageService.uploadImage(file, folder));

        assertTrue(exception.getMessage().contains("File size exceeds maximum"));
    }

    @Test
    void deleteImage_WithValidPath_ShouldDeleteSuccessfully() throws IOException {
        // Given
        String folder = "test-entity/123e4567-e89b-12d3-a456-426614174000";
        MultipartFile file = createTestImageFile("test-image.jpg");
        Map<String, String> uploadResult = imageService.uploadImage(file, folder);

        // Verify file exists before deletion
        String relativePath = folder + "/original/test-image.jpg";
        assertTrue(imageService.imageExists(relativePath));

        // When
        imageService.deleteImage(relativePath);

        // Then
        assertFalse(imageService.imageExists(relativePath));
    }

    @Test
    void imageExists_WithValidPath_ShouldReturnTrue() throws IOException {
        // Given
        String folder = "test-entity/123e4567-e89b-12d3-a456-426614174000";
        MultipartFile file = createTestImageFile("test-image.jpg");
        Map<String, String> uploadResult = imageService.uploadImage(file, folder);

        String relativePath = folder + "/original/test-image.jpg";

        // When
        boolean exists = imageService.imageExists(relativePath);

        // Then
        assertTrue(exists);
    }

    @Test
    void imageExists_WithInvalidPath_ShouldReturnFalse() {
        // Given
        String relativePath = "nonexistent/path/image.jpg";

        // When
        boolean exists = imageService.imageExists(relativePath);

        // Then
        assertFalse(exists);
    }

    @Test
    void getImageMetadata_WithValidPath_ShouldReturnMetadata() throws IOException {
        // Given
        String folder = "test-entity/123e4567-e89b-12d3-a456-426614174000";
        MultipartFile file = createTestImageFile("test-image.jpg");
        Map<String, String> uploadResult = imageService.uploadImage(file, folder);

        String relativePath = folder + "/original/test-image.jpg";

        // When
        Map<String, Object> metadata = imageService.getImageMetadata(relativePath);

        // Then
        assertNotNull(metadata);
        assertTrue(metadata.containsKey("fileName"));
        assertTrue(metadata.containsKey("fileSize"));
        assertTrue(metadata.containsKey("isReadable"));
    }

    @Test
    void getImageMetadata_WithInvalidPath_ShouldReturnEmptyMap() {
        // Given
        String relativePath = "nonexistent/path/image.jpg";

        // When
        Map<String, Object> metadata = imageService.getImageMetadata(relativePath);

        // Then
        assertTrue(metadata.isEmpty());
    }

    @Test
    void generateResponsiveImageUrls_WithValidPath_ShouldReturnUrls() throws IOException {
        // Given
        String folder = "test-entity/123e4567-e89b-12d3-a456-426614174000";
        MultipartFile file = createTestImageFile("test-image.jpg");
        Map<String, String> uploadResult = imageService.uploadImage(file, folder);

        String basePath = folder + "/original/test-image.jpg";

        // When
        Map<String, String> responsiveUrls = imageService.generateResponsiveImageUrls(basePath);

        // Then
        assertNotNull(responsiveUrls);
        assertTrue(responsiveUrls.containsKey("original"));
        assertTrue(responsiveUrls.containsKey("thumbnail"));
        assertTrue(responsiveUrls.containsKey("small"));
        assertTrue(responsiveUrls.containsKey("medium"));
        assertTrue(responsiveUrls.containsKey("large"));

        // Verify URLs contain expected paths
        assertTrue(responsiveUrls.get("original").contains("test-image.jpg"));
        assertTrue(responsiveUrls.get("thumbnail").contains("thumbnail/test-image.jpg"));
    }

    @Test
    void validateImageFile_WithValidImage_ShouldPass() throws IOException {
        // Given
        MultipartFile file = createTestImageFile("valid-image.jpg");

        // When & Then (no exception should be thrown)
        assertDoesNotThrow(() -> {
            // Access private method through reflection for testing
            java.lang.reflect.Method method = ImageService.class.getDeclaredMethod("validateImageFile", MultipartFile.class);
            method.setAccessible(true);
            method.invoke(imageService, file);
        });
    }

    @Test
    void validateImageFile_WithInvalidExtension_ShouldThrowException() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.txt",
            "text/plain",
            "This is not an image".getBytes()
        );

        // When & Then
        IOException exception = assertThrows(IOException.class, () -> {
            java.lang.reflect.Method method = ImageService.class.getDeclaredMethod("validateImageFile", MultipartFile.class);
            method.setAccessible(true);
            method.invoke(imageService, file);
        });

        assertTrue(exception.getMessage().contains("Invalid file type"));
    }

    // Helper method to create a test image file
    private MultipartFile createTestImageFile(String filename) throws IOException {
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