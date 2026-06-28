package com.sahil.chatapp.repository;

import com.sahil.chatapp.model.Message;
import com.sahil.chatapp.model.MessageType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("""
            SELECT
                m.id AS id,
                m.content AS content,
                m.conversation.id AS conversationId,
                m.sender.id AS senderId,
                m.createdAt AS createdAt,
                m.type AS type
            FROM
                Message m
            WHERE
                m.conversation.id = :conversationId
            ORDER BY
                m.createdAt DESC
            """)
    Slice<MessageProjection> findByConversationIdOrderByCreatedAtDesc(@Param("conversationId") Long conversationId, Pageable pageable);

    interface  MessageProjection {
        Long getId();
        String getContent();
        Long getConversationId();
        Long getSenderId();
        LocalDateTime getCreatedAt();
        MessageType getType();
    }
}
