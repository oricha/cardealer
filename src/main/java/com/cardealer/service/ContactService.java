package com.cardealer.service;

import com.cardealer.model.ContactForm;
import com.cardealer.repository.ContactFormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContactService {

    private final ContactFormRepository contactFormRepository;

    /**
     * Get all contact forms
     */
    public List<ContactForm> getAllContactForms() {
        return contactFormRepository.findAllByOrderBySubmittedAtDesc();
    }

    /**
     * Get contact form by ID
     */
    public Optional<ContactForm> getContactFormById(Long id) {
        return contactFormRepository.findById(id);
    }

    /**
     * Get contact forms by email
     */
    public List<ContactForm> getContactFormsByEmail(String email) {
        return contactFormRepository.findByEmail(email);
    }

    /**
     * Save a contact form
     */
    @Transactional
    public ContactForm saveContactForm(ContactForm contactForm) {
        return contactFormRepository.save(contactForm);
    }

    /**
     * Delete a contact form
     */
    @Transactional
    public void deleteContactForm(Long id) {
        contactFormRepository.deleteById(id);
    }

    /**
     * Get total count of contact forms
     */
    public long getTotalContactFormCount() {
        return contactFormRepository.count();
    }
}