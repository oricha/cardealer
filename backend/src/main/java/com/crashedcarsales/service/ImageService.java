package com.crashedcarsales.service;

import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class ImageService {

    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    @Value("${app.storage.local.directory:/tmp/crashed-car-sales/images}")
    private String localStorageDirectory;

    @Value("${app.cdn.base-url:http://localhost:8080/api/images}")
    private String cdnBaseUrl;

    // Image size configurations
    private static final Map<String, Integer> IMAGE_SIZES = Map.of(
        "thumbnail", 150,
        "small", 400,
        "medium", 800,
        "large", 1200,
        "original", -1 // Keep original size
    );

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final int MAX_WIDTH = 4000;
    private static final int MAX_HEIGHT = 4000;

    /**
     * Upload image and generate multiple sizes
     */
    public Map<String, String> uploadImage(MultipartFile file, String folder) throws IOException {
        logger.info("Starting image upload for file: {}", file.getOriginalFilename());

        // Validate file
        validateImageFile(file);

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String baseFilename = UUID.randomUUID().toString();
        String filename = baseFilename + "." + extension;

        // Read original image
        BufferedImage originalImage = ImageIO.read(file.getInputStream());

        // Validate image dimensions
        validateImageDimensions(originalImage);

        Map<String, String> uploadedUrls = new HashMap<>();

        // Create directory structure
        createDirectoryStructure(folder);

        // Save original size
        String originalPath = localStorageDirectory + "/" + folder + "/original/" + filename;
        String originalUrl = saveImageToLocalStorage(originalImage, originalPath, extension);
        uploadedUrls.put("original", originalUrl);

        // Generate and save different sizes
        for (Map.Entry<String, Integer> sizeEntry : IMAGE_SIZES.entrySet()) {
            String sizeName = sizeEntry.getKey();
            Integer maxDimension = sizeEntry.getValue();

            if (maxDimension == -1) continue; // Skip original size, already saved

            try {
                BufferedImage resizedImage = resizeImage(originalImage, maxDimension);

                String sizePath = localStorageDirectory + "/" + folder + "/" + sizeName + "/" + filename;
                String sizeUrl = saveImageToLocalStorage(resizedImage, sizePath, extension);
                uploadedUrls.put(sizeName, sizeUrl);

            } catch (Exception e) {
                logger.warn("Failed to generate {} size for image: {}", sizeName, filename, e);
            }
        }

        logger.info("Image upload completed for file: {}", filename);
        return uploadedUrls;
    }

    /**
     * Save image to local storage
     */
    private String saveImageToLocalStorage(BufferedImage image, String filePath, String extension) throws IOException {
        File outputFile = new File(filePath);
        outputFile.getParentFile().mkdirs(); // Create directories if they don't exist

        ImageIO.write(image, extension, outputFile);

        // Generate URL for the saved image
        return generateImageUrl(filePath.replace(localStorageDirectory + "/", ""));
    }

    /**
     * Generate image URL (with CDN if configured)
     */
    private String generateImageUrl(String relativePath) {
        if (cdnBaseUrl != null && !cdnBaseUrl.isEmpty()) {
            return cdnBaseUrl + "/" + relativePath;
        } else {
            return "/api/images/" + relativePath;
        }
    }

    /**
     * Create directory structure for image storage
     */
    private void createDirectoryStructure(String folder) throws IOException {
        // Create directories for all image sizes
        for (String size : IMAGE_SIZES.keySet()) {
            Path dirPath = Paths.get(localStorageDirectory, folder, size);
            Files.createDirectories(dirPath);
        }
    }

    /**
     * Resize image maintaining aspect ratio
     */
    private BufferedImage resizeImage(BufferedImage originalImage, int maxDimension) throws IOException {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        if (originalWidth <= maxDimension && originalHeight <= maxDimension) {
            return originalImage; // No resize needed
        }

        double aspectRatio = (double) originalWidth / originalHeight;

        int newWidth, newHeight;
        if (originalWidth > originalHeight) {
            newWidth = maxDimension;
            newHeight = (int) (maxDimension / aspectRatio);
        } else {
            newHeight = maxDimension;
            newWidth = (int) (maxDimension * aspectRatio);
        }

        return Thumbnails.of(originalImage)
            .size(newWidth, newHeight)
            .outputQuality(0.85)
            .asBufferedImage();
    }

    /**
     * Validate uploaded image file
     */
    private void validateImageFile(MultipartFile file) throws IOException {
        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IOException("File size exceeds maximum allowed size of " + (MAX_FILE_SIZE / 1024 / 1024) + "MB");
        }

        // Check file extension
        String filename = file.getOriginalFilename();
        if (filename == null || !isValidImageExtension(filename)) {
            throw new IOException("Invalid file type. Only JPG, PNG, and WebP images are allowed");
        }

        // Check if file is actually an image
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new IOException("File is not a valid image");
            }
        } catch (IOException e) {
            throw new IOException("Unable to read image file", e);
        }
    }

    /**
     * Validate image dimensions
     */
    private void validateImageDimensions(BufferedImage image) throws IOException {
        int width = image.getWidth();
        int height = image.getHeight();

        if (width > MAX_WIDTH || height > MAX_HEIGHT) {
            throw new IOException(String.format(
                "Image dimensions too large. Maximum allowed: %dx%d, actual: %dx%d",
                MAX_WIDTH, MAX_HEIGHT, width, height));
        }
    }

    /**
     * Check if file extension is valid
     */
    private boolean isValidImageExtension(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        return ALLOWED_EXTENSIONS.contains(extension);
    }

    /**
     * Get file extension from filename
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    /**
     * Delete image from storage
     */
    public void deleteImage(String relativePath) {
        try {
            logger.info("Deleting image from storage: {}", relativePath);

            // Delete all size variants
            for (String size : IMAGE_SIZES.keySet()) {
                String sizePath = relativePath.replace("original/", size + "/");
                File file = new File(localStorageDirectory, sizePath);

                if (file.exists()) {
                    boolean deleted = file.delete();
                    if (!deleted) {
                        logger.warn("Failed to delete file: {}", file.getAbsolutePath());
                    }
                }
            }

            logger.info("Image deleted successfully: {}", relativePath);

        } catch (Exception e) {
            logger.error("Failed to delete image: {}", relativePath, e);
            throw new RuntimeException("Failed to delete image", e);
        }
    }

    /**
     * Check if image exists in storage
     */
    public boolean imageExists(String relativePath) {
        try {
            // Check if original size exists
            String originalPath = relativePath.replace("original/", "original/");
            File file = new File(localStorageDirectory, originalPath);
            return file.exists();

        } catch (Exception e) {
            logger.error("Error checking if image exists: {}", relativePath, e);
            return false;
        }
    }

    /**
     * Get image metadata
     */
    public Map<String, Object> getImageMetadata(String relativePath) {
        try {
            String originalPath = relativePath.replace("original/", "original/");
            File file = new File(localStorageDirectory, originalPath);

            if (!file.exists()) {
                return Collections.emptyMap();
            }

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("fileName", file.getName());
            metadata.put("filePath", file.getAbsolutePath());
            metadata.put("fileSize", file.length());
            metadata.put("lastModified", file.lastModified());
            metadata.put("isReadable", file.canRead());

            return metadata;

        } catch (Exception e) {
            logger.error("Error getting image metadata: {}", relativePath, e);
            return Collections.emptyMap();
        }
    }

    /**
     * Generate optimized image URLs for responsive images
     */
    public Map<String, String> generateResponsiveImageUrls(String baseKey) {
        Map<String, String> responsiveUrls = new HashMap<>();

        for (String size : IMAGE_SIZES.keySet()) {
            String sizeKey = baseKey.replace("original/", size + "/");
            responsiveUrls.put(size, generateImageUrl(sizeKey));
        }

        return responsiveUrls;
    }

}