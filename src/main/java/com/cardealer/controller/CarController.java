package com.cardealer.controller;

import com.cardealer.dto.CarFilterDTO;
import com.cardealer.dto.MessageDTO;
import com.cardealer.model.Car;
import com.cardealer.model.Comment;
import com.cardealer.model.enums.BodyType;
import com.cardealer.model.enums.CarCondition;
import com.cardealer.model.enums.FuelType;
import com.cardealer.model.enums.TransmissionType;
import com.cardealer.service.CarService;
import com.cardealer.service.CommentService;
import com.cardealer.service.FavoriteService;
import com.cardealer.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;
    private final CommentService commentService;
    private final FavoriteService favoriteService;
    private final UserService userService;

    /**
     * List cars with filters and pagination
     */
    @GetMapping
    public String listCars(
            @ModelAttribute CarFilterDTO filters,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {
        
        // Create pageable
        Pageable pageable = PageRequest.of(page, size);
        
        // Get filtered cars
        Page<Car> carsPage = carService.findCarsWithFilters(filters, pageable);
        
        // Get all unique brands from database for filter dropdown
        List<String> availableBrands = carService.getAllCars().stream()
            .map(Car::getMake)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        
        // Add data to model
        model.addAttribute("cars", carsPage);
        model.addAttribute("filters", filters);
        model.addAttribute("availableBrands", availableBrands);
        model.addAttribute("fuelTypes", Arrays.asList(FuelType.values()));
        model.addAttribute("transmissionTypes", Arrays.asList(TransmissionType.values()));
        model.addAttribute("bodyTypes", Arrays.asList(BodyType.values()));
        model.addAttribute("conditions", Arrays.asList(CarCondition.values()));
        
        // Pagination info
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", carsPage.getTotalPages());
        model.addAttribute("totalItems", carsPage.getTotalElements());
        
        return "inventory-grid";
    }

    /**
     * Car detail page
     */
    @GetMapping("/{id}")
    public String carDetail(@PathVariable Long id, Model model, Authentication authentication) {
        // Get car and increment views
        Car car = carService.getCarById(id);
        model.addAttribute("car", car);
        
        // Get related cars (same brand)
        List<Car> relatedCars = carService.getRelatedCars(id);
        model.addAttribute("relatedCars", relatedCars);
        
        // Get comments for this car
        List<Comment> comments = commentService.getCarComments(id);
        model.addAttribute("comments", comments);
        model.addAttribute("commentCount", commentService.getCommentCount(id));
        
        // Add message DTO for contact form
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setCarId(id);
        if (car.getDealer() != null && car.getDealer().getUser() != null) {
            messageDTO.setReceiverId(car.getDealer().getUser().getId());
        }
        model.addAttribute("messageDTO", messageDTO);
        
        // Check if car is in user's favorites (if authenticated)
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            var user = userService.getUserByEmail(email);
            boolean isFavorite = favoriteService.isFavorite(user.getId(), id);
            model.addAttribute("isFavorite", isFavorite);
        } else {
            model.addAttribute("isFavorite", false);
        }
        
        return "inventory-single";
    }

    /**
     * Compare cars page
     */
    @GetMapping("/compare")
    public String compareCars(@RequestParam(required = false) List<Long> ids, Model model) {
        if (ids == null || ids.isEmpty()) {
            // Redirect to cars list if no IDs provided
            return "redirect:/cars";
        }
        
        // Limit to maximum 3 cars
        if (ids.size() > 3) {
            ids = ids.subList(0, 3);
        }
        
        // Get cars by IDs
        List<Car> carsToCompare = ids.stream()
            .map(carService::getCarById)
            .collect(Collectors.toList());
        
        model.addAttribute("cars", carsToCompare);
        
        return "compare";
    }
}