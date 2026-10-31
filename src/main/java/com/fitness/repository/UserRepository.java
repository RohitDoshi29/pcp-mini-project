package com.fitness.repository;

import com.fitness.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for User entity.
 * 
 * Spring Boot Concept:
 * - JpaRepository provides ready-to-use CRUD methods:
 *   save(), findById(), findAll(), deleteById(), etc.
 * - Spring automatically generates the implementation at runtime!
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
}

