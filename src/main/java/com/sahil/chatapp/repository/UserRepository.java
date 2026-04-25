package com.sahil.chatapp.repository;

import com.sahil.chatapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<UserProjection> findUserMinimalByPhoneNumber(String phoneNumber);

    interface UserProjection{
        Long getId();
        String getName();
        String getPhoneNumber();
    }
}
