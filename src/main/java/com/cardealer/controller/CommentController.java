package com.cardealer.controller;

import com.cardealer.model.User;
import com.cardealer.service.CommentService;
import com.cardealer.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/comments")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    /**
     * Add a comment to a car
     */
    @PostMapping("/add")
    public String addComment(
            @RequestParam Long carId,
            @RequestParam String content,
            @RequestParam(required = false) Integer rating,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        log.info("Adding comment to car: {}", carId);
        
        try {
            // Get authenticated user
            String email = authentication.getName();
            User user = userService.getUserByEmail(email);
            
            // Validate content
            if (content == null || content.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El comentario no puede estar vacío");
                return "redirect:/cars/" + carId;
            }
            
            // Add comment
            commentService.addComment(carId, user.getId(), content, rating);
            
            log.info("Comment added successfully");
            redirectAttributes.addFlashAttribute("success", "Comentario añadido exitosamente");
            
            return "redirect:/cars/" + carId;
            
        } catch (Exception e) {
            log.error("Error adding comment", e);
            redirectAttributes.addFlashAttribute("error", "Error al añadir el comentario: " + e.getMessage());
            return "redirect:/cars/" + carId;
        }
    }

    /**
     * Delete a comment
     */
    @PostMapping("/delete/{id}")
    public String deleteComment(
            @PathVariable Long id,
            @RequestParam Long carId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        log.info("Deleting comment: {}", id);
        
        try {
            // Get authenticated user
            String email = authentication.getName();
            User user = userService.getUserByEmail(email);
            
            // Get comment and verify ownership
            var comment = commentService.getCommentById(id);
            
            if (!comment.getUser().getId().equals(user.getId())) {
                log.error("Unauthorized attempt to delete comment {} by user {}", id, email);
                redirectAttributes.addFlashAttribute("error", "No tienes permisos para eliminar este comentario");
                return "redirect:/cars/" + carId;
            }
            
            // Delete comment
            commentService.deleteComment(id);
            
            log.info("Comment deleted successfully");
            redirectAttributes.addFlashAttribute("success", "Comentario eliminado exitosamente");
            
            return "redirect:/cars/" + carId;
            
        } catch (Exception e) {
            log.error("Error deleting comment", e);
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el comentario: " + e.getMessage());
            return "redirect:/cars/" + carId;
        }
    }
}