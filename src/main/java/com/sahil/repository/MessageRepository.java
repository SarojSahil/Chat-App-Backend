package com.sahil.repository;

import com.sahil.model.Message;
import com.sahil.model.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findBySenderIdOrReceiverId(Long senderId, Long receiverId);
    List<Message> findByReceiverIdAndStatus(Long receiverId, MessageStatus status);
    List<Message> findBySenderIdAndReceiverIdAndStatusNot(Long senderId, Long receiverId, MessageStatus status);
}
