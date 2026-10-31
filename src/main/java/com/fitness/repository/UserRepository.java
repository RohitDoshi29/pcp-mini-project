package com.fitness.repository;

import com.fitness.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for User entity using explicit Native SQL queries.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Native SQL: Find user by email
    @Query(value = "SELECT * FROM users WHERE email = :email LIMIT 1", nativeQuery = true)
    Optional<User> findByEmail(@Param("email") String email);
    
    // check email exists by returning true or false
    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END FROM users WHERE email = :email", nativeQuery = true)
    boolean existsByEmail(@Param("email") String email);
}
