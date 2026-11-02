package com.fitness.repository;

import com.fitness.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * ===================================================================
 * [PRESENTATION POINT] REPOSITORY LAYER: UserRepository
 * ===================================================================
 * - ROLE: Communicates directly with the SQL database for User records.
 * - CONCEPTS:
 *   1. `JpaRepository<User, Long>`: Spring Data interface providing built-in methods
 *      like `save()`, `findAll()`, `findById()`, and `deleteById()`.
 *   2. `@Repository`: Identifies this interface as a Data Access Bean in Spring.
 *   3. `@Query(nativeQuery = true)`: Executes raw SQL queries against the database
 *      instead of generated ORM queries, giving full control over SQL performance.
 *   4. `@Param`: Securely binds Java method arguments into SQL queries (`:param`),
 *      preventing SQL Injection attacks.
 * ===================================================================
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * [QUERY 1] findByEmail
     * - PURPOSE: Looks up a user account by their unique email address.
     * - SQL: SELECT * FROM users WHERE email = :email LIMIT 1
     * - RETURN: Optional<User> (avoids NullPointerException if user is not found).
     */
    @Query(value = "SELECT * FROM users WHERE email = :email LIMIT 1", nativeQuery = true)
    Optional<User> findByEmail(@Param("email") String email);
    
    /**
     * [QUERY 2] existsByEmail
     * - PURPOSE: Checks if an email is already registered before creating a new account.
     * - SQL: SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END FROM users WHERE email = :email
     * - RETURN: boolean (true if email exists, false otherwise).
     */
    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END FROM users WHERE email = :email", nativeQuery = true)
    boolean existsByEmail(@Param("email") String email);
}

