package com.cardealer.repository;

import com.cardealer.model.ContactForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactFormRepository extends JpaRepository<ContactForm, Long> {
    
    // Find contact forms by email
    List<ContactForm> findByEmail(String email);
    
    // Find all contact forms ordered by submission date descending (newest first)
    List<ContactForm> findAllByOrderBySubmittedAtDesc();
}