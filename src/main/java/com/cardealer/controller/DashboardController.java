package com.cardealer.controller;

import com.cardealer.dto.CarDTO;
import com.cardealer.dto.DashboardStats;
import com.cardealer.model.Car;
import com.cardealer.model.Dealer;
import com.cardealer.model.User;
import com.cardealer.model.enums.BodyType;
import com.cardealer.model.enums.CarCondition;
import com.cardealer.model.enums.FuelType;
import com.cardealer.model.enums.TransmissionType;
import com.cardealer.service.CarService;
import com.cardealer.service.DealerService;
import com.cardealer.service.FavoriteService;
import com.cardealer.service.MessageService;
import com.cardealer.service.UserService;
import com.cardealer.util.FileUploadUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/dashboard")
// @PreAuthorize("hasRole('VENDEDOR')") // TEMPORARY: Disabled for development
@RequiredArgsConstructor
public class DashboardController {

    private final CarService carService;
    private final DealerService dealerService;
    private final UserService userService;
    private final MessageService messageService;
    private final FavoriteService favoriteService;

    /**
     * Show dashboard
     */
    @GetMapping
    public String dashboard(Model model, Authentication authentication) {
        // TEMPORARY: For development without authentication, use first dealer
        if (authentication == null) {
            log.info("Loading dashboard without authentication (development mode)");
            Dealer dealer = dealerService.getDealerById(1L);
            DashboardStats stats = carService.getDealerStats(dealer.getId());
            
            model.addAttribute("dealer", dealer);
            model.addAttribute("stats", stats);
            model.addAttribute("activeListings", stats.getActiveListings());
            model.addAttribute("totalViews", stats.getTotalViews());
            model.addAttribute("totalListings", stats.getTotalListings());
            model.addAttribute("recentListings", stats.getRecentListings());
            
            return "dashboard";
        }
        
        log.info("Loading dashboard for user: {}", authentication.getName());
        
        // Get authenticated user
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        
        // Get dealer associated with user
        Dealer dealer = dealerService.getDealerByUserId(user.getId());
        
        if (dealer == null) {
            log.error("No dealer found for user: {}", email);
            return "redirect:/";
        }
        
        // Get dealer statistics
        DashboardStats stats = carService.getDealerStats(dealer.getId());
        
        // Add data to model
        model.addAttribute("user", user);
        model.addAttribute("dealer", dealer);
        model.addAttribute("stats", stats);
        model.addAttribute("activeListings", stats.getActiveListings());
        model.addAttribute("totalViews", stats.getTotalViews());
        model.addAttribute("totalListings", stats.getTotalListings());
        model.addAttribute("recentListings", stats.getRecentListings());
        
        log.info("Dashboard loaded successfully for dealer: {}", dealer.getName());
        return "dashboard";
    }

    /**
     * Show my listings
     */
    @GetMapping("/listings")
    public String myListings(Model model, Authentication authentication) {
        // TEMPORARY: For development without authentication, use first dealer
        if (authentication == null) {
            log.info("Loading listings without authentication (development mode)");
            Dealer dealer = dealerService.getDealerById(1L);
            model.addAttribute("cars", carService.getCarsByDealer(dealer.getId()));
            model.addAttribute("dealer", dealer);
            return "profile-listing";
        }
        
        log.info("Loading listings for user: {}", authentication.getName());
        
        // Get authenticated user
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        
        // Get dealer associated with user
        Dealer dealer = dealerService.getDealerByUserId(user.getId());
        
        if (dealer == null) {
            log.error("No dealer found for user: {}", email);
            return "redirect:/";
        }
        
        // Get all cars for this dealer
        model.addAttribute("cars", carService.getCarsByDealer(dealer.getId()));
        model.addAttribute("dealer", dealer);
        
        return "profile-listing";
    }

    /**
     * Show messages
     */
    @GetMapping("/messages")
    public String messages(Model model, Authentication authentication) {
        // TEMPORARY: For development without authentication
        if (authentication == null) {
            log.info("Loading messages without authentication (development mode)");
            model.addAttribute("messages", messageService.getReceivedMessages(1L));
            return "profile-message";
        }
        
        log.info("Loading messages for user: {}", authentication.getName());
        
        // Get authenticated user
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        
        // Get received messages
        model.addAttribute("user", user);
        model.addAttribute("messages", messageService.getReceivedMessages(user.getId()));
        model.addAttribute("unreadCount", messageService.getUnreadCount(user.getId()));
        
        return "profile-message";
    }

    /**
     * Show favorites
     */
    @GetMapping("/favorites")
    public String favorites(Model model, Authentication authentication) {
        // TEMPORARY: For development without authentication
        if (authentication == null) {
            log.info("Loading favorites without authentication (development mode)");
            model.addAttribute("favoriteCars", favoriteService.getUserFavorites(1L));
            return "profile-favorite";
        }
        
        log.info("Loading favorites for user: {}", authentication.getName());
        
        // Get authenticated user
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        
        model.addAttribute("user", user);
        model.addAttribute("favoriteCars", favoriteService.getUserFavorites(user.getId()));
        
        return "profile-favorite";
    }

    /**
     * Show add listing form
     */
    @GetMapping("/listings/add")
    public String addListingForm(Model model, Authentication authentication) {
        // TEMPORARY: For development without authentication
        if (authentication == null) {
            log.info("Loading add listing form without authentication (development mode)");
            model.addAttribute("carDTO", new CarDTO());
            addEnumsToModel(model);
            return "add-listing";
        }
        
        log.info("Loading add listing form for user: {}", authentication.getName());
        
        // Get authenticated user
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        
        // Get dealer associated with user
        Dealer dealer = dealerService.getDealerByUserId(user.getId());
        
        if (dealer == null) {
            log.error("No dealer found for user: {}", email);
            return "redirect:/";
        }
        
        model.addAttribute("carDTO", new CarDTO());
        model.addAttribute("dealer", dealer);
        addEnumsToModel(model);
        
        return "add-listing";
    }

    /**
     * Process add listing form
     */
    @PostMapping("/listings/add")
    public String addListing(
            @Valid @ModelAttribute CarDTO carDTO,
            BindingResult bindingResult,
            @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        // TEMPORARY: For development without authentication, use first dealer
        Long dealerId;
        if (authentication == null) {
            log.info("Adding listing without authentication (development mode)");
            dealerId = 1L;
        } else {
            String email = authentication.getName();
            User user = userService.getUserByEmail(email);
            Dealer dealer = dealerService.getDealerByUserId(user.getId());
            
            if (dealer == null) {
                log.error("No dealer found for user: {}", email);
                redirectAttributes.addFlashAttribute("error", "No se encontró el concesionario asociado");
                return "redirect:/dashboard";
            }
            dealerId = dealer.getId();
        }
        
        // Validate form
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors in add listing form");
            model.addAttribute("carDTO", carDTO);
            addEnumsToModel(model);
            return "add-listing";
        }
        
        try {
            // Handle image uploads
            if (imageFiles != null && !imageFiles.isEmpty()) {
                FileUploadUtil fileUploadUtil = new FileUploadUtil();
                List<String> imageUrls = new ArrayList<>();
                
                for (MultipartFile file : imageFiles) {
                    if (!file.isEmpty()) {
                        String fileName = fileUploadUtil.saveFile(file);
                        imageUrls.add(fileName);
                    }
                }
                
                if (!imageUrls.isEmpty()) {
                    carDTO.setExistingImages(imageUrls);
                } else {
                    redirectAttributes.addFlashAttribute("error", "Debe subir al menos una imagen");
                    model.addAttribute("carDTO", carDTO);
                    addEnumsToModel(model);
                    return "add-listing";
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Debe subir al menos una imagen");
                model.addAttribute("carDTO", carDTO);
                addEnumsToModel(model);
                return "add-listing";
            }
            
            // Create car
            Car car = carService.createCar(carDTO, dealerId);
            log.info("Car created successfully with id: {}", car.getId());
            
            redirectAttributes.addFlashAttribute("success", "Listado creado exitosamente");
            return "redirect:/dashboard/listings";
            
        } catch (IOException e) {
            log.error("Error uploading images", e);
            redirectAttributes.addFlashAttribute("error", "Error al subir las imágenes: " + e.getMessage());
            model.addAttribute("carDTO", carDTO);
            addEnumsToModel(model);
            return "add-listing";
        } catch (Exception e) {
            log.error("Error creating car", e);
            redirectAttributes.addFlashAttribute("error", "Error al crear el listado: " + e.getMessage());
            model.addAttribute("carDTO", carDTO);
            addEnumsToModel(model);
            return "add-listing";
        }
    }

    /**
     * Show edit listing form
     */
    @GetMapping("/listings/edit/{id}")
    public String editListingForm(@PathVariable Long id, Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        // TEMPORARY: For development without authentication, use first dealer
        Long dealerId;
        if (authentication == null) {
            log.info("Loading edit listing form without authentication (development mode)");
            dealerId = 1L;
        } else {
            String email = authentication.getName();
            User user = userService.getUserByEmail(email);
            Dealer dealer = dealerService.getDealerByUserId(user.getId());
            
            if (dealer == null) {
                log.error("No dealer found for user: {}", email);
                redirectAttributes.addFlashAttribute("error", "No se encontró el concesionario asociado");
                return "redirect:/dashboard";
            }
            dealerId = dealer.getId();
        }
        
        try {
            // Get car
            Car car = carService.getCarByIdWithoutIncrement(id)
                .orElseThrow(() -> new RuntimeException("Coche no encontrado"));
            
            // Verify that the car belongs to the dealer
            if (!car.getDealer().getId().equals(dealerId)) {
                log.error("Unauthorized attempt to edit car {} by dealer {}", id, dealerId);
                redirectAttributes.addFlashAttribute("error", "No tienes permisos para editar este listado");
                return "redirect:/dashboard/listings";
            }
            
            // Convert Car to CarDTO
            CarDTO carDTO = convertToDTO(car);
            
            model.addAttribute("carDTO", carDTO);
            model.addAttribute("carId", id);
            model.addAttribute("isEdit", true);
            addEnumsToModel(model);
            
            return "add-listing";
            
        } catch (Exception e) {
            log.error("Error loading car for edit", e);
            redirectAttributes.addFlashAttribute("error", "Error al cargar el listado: " + e.getMessage());
            return "redirect:/dashboard/listings";
        }
    }

    /**
     * Process edit listing form
     */
    @PostMapping("/listings/edit/{id}")
    public String editListing(
            @PathVariable Long id,
            @Valid @ModelAttribute CarDTO carDTO,
            BindingResult bindingResult,
            @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        // TEMPORARY: For development without authentication, use first dealer
        Long dealerId;
        if (authentication == null) {
            log.info("Editing listing without authentication (development mode)");
            dealerId = 1L;
        } else {
            String email = authentication.getName();
            User user = userService.getUserByEmail(email);
            Dealer dealer = dealerService.getDealerByUserId(user.getId());
            
            if (dealer == null) {
                log.error("No dealer found for user: {}", email);
                redirectAttributes.addFlashAttribute("error", "No se encontró el concesionario asociado");
                return "redirect:/dashboard";
            }
            dealerId = dealer.getId();
        }
        
        // Validate form
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors in edit listing form");
            model.addAttribute("carDTO", carDTO);
            model.addAttribute("carId", id);
            model.addAttribute("isEdit", true);
            addEnumsToModel(model);
            return "add-listing";
        }
        
        try {
            // Handle new image uploads
            if (imageFiles != null && !imageFiles.isEmpty()) {
                FileUploadUtil fileUploadUtil = new FileUploadUtil();
                List<String> newImageUrls = new ArrayList<>();
                
                for (MultipartFile file : imageFiles) {
                    if (!file.isEmpty()) {
                        String fileName = fileUploadUtil.saveFile(file);
                        newImageUrls.add(fileName);
                    }
                }
                
                // Combine existing images with new ones
                if (carDTO.getExistingImages() == null) {
                    carDTO.setExistingImages(new ArrayList<>());
                }
                carDTO.getExistingImages().addAll(newImageUrls);
            }
            
            // Update car
            Car car = carService.updateCar(id, carDTO, dealerId);
            log.info("Car updated successfully with id: {}", car.getId());
            
            redirectAttributes.addFlashAttribute("success", "Listado actualizado exitosamente");
            return "redirect:/dashboard/listings";
            
        } catch (IOException e) {
            log.error("Error uploading images", e);
            redirectAttributes.addFlashAttribute("error", "Error al subir las imágenes: " + e.getMessage());
            model.addAttribute("carDTO", carDTO);
            model.addAttribute("carId", id);
            model.addAttribute("isEdit", true);
            addEnumsToModel(model);
            return "add-listing";
        } catch (Exception e) {
            log.error("Error updating car", e);
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el listado: " + e.getMessage());
            model.addAttribute("carDTO", carDTO);
            model.addAttribute("carId", id);
            model.addAttribute("isEdit", true);
            addEnumsToModel(model);
            return "add-listing";
        }
    }

    /**
     * Delete listing
     */
    @PostMapping("/listings/delete/{id}")
    public String deleteListing(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        // TEMPORARY: For development without authentication, use first dealer
        Long dealerId;
        if (authentication == null) {
            log.info("Deleting listing without authentication (development mode)");
            dealerId = 1L;
        } else {
            String email = authentication.getName();
            User user = userService.getUserByEmail(email);
            Dealer dealer = dealerService.getDealerByUserId(user.getId());
            
            if (dealer == null) {
                log.error("No dealer found for user: {}", email);
                redirectAttributes.addFlashAttribute("error", "No se encontró el concesionario asociado");
                return "redirect:/dashboard";
            }
            dealerId = dealer.getId();
        }
        
        try {
            carService.deleteCar(id, dealerId);
            log.info("Car deleted successfully with id: {}", id);
            
            redirectAttributes.addFlashAttribute("success", "Listado eliminado exitosamente");
            return "redirect:/dashboard/listings";
            
        } catch (Exception e) {
            log.error("Error deleting car", e);
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el listado: " + e.getMessage());
            return "redirect:/dashboard/listings";
        }
    }

    // Helper methods

    /**
     * Add enums to model for dropdowns
     */
    private void addEnumsToModel(Model model) {
        model.addAttribute("conditions", CarCondition.values());
        model.addAttribute("bodyTypes", BodyType.values());
        model.addAttribute("fuelTypes", FuelType.values());
        model.addAttribute("transmissions", TransmissionType.values());
        
        // Add predefined features list
        List<String> features = List.of(
            "Multi-zone A/C",
            "Adaptive Cruise Control",
            "Sunroof",
            "Heated front seats",
            "Cooled Seats",
            "Panoramic roof",
            "Navigation system",
            "Keyless Start",
            "Bluetooth",
            "Antilock brakes",
            "Android Auto"
        );
        model.addAttribute("availableFeatures", features);
    }

    /**
     * Convert Car entity to CarDTO
     */
    private CarDTO convertToDTO(Car car) {
        CarDTO dto = new CarDTO();
        dto.setBrand(car.getMake());
        dto.setModel(car.getModel());
        dto.setYear(car.getYear());
        dto.setPrice(car.getPrice());
        dto.setMileage(car.getMileage());
        dto.setColor(car.getColor());
        dto.setDoors(car.getDoors());
        dto.setEngine(car.getEngine());
        dto.setDescription(car.getDescription());
        
        if (car.getFuelType() != null) {
            dto.setFuelType(car.getFuelType().name());
        }
        if (car.getTransmission() != null) {
            dto.setTransmission(car.getTransmission().name());
        }
        if (car.getBodyType() != null) {
            dto.setBodyType(car.getBodyType().name());
        }
        if (car.getCondition() != null) {
            dto.setCondition(car.getCondition().name());
        }
        
        dto.setFeatures(car.getFeatures());
        dto.setExistingImages(car.getImages());
        
        return dto;
    }
}