package com.cardealer.service;

import com.cardealer.dto.MessageDTO;
import com.cardealer.exception.ResourceNotFoundException;
import com.cardealer.model.Car;
import com.cardealer.model.Message;
import com.cardealer.model.User;
import com.cardealer.repository.CarRepository;
import com.cardealer.repository.MessageRepository;
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
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;

    /**
     * Send a message
     */
    @Transactional
    public Message sendMessage(MessageDTO messageDTO) {
        log.info("Sending message from user to receiver: {}", messageDTO.getReceiverId());
        
        // Validate receiver exists
        User receiver = userRepository.findById(messageDTO.getReceiverId())
            .orElseThrow(() -> new ResourceNotFoundException("Usuario destinatario no encontrado"));
        
        // Validate car exists if carId is provided
        Car car = null;
        if (messageDTO.getCarId() != null) {
            car = carRepository.findById(messageDTO.getCarId())
                .orElseThrow(() -> new ResourceNotFoundException("Coche no encontrado"));
        }
        
        // Note: sender will be set by the controller from the authenticated user
        Message message = new Message();
        message.setReceiver(receiver);
        message.setCar(car);
        message.setSubject(messageDTO.getSubject());
        message.setContent(messageDTO.getContent());
        message.setRead(false);
        
        Message savedMessage = messageRepository.save(message);
        log.info("Message sent successfully with id: {}", savedMessage.getId());
        
        return savedMessage;
    }

    /**
     * Send a message with sender
     */
    @Transactional
    public Message sendMessage(Long senderId, MessageDTO messageDTO) {
        log.info("Sending message from user {} to receiver: {}", senderId, messageDTO.getReceiverId());
        
        // Validate sender exists
        User sender = userRepository.findById(senderId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario remitente no encontrado"));
        
        // Validate receiver exists
        User receiver = userRepository.findById(messageDTO.getReceiverId())
            .orElseThrow(() -> new ResourceNotFoundException("Usuario destinatario no encontrado"));
        
        // Validate car exists if carId is provided
        Car car = null;
        if (messageDTO.getCarId() != null) {
            car = carRepository.findById(messageDTO.getCarId())
                .orElseThrow(() -> new ResourceNotFoundException("Coche no encontrado"));
        }
        
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setCar(car);
        message.setSubject(messageDTO.getSubject());
        message.setContent(messageDTO.getContent());
        message.setRead(false);
        
        Message savedMessage = messageRepository.save(message);
        log.info("Message sent successfully with id: {}", savedMessage.getId());
        
        return savedMessage;
    }

    /**
     * Get received messages for a user
     */
    public List<Message> getReceivedMessages(Long userId) {
        log.info("Fetching received messages for user: {}", userId);
        return messageRepository.findByReceiverIdOrderBySentAtDesc(userId);
    }

    /**
     * Get sent messages for a user
     */
    public List<Message> getSentMessages(Long userId) {
        log.info("Fetching sent messages for user: {}", userId);
        return messageRepository.findBySenderIdOrderBySentAtDesc(userId);
    }

    /**
     * Get message by ID
     */
    public Message getMessageById(Long id) {
        log.info("Fetching message with id: {}", id);
        return messageRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Mensaje no encontrado con id: " + id));
    }

    /**
     * Mark message as read
     */
    @Transactional
    public void markAsRead(Long messageId) {
        log.info("Marking message as read: {}", messageId);
        
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new ResourceNotFoundException("Mensaje no encontrado con id: " + messageId));
        
        message.setRead(true);
        messageRepository.save(message);
        
        log.info("Message marked as read: {}", messageId);
    }

    /**
     * Get unread message count for a user
     */
    public Long getUnreadCount(Long userId) {
        log.info("Fetching unread message count for user: {}", userId);
        return messageRepository.countByReceiverIdAndReadFalse(userId);
    }
}