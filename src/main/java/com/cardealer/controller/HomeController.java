package com.cardealer.controller;

import com.cardealer.model.enums.BodyType;
import com.cardealer.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CarService carService;

    @GetMapping("/")
    public String home(Model model) {
        // Load latest 8 cars
        model.addAttribute("latestCars", carService.getLatestCars());
        model.addAttribute("totalCars", carService.getTotalCarCount());
        
        // Load body type categories
        List<BodyType> bodyTypes = Arrays.asList(BodyType.values());
        model.addAttribute("bodyTypes", bodyTypes);
        
        return "index";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
}