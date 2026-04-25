package com.sahil.chatapp.repository;

import com.sahil.chatapp.dto.ContactResponse;
import com.sahil.chatapp.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    @Query("""
            SELECT
                c.id AS id,
                c.name AS name,
                cu.id AS userId,
                cu.name AS userName,
                cu.phoneNumber AS userPhoneNumber
            FROM
                Contact c
            JOIN
                c.contactUser cu
            WHERE
                c.owner.id = :ownerId
            """)
    List<ContactProjection> findByOwnerId(@Param("ownerId") Long ownerId);

    interface ContactProjection {
        Long getId();
        String getName();
        Long getUserId();
        String getUserName();
        String getUserPhoneNumber();
    }

    @Query("""
            FROM
                Contact c
            JOIN FETCH
                c.contactUser cu
            WHERE
                c.owner.id = :ownerId
                AND
                c.id = :id
            """)
    Optional<Contact> findByIdAndOwnerId(@Param("id") Long id, @Param("ownerId") Long ownerId);

    @Modifying
    @Query("""
            DELETE FROM
                Contact c
            WHERE
                c.id = :id
                AND
                c.owner.id = :ownerId
            """)
    int deleteByIdAndOwnerId(@Param("id") Long id, @Param("ownerId") Long ownerId);
}
