package com.crashedcarsales.controller;

import com.crashedcarsales.dto.ImageMetadata;
import com.crashedcarsales.dto.ImageResponse;
import com.crashedcarsales.dto.ImageUploadRequest;
import com.crashedcarsales.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/images")
@Tag(name = "Image Management", description = "Image upload, storage, and management APIs")
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class ImageController {

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/upload")
    @Operation(
        summary = "Upload image",
        description = "Upload an image file and generate multiple sizes"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Image uploaded successfully",
            content = @Content(schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid file or request data"
        ),
        @ApiResponse(
            responseCode = "413",
            description = "File too large"
        ),
        @ApiResponse(
            responseCode = "415",
            description = "Unsupported file type"
        )
    })
    public ResponseEntity<?> uploadImage(
            @Parameter(description = "Image file to upload")
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Entity type (CAR, DEALER, USER)")
            @RequestParam ImageUploadRequest.EntityType entityType,
            @Parameter(description = "Entity ID")
            @RequestParam String entityId,
            @Parameter(description = "Alt text for the image")
            @RequestParam(required = false) String altText,
            @Parameter(description = "Whether this is the primary image")
            @RequestParam(defaultValue = "false") Boolean isPrimary) {

        try {
            logger.info("Image upload request for entity: {} - {}", entityType, entityId);

            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("File is empty"));
            }

            // Validate entity ID format
            UUID entityUuid;
            try {
                entityUuid = UUID.fromString(entityId);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Invalid entity ID format"));
            }

            // Create folder path based on entity type and ID
            String folder = entityType.toString().toLowerCase() + "/" + entityUuid.toString();

            // Upload image
            Map<String, String> uploadedUrls = imageService.uploadImage(file, folder);

            logger.info("Image uploaded successfully for entity: {} - {}", entityType, entityId);
            return ResponseEntity.ok(uploadedUrls);

        } catch (IOException e) {
            logger.error("Image upload failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error during image upload", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/serve/{*relativePath}")
    @Operation(
        summary = "Serve image file",
        description = "Serve an image file from local storage"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Image served successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Image not found"
        )
    })
    public ResponseEntity<byte[]> serveImage(
            @Parameter(description = "Relative path to the image")
            @PathVariable String relativePath) {

        try {
            logger.debug("Serving image: {}", relativePath);

            // Construct full file path
            Path filePath = Paths.get("/tmp/crashed-car-sales/images", relativePath);

            if (!Files.exists(filePath)) {
                logger.warn("Image not found: {}", relativePath);
                return ResponseEntity.notFound().build();
            }

            // Read file
            byte[] imageBytes = Files.readAllBytes(filePath);

            // Determine content type
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            // Set cache headers for better performance
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setCacheControl("public, max-age=3600"); // Cache for 1 hour

            return ResponseEntity.ok()
                .headers(headers)
                .body(imageBytes);

        } catch (Exception e) {
            logger.error("Error serving image: {}", relativePath, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{relativePath}")
    @Operation(
        summary = "Delete image",
        description = "Delete an image and all its size variants"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Image deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Image not found"
        )
    })
    public ResponseEntity<?> deleteImage(
            @Parameter(description = "Relative path to the image")
            @PathVariable String relativePath) {

        try {
            logger.info("Deleting image: {}", relativePath);

            imageService.deleteImage(relativePath);

            return ResponseEntity.ok(new SuccessResponse("Image deleted successfully"));

        } catch (Exception e) {
            logger.error("Error deleting image: {}", relativePath, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to delete image"));
        }
    }

    @GetMapping("/{relativePath}/metadata")
    @Operation(
        summary = "Get image metadata",
        description = "Get metadata information about an image"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Metadata retrieved successfully",
            content = @Content(schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Image not found"
        )
    })
    public ResponseEntity<Map<String, Object>> getImageMetadata(
            @Parameter(description = "Relative path to the image")
            @PathVariable String relativePath) {

        try {
            logger.debug("Getting metadata for image: {}", relativePath);

            Map<String, Object> metadata = imageService.getImageMetadata(relativePath);

            if (metadata.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(metadata);

        } catch (Exception e) {
            logger.error("Error getting image metadata: {}", relativePath, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{relativePath}/exists")
    @Operation(
        summary = "Check if image exists",
        description = "Check if an image exists in storage"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Existence check completed"
        )
    })
    public ResponseEntity<Map<String, Boolean>> checkImageExists(
            @Parameter(description = "Relative path to the image")
            @PathVariable String relativePath) {

        try {
            boolean exists = imageService.imageExists(relativePath);
            Map<String, Boolean> response = Map.of("exists", exists);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error checking image existence: {}", relativePath, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/responsive/{relativePath}")
    @Operation(
        summary = "Get responsive image URLs",
        description = "Get URLs for all image sizes for responsive design"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Responsive URLs generated successfully",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    public ResponseEntity<Map<String, String>> getResponsiveImageUrls(
            @Parameter(description = "Relative path to the original image")
            @PathVariable String relativePath) {

        try {
            logger.debug("Generating responsive URLs for image: {}", relativePath);

            Map<String, String> responsiveUrls = imageService.generateResponsiveImageUrls(relativePath);
            return ResponseEntity.ok(responsiveUrls);

        } catch (Exception e) {
            logger.error("Error generating responsive URLs: {}", relativePath, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // Inner classes for error and success responses
    public static class ErrorResponse {
        private String error;
        private Long timestamp;

        public ErrorResponse(String error) {
            this.error = error;
            this.timestamp = System.currentTimeMillis();
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }
    }

    public static class SuccessResponse {
        private String message;
        private Long timestamp;

        public SuccessResponse(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }
    }
}