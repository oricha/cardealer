package com.cardealer.controller;

import com.cardealer.dto.BreadcrumbItem;
import com.cardealer.model.Car;
import com.cardealer.model.Dealer;
import com.cardealer.service.CarService;
import com.cardealer.service.DealerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/dealers")
@RequiredArgsConstructor
public class DealerController {

    private final DealerService dealerService;
    private final CarService carService;

    /**
     * List all active dealers
     */
    @GetMapping
    public String listDealers(Model model) {
        log.info("Loading dealers list");
        
        try {
            // Get all active dealers
            List<Dealer> dealers = dealerService.getAllActiveDealers();
            
            model.addAttribute("dealers", dealers);
            model.addAttribute("pageDescription", "Conoce los concesionarios registrados y explora sus vehículos activos.");
            model.addAttribute("pageKeywords", "concesionarios, vendedores coches, dealers");
            model.addAttribute("ogTitle", "Concesionarios");
            
            log.info("Loaded {} dealers", dealers.size());
            return "dealer";
            
        } catch (Exception e) {
            log.error("Error loading dealers", e);
            model.addAttribute("error", "Error al cargar los concesionarios: " + e.getMessage());
            return "dealer";
        }
    }

    /**
     * Show dealer detail page
     */
    @GetMapping("/{id}")
    public String dealerDetail(@PathVariable Long id, Model model) {
        log.info("Loading dealer detail for id: {}", id);
        
        try {
            // Get dealer
            Dealer dealer = dealerService.getDealerById(id);
            
            // Get dealer's cars
            List<Car> dealerCars = carService.getCarsByDealer(id);
            
            model.addAttribute("dealer", dealer);
            model.addAttribute("dealerCars", dealerCars);
            model.addAttribute("totalListings", dealerCars.size());
            model.addAttribute("breadcrumbItems", List.of(
                new BreadcrumbItem("Inicio", "/", false),
                new BreadcrumbItem("Concesionarios", "/dealers", false),
                new BreadcrumbItem(dealer.getName(), null, true)
            ));
            model.addAttribute("pageDescription", dealer.getDescription() != null && !dealer.getDescription().isBlank()
                ? dealer.getDescription()
                : "Consulta el perfil y los vehículos activos de " + dealer.getName() + ".");
            model.addAttribute("pageKeywords", String.join(", ",
                List.of("concesionario", dealer.getName(), "coches en venta")));
            model.addAttribute("ogTitle", dealer.getName());
            model.addAttribute("ogImage", dealer.getLogoUrl() != null && !dealer.getLogoUrl().isBlank()
                ? "/uploads/" + dealer.getLogoUrl()
                : "/img/store/logo.jpg");
            
            log.info("Loaded dealer: {} with {} cars", dealer.getName(), dealerCars.size());
            return "dealer-single";
            
        } catch (Exception e) {
            log.error("Error loading dealer detail", e);
            model.addAttribute("error", "Error al cargar el concesionario: " + e.getMessage());
            return "404";
        }
    }
}
