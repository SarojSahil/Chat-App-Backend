package com.sahil.chatapp.repository;

import com.sahil.chatapp.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("""
            SELECT
                c
            FROM
                Conversation c
            JOIN FETCH
                c.userOne
            JOIN FETCH
                c.userTwo
            WHERE
                c.userOne.id = :userOneId
                AND
                c.userTwo.id = :userTwoId
            """)
    Optional<Conversation> findByUsers(@Param("userOneId") Long userOneId, @Param("userTwoId") Long userTwoId);

    @Query("""
            SELECT
                c
            FROM
                Conversation c
            JOIN FETCH
                c.userOne
            JOIN FETCH
                c.userTwo
            WHERE
                c.id = :conversationId
                AND
                (c.userOne.id = :userId OR c.userTwo.id = :userId)
            """)
    Optional<Conversation> findByIdAndUser(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    @Query("""
            SELECT
                c
            FROM
                Conversation c
            JOIN FETCH
                c.userOne
            JOIN FETCH
                c.userTwo
            WHERE
                c.userOne.id = :userId
                OR
                c.userTwo.id = :userId
            """)
    List<Conversation> findByUser(@Param("userId") Long userId);
}