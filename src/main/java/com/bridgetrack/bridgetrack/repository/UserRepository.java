package com.bridgetrack.bridgetrack.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgetrack.bridgetrack.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);
    
    Optional<User> findByEntityTypeAndEntityId(String entityType, Long entityId);

}