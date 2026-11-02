package com.fitness.service;

import com.fitness.model.User;
import com.fitness.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * ===================================================================
 * [PRESENTATION POINT] SERVICE LAYER: UserService
 * ===================================================================
 * - ROLE: Handles core business rules for user management.
 * - CONCEPTS:
 *   1. `@Service`: Registers this class as a Spring Service Bean in the Application Context.
 *   2. Validation: Ensures business constraints, such as duplicate email prevention.
 *   3. Exception Handling: Throws `ResponseStatusException` (e.g. 400 Bad Request, 404 Not Found)
 *      to return clean HTTP error responses to the client.
 * ===================================================================
 */
@Service
public class UserService {

    // Dependency Injection: Spring automatically injects UserRepository
    private final UserRepository userRepository;

    // Constructor Injection (Best practice in Spring Boot)
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * [FUNCTION 1] createUser
     * - PURPOSE: Registers a new user with duplicate email protection.
     * - CODE FLOW:
     *   Step 1: Check if the email already exists in the database.
     *   Step 2: If found, reject with HTTP 400 BAD REQUEST error.
     *   Step 3: If unique, save the user entity to the SQL database.
     *   Step 4: Return the saved user (with generated ID).
     */
    public User createUser(User user) {
        // Step 1: Duplicate email check via repository
        if (userRepository.existsByEmail(user.getEmail())) {
            // Step 2: Guard clause - reject duplicates
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }
        // Step 3 & 4: Persist in database and return saved record
        return userRepository.save(user);
    }

    /**
     * [FUNCTION 2] getAllUsers
     * - PURPOSE: Retrieves all registered users from the database.
     * - CODE FLOW:
     *   Calls UserRepository.findAll() to run "SELECT * FROM users".
     *   Used by frontend dropdown so user can switch profiles.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * [FUNCTION 3] getUserById
     * - PURPOSE: Retrieves a specific user by primary key ID.
     * - CODE FLOW:
     *   Step 1: Queries database with findById(id).
     *   Step 2: If user does not exist, throws HTTP 404 NOT FOUND error.
     *   Step 3: Returns the User object if present.
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}

