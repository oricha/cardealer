package com.cardealer.repository;

import com.cardealer.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByReceiverIdOrderBySentAtDesc(Long receiverId);
    List<Message> findBySenderIdOrderBySentAtDesc(Long senderId);
    Page<Message> findByReceiverIdOrderBySentAtDesc(Long receiverId, Pageable pageable);
    Long countByReceiverIdAndReadFalse(Long receiverId);
}

