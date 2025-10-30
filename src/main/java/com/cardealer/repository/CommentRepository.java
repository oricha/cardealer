package com.cardealer.repository;

import com.cardealer.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByCarIdOrderByCreatedAtDesc(Long carId);
    Long countByCarId(Long carId);
}


