package com.sahil.chatapp.repository;

import com.sahil.chatapp.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    List<Contact> findByOwnerId(Long ownerId);

    boolean existsByOwnerIdAndContactUserId(Long ownerId, Long contactUserId);

    Optional<Contact> findByIdAndOwnerId(Long id,Long ownerId);
}
