package com.cardealer.controller;

import com.cardealer.dto.ContactFormDTO;
import com.cardealer.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    public String submitContactForm(
            @Valid @ModelAttribute("contactForm") ContactFormDTO contactForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("contactForm", contactForm);
            return "contact";
        }

        contactService.saveContactForm(contactForm);
        
        redirectAttributes.addFlashAttribute("successMessage", 
            "Hemos recibido tu mensaje. Te responderemos lo antes posible.");
        
        return "redirect:/contact";
    }
}
