package com.cardealer.service;

import com.cardealer.exception.ResourceNotFoundException;
import com.cardealer.model.Car;
import com.cardealer.model.Comment;
import com.cardealer.model.User;
import com.cardealer.repository.CarRepository;
import com.cardealer.repository.CommentRepository;
import com.cardealer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    /**
     * Add a comment to a car
     */
    @Transactional
    public Comment addComment(Comment comment) {
        log.info("Adding comment to car: {}", comment.getCar().getId());
        
        // Validate content is not empty
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido del comentario no puede estar vacío");
        }
        
        // Validate car exists
        if (comment.getCar() != null && comment.getCar().getId() != null) {
            Car car = carRepository.findById(comment.getCar().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Coche no encontrado"));
            comment.setCar(car);
        }
        
        // Validate user exists
        if (comment.getUser() != null && comment.getUser().getId() != null) {
            User user = userRepository.findById(comment.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
            comment.setUser(user);
        }
        
        Comment savedComment = commentRepository.save(comment);
        log.info("Comment added successfully with id: {}", savedComment.getId());
        
        return savedComment;
    }

    /**
     * Add a comment with explicit IDs
     */
    @Transactional
    public Comment addComment(Long carId, Long userId, String content, Integer rating) {
        log.info("Adding comment to car {} by user {}", carId, userId);
        
        // Validate content
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido del comentario no puede estar vacío");
        }
        
        // Validate car exists
        Car car = carRepository.findById(carId)
            .orElseThrow(() -> new ResourceNotFoundException("Coche no encontrado con id: " + carId));
        
        // Validate user exists
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + userId));
        
        Comment comment = new Comment();
        comment.setCar(car);
        comment.setUser(user);
        comment.setContent(content);
        comment.setRating(rating);
        
        Comment savedComment = commentRepository.save(comment);
        log.info("Comment added successfully with id: {}", savedComment.getId());
        
        return savedComment;
    }

    /**
     * Get all comments for a car
     */
    public List<Comment> getCarComments(Long carId) {
        log.info("Fetching comments for car: {}", carId);
        return commentRepository.findByCarIdOrderByCreatedAtDesc(carId);
    }

    /**
     * Get comment count for a car
     */
    public Long getCommentCount(Long carId) {
        log.info("Fetching comment count for car: {}", carId);
        return commentRepository.countByCarId(carId);
    }

    /**
     * Get comment by ID
     */
    public Comment getCommentById(Long id) {
        log.info("Fetching comment with id: {}", id);
        return commentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Comentario no encontrado con id: " + id));
    }

    /**
     * Delete a comment
     */
    @Transactional
    public void deleteComment(Long id) {
        log.info("Deleting comment with id: {}", id);
        
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Comentario no encontrado con id: " + id));
        
        commentRepository.delete(comment);
        log.info("Comment deleted successfully: {}", id);
    }
}